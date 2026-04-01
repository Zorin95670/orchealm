<template>
  <q-page v-bind="uiProps.page">
    <team-environments-manager-card
      v-if="isEditor"
      :environments="environments"
      :i18n-scope="localI18nScope"
      :loading="loading"
      :name-space="localNameSpace"
      :team-key="team?.key"
      @delete-environment="openDeleteEnvironmentDialog"
      @reload-environments="loadEnvironments"
      @update-order="updateEnvironments"
    />
    <team-environments-card
      v-else
      :environments="environments"
      :i18n-scope="localI18nScope"
      :loading="loading"
      :name-space="localNameSpace"
    />
    <projects-card
      :i18n-scope="localI18nScope"
      :is-editor="isEditor"
      :name-space="localNameSpace"
      :team-key="team?.key"
    />
    <team-visibility-card
      :i18n-scope="localI18nScope"
      :name-space="localNameSpace"
      :team="team"
      @update:visibility="updateVisibility"
    />
  </q-page>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useUiDesign } from "src/composables/useUiDesign.js";
import UiEvent from "src/events/UiEvent.js";
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { getTeam } from "src/services/TeamService.js";
import * as EnvironmentService from "src/services/EnvironmentService.js";
import TeamEnvironmentsCard from "components/card/TeamEnvironmentsCard.vue";
import TeamEnvironmentsManagerCard from "components/card/TeamEnvironmentsManagerCard.vue";
import { useAcl } from "vue-simple-acl";
import ProjectsCard from "components/card/ProjectsCard.vue";
import { useUiStore } from "stores/uiStore.js";
import TeamVisibilityCard from "components/card/TeamVisibilityCard.vue";

const route = useRoute();
const router = useRouter();
const {t} = useI18n();
const {ui} = useUiDesign();
const acl = useAcl();
const uiStore = useUiStore();

const localNameSpace = 'Pages.TeamDetailPage';
const localI18nScope = 'Pages.TeamDetailPage';

const uiProps = {
  page: ui(localNameSpace, "q-page"),
  envCardActions: ui(`${localNameSpace}.EnvironmentCard`, 'q-card-actions'),
  envEdit: ui(`${localNameSpace}.EnvironmentCard`, 'edit', 'q-btn'),
  envSave: ui(`${localNameSpace}.EnvironmentCard`, 'save', 'q-btn'),
  envCancel: ui(`${localNameSpace}.EnvironmentCard`, 'cancel', 'q-btn'),
  envInputName: ui(`${localNameSpace}.EnvironmentCard`, 'name', 'q-input'),
  envInputShortName: ui(`${localNameSpace}.EnvironmentCard`, 'shortName', 'q-input'),
};
const team = ref(null);
const environments = ref([]);
const loading = ref(true);

const isEditor = computed(() => acl.can('edit-team', {teamKey: team.value?.key}));

async function loadTeam() {
  return getTeam(route.params.key).then(data => {
    team.value = data;
  }).catch(() => {
    router.push('/teams');
  });
}

async function loadEnvironments() {
  return EnvironmentService.getEnvironmentsByTeamKey(team.value.key)
    .then(data => {
      environments.value = data.map((environment) => ({...environment, loading: false}));
    });
}

async function updateEnvironments(newEnvironments) {
  const data = newEnvironments
    .map((env, index) => ({
      ...env,
      newPosition: index
    }))
    .filter(env => env.position !== env.newPosition)
    .map(({id, teamKey, name, shortName, newPosition}) => ({
      id,
      teamKey,
      name,
      shortName,
      position: newPosition,
    }));

  if (data.length === 0) {
    return;
  }

  return Promise.allSettled(data.map((environment) => EnvironmentService.updateEnvironment(environment.id, environment)))
    .then(loadEnvironments);
}

function openDeleteEnvironmentDialog(environment) {
  return UiEvent.next({
    key: 'ConfirmationDialog',
    data: {
      type: 'open',
      nameSpace: `${localNameSpace}.DeleteEnvironmentDialog`,
      i18nScope: `${localI18nScope}.DeleteEnvironmentDialog`,
      title: t(`${localI18nScope}.DeleteEnvironmentDialog.title`),
      content: t(`${localI18nScope}.DeleteEnvironmentDialog.content`, environment),
      onConfirm: () => deleteEnvironment(environment),
    },
  })
}

function deleteEnvironment(environment) {
  return EnvironmentService.deleteEnvironment(environment.id)
    .then(loadEnvironments);
}

function updateVisibility(visibility) {
  team.value.isPublic = visibility;
}

onMounted(() => {
  uiStore.setTitle('Pages.TeamDetailPage.title', null, true);
  UiEvent.next({
    key: "loading",
    value: loading.value
  });

  loadTeam()
    .then(loadEnvironments)
    .finally(() => {
      loading.value = false;
      uiStore.setTitle('Pages.TeamDetailPage.title', team.value.name, false);

      UiEvent.next({
        key: "loading",
        value: loading.value
      });
    });
});
</script>

<style lang="scss" scoped>
.environment-card {
  cursor: grab;
}

.ghost {
  opacity: 0.4;
}

.chosen {
  transform: scale(1.02);
}

.dragging {
  cursor: grabbing;
}
</style>
