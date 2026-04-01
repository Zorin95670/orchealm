<template>
  <q-page v-bind="uiProps.page">
    <deployment-search-card
      v-model="filters"
      :i18n-scope="localI18nScope"
      :name-space="localNameSpace"
      :team-key="teamKey"
      @update:model-value="initData"
    />
    <deployment-table
      v-if="isValid"
      :deployments="deployments"
      :environments="environments"
      :loading="loading"
      :name-space="localNameSpace"
      :projects="projects"
    />
    <q-card v-else v-bind="uiProps.invalidCard">
      <q-card-section v-bind="uiProps.invalidCardSection">
        <q-icon v-if="uiProps.invalidIcon.name" v-bind="uiProps.invalidIcon"/>
        {{ t(`${localI18nScope}.invalid`) }}
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { useUiDesign } from "src/composables/useUiDesign.js";
import { computed, onMounted, ref } from "vue";
import UiEvent from "src/events/UiEvent.js";
import { getLatestDeployments } from "src/services/DeploymentService.js";
import DeploymentTable from "components/table/DeploymentTable.vue";
import { useRoute } from "vue-router";
import { getEnvironmentsByTeamKey } from "src/services/EnvironmentService.js";
import { getProjects } from "src/services/ProjectService.js";
import DeploymentSearchCard from "components/card/DeploymentSearchCard.vue";
import { useUiStore } from "stores/uiStore.js";
import { getTeam } from "src/services/TeamService.js";
import router from "src/router/index.js";
import { useI18n } from "vue-i18n";

const route = useRoute();
const {ui} = useUiDesign();
const {t} = useI18n();
const uiStore = useUiStore();
const loading = ref(true);
const deployments = ref({});
const projects = ref([]);
const environments = ref([]);
const filters = ref({});
const teamKey = computed(() => route.params.key);

const localNameSpace = 'Pages.LatestDeploymentsPage';
const localI18nScope = 'Pages.LatestDeploymentsPage';

const uiProps = {
  page: ui(localNameSpace, "q-page"),
  invalidCard: ui(localNameSpace, 'q-card-invalid', "q-card"),
  invalidCardSection: ui(localNameSpace, 'q-card-section-invalid', "q-card-section"),
  invalidIcon: ui(localNameSpace, 'q-icon-invalid', "q-icon")
};

const isValid = computed(() => projects.value.length > 0 && environments.value.length > 0);

async function loadData() {
  return getLatestDeployments({
    ...filters.value,
    teamKey: teamKey.value,
  }).then((data) => {
    deployments.value = [];

    const clientsByEnvironment = environments.value.reduce((acc, environment) => {
      acc.set(environment.shortName, {});
      return acc;
    }, new Map());

    data.forEach((deployment) => {
      const envClients = clientsByEnvironment.get(deployment.environmentShortName);
      if (!envClients[deployment.projectId]) {
        envClients[deployment.projectId] = [];
      }
      envClients[deployment.projectId].push(deployment.client);
    });

    environments.value = environments.value
      .map((environment) => {
        const clients = projects.value.reduce((acc, project) => {
          const rawClients = clientsByEnvironment.get(environment.shortName)[project.id] || [];
          acc[project.id] = [...new Set(rawClients)].sort((a, b) => a.localeCompare(b));
          return acc;
        }, {});

        const maxClients = Math.max(0, ...Object.values(clients).map((c) => c.length));

        return {
          ...environment,
          clients,
          maxClients,
        };
      });

    deployments.value = initTableData(data, environments.value);
  });
}

function initTableData(deploymentsData, environmentsData) {
  return deploymentsData.reduce((acc, deployment) => {
    if (!acc[deployment.projectId]) {
      acc[deployment.projectId] = {};
    }
    if (!acc[deployment.projectId][deployment.environmentShortName]) {
      acc[deployment.projectId][deployment.environmentShortName] = {};
    }
    const colorIndex = environmentsData
      .findIndex((env) => env.shortName === deployment.environmentShortName) + 1;

    acc[deployment.projectId][deployment.environmentShortName][deployment.client] = {
      ...deployment,
      color: `${deployment.projectColorR},${deployment.projectColorG},${deployment.projectColorB}`,
      colorIndex,
    };
    return acc;
  }, {});
}

function initEnvironments() {
  return getEnvironmentsByTeamKey(teamKey.value, {name: filters.value.environmentName})
    .then((data) => {
      environments.value = data.map((environment) => ({
        ...environment,
        clients: {},
        maxClients: 0,
      }))
    });
}

function initProjects() {
  return getProjects({teamKey: teamKey.value, name: filters.value.projectName})
    .then((data) => {
      projects.value = data.content;
    });
}

function initData() {
  return Promise.allSettled([
    initEnvironments(),
    initProjects(),
  ]).then(loadData);
}

async function loadTeam() {
  return getTeam(route.params.key).then(data => {
    uiStore.setTitle('Pages.LatestDeploymentsPage.title', data.name, false);
  }).catch(() => {
    router.push('/');
  });
}

onMounted(() => {
  uiStore.setTitle('Pages.LatestDeploymentsPage.title', null, true);

  UiEvent.next({
    key: "loading",
    value: loading.value
  });
  loadTeam();

  initData()
    .finally(() => {
      loading.value = false;

      UiEvent.next({
        key: "loading",
        value: loading.value
      });
    });
});
</script>

<style scoped>

</style>
