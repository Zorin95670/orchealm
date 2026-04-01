<template>
  <team-environments-card
    :environments="environments"
    :i18n-scope="i18nScope"
    :loading="loading"
    :name-space="nameSpace"
  >
    <template #environments>
      <draggable
        v-model="localEnvironments"
        chosen-class="chosen"
        class="row"
        drag-class="dragging"
        ghost-class="ghost"
        item-key="id"
        @change="emits('update-order', localEnvironments)"
      >
        <template #item="{ element: environment }">
          <environment-card
            :environment="environment"
            :loading="environment.loading"
            :name-space="nameSpace"
            class="mdi-cursor-default-gesture"
          >
            <template #append>
              <q-card-actions v-if="!environment.edit" v-bind="uiProps.envCardActions">
                <q-btn
                  v-bind="uiProps.envDelete"
                  @click.prevent="emits('delete-environment', environment)"
                />
              </q-card-actions>
            </template>
            <template #title="props">
              <span
                v-if="!environment.nameEdit"
                :title="t(`${i18nScope}.TeamEnvironmentsCard.EnvironmentCard.name.title`, '')"
                style="cursor: pointer"
                v-bind="props.uiProps"
                @click="editName(environment)"
              >
                {{ environment.name }}
              </span>
              <q-input
                v-else
                ref="newNameInput"
                v-model="environment.newName"
                :hint="t(`${i18nScope}.TeamEnvironmentsCard.EnvironmentCard.name.hint`, '')"
                :label="t(`${i18nScope}.TeamEnvironmentsCard.EnvironmentCard.name.label`, '')"
                :prefix="t(`${i18nScope}.TeamEnvironmentsCard.EnvironmentCard.name.prefix`, '')"
                :suffix="t(`${i18nScope}.TeamEnvironmentsCard.EnvironmentCard.name.suffix`, '')"
                v-bind="uiProps.envInputName"
                @keyup="(event) => onNameKeyUp(environment.id, event)"
              />
            </template>
            <template #sub-title="props">
              <span
                v-if="!environment.shortNameEdit"
                :title="t(`${i18nScope}.TeamEnvironmentsCard.EnvironmentCard.shortName.title`, '')"
                style="cursor: pointer"
                v-bind="props.uiProps"
                @click="editShortName(environment)"
              >
                {{ environment.shortName }}
              </span>
              <q-input
                v-else
                ref="newShortNameInput"
                v-model="environment.newShortName"
                :hint="t(`${i18nScope}.TeamEnvironmentsCard.EnvironmentCard.shortName.hint`, '')"
                :label="t(`${i18nScope}.TeamEnvironmentsCard.EnvironmentCard.shortName.label`, '')"
                :prefix="t(`${i18nScope}.TeamEnvironmentsCard.EnvironmentCard.shortName.prefix`, '')"
                :suffix="t(`${i18nScope}.TeamEnvironmentsCard.EnvironmentCard.shortName.suffix`, '')"
                v-bind="uiProps.envInputShortName"
                @keyup="(event) => onShortNameKeyUp(environment.id, event)"
              />
            </template>
          </environment-card>
        </template>
      </draggable>
    </template>
    <template #actions>
      <q-card-actions v-if="uiProps.actions">
        <q-btn
          :label="t(`${i18nScope}.TeamEnvironmentsCard.add`, '')"
          v-bind="uiProps.add"
          @click="openAddEnvironmentDialog"
        />
      </q-card-actions>
    </template>
  </team-environments-card>
</template>

<script setup>
import TeamEnvironmentsCard from "components/card/TeamEnvironmentsCard.vue";
import { nextTick, ref, watch } from "vue";
import EnvironmentCard from "components/card/EnvironmentCard.vue";
import { useUiDesign } from "src/composables/useUiDesign.js";
import draggable from 'vuedraggable';
import { useI18n } from "vue-i18n";
import * as EnvironmentService from "src/services/EnvironmentService.js";
import UiEvent from "src/events/UiEvent.js";
import { Notify } from "quasar";

const props = defineProps({
  i18nScope: String,
  nameSpace: String,
  loading: Boolean,
  environments: {
    type: Array,
    default: () => [],
  },
  teamKey: String,
});

const emits = defineEmits([
  'update-order',
  'reload-environments',
  'delete-environment',
]);

const {t} = useI18n();
const {ui} = useUiDesign();

const localEnvironments = ref([]);
const newNameInput = ref(null);
const newShortNameInput = ref(null);

watch(() => props.environments, () => {
  localEnvironments.value = props.environments.map((environment) => ({
    edit: false,
    loading: false,
    newName: environment.name,
    newShortName: environment.shortName,
    ...environment,
  }));
});

const uiProps = {
  actions: ui(`${props.nameSpace}.TeamEnvironmentsCard`, 'q-card-actions'),
  add: ui(`${props.nameSpace}.TeamEnvironmentsCard`, 'add', 'q-btn'),
  envCardActions: ui(`${props.nameSpace}.TeamEnvironmentsCard.EnvironmentCard`, 'q-card-actions'),
  envEdit: ui(`${props.nameSpace}.TeamEnvironmentsCard.EnvironmentCard`, 'edit', 'q-btn'),
  envDelete: ui(`${props.nameSpace}.TeamEnvironmentsCard.EnvironmentCard`, 'delete', 'q-btn'),
  envSave: ui(`${props.nameSpace}.TeamEnvironmentsCard.EnvironmentCard`, 'save', 'q-btn'),
  envCancel: ui(`${props.nameSpace}.TeamEnvironmentsCard.EnvironmentCard`, 'cancel', 'q-btn'),
  envInputName: ui(`${props.nameSpace}.TeamEnvironmentsCard.EnvironmentCard`, 'name', 'q-input'),
  envInputShortName: ui(`${props.nameSpace}.TeamEnvironmentsCard.EnvironmentCard`, 'shortName', 'q-input'),
  notifySuccess: ui(`${props.nameSpace}.TeamEnvironmentsCard`, 'UpdateSuccessNotify', 'q-notify'),
  notifyError: ui(`${props.nameSpace}.TeamEnvironmentsCard`, 'UpdateErrorNotify', 'q-notify')
};

function editName(environment) {
  environment.newName = environment.name;
  environment.nameEdit = true;

  nextTick(() => {
    newNameInput.value?.focus()
  })
}

function editShortName(environment) {
  environment.newShortName = environment.shortName;
  environment.shortNameEdit = true;

  nextTick(() => {
    newShortNameInput.value?.focus()
  })
}

function onNameKeyUp(environmentId, {key}) {
  if (['Enter', 'Escape'].includes(key)) {
    const environment = localEnvironments.value.find(({id}) => id === environmentId);

    if (key === 'Escape') {
      environment.nameEdit = false;
      return;
    }

    environment.nameEdit = false;
    environment.shortNameEdit = false;

    updateEnvironment(environmentId);
  }
}

function onShortNameKeyUp(environmentId, {key}) {
  if (['Enter', 'Escape'].includes(key)) {
    const environment = localEnvironments.value.find(({id}) => id === environmentId);

    if (key === 'Escape') {
      environment.shortNameEdit = false;
      return;
    }

    environment.nameEdit = false;
    environment.shortNameEdit = false;

    updateEnvironment(environmentId);
  }
}

function updateEnvironment(environmentId) {
  const environment = localEnvironments.value.find(({id}) => id === environmentId);

  if (environment.name === environment.newName
    && environment.shortName === environment.newShortName) {
    return;
  }

  environment.loading = true;

  return EnvironmentService.updateEnvironment(environmentId, {
    name: environment.newName,
    shortName: environment.newShortName,
    position: environment.position,
    teamKey: environment.teamKey,
  }).then(() => {
    environment.edit = false;

    Notify.create({
      ...uiProps.notifySuccess,
      message: t(`${props.i18nScope}.TeamEnvironmentsCard.UpdateSuccessNotify`, '')
    });
    emits('reload-environments');
  }).catch(() => {
    Notify.create({
      ...uiProps.notifyError,
      message: t(`${props.i18nScope}.TeamEnvironmentsCard.UpdateErrorNotify`, '')
    });
  }).finally(() => {
    environment.loading = false;
  });
}

function openAddEnvironmentDialog() {
  return UiEvent.next({
    key: 'AddEnvironmentDialog',
    data: {
      type: 'open',
      position: localEnvironments.value[localEnvironments.value.length - 1]?.position + 1 || 0,
      teamKey: props.teamKey,
      onConfirm: () => emits('reload-environments'),
    },
  });
}
</script>

<style scoped>

</style>
