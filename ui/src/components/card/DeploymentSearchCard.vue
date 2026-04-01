<template>
  <q-card v-bind="uiProps.card">
    <q-card-section v-bind="uiProps.cardSection">
      <q-select
        v-model="projects"
        :hint="t(`${localI18nScope}.project.hint`, '')"
        :label="t(`${localI18nScope}.project.label`, '')"
        :options="projectOptions"
        :prefix="t(`${localI18nScope}.project.prefix`, '')"
        :suffix="t(`${localI18nScope}.project.suffix`, '')"
        multiple
        use-input
        v-bind="uiProps.projectSelect"
        @clear="filter"
        @filter="searchProject"
        @update:model-value="filter"
      />
      <q-select
        v-model="clients"
        :hint="t(`${localI18nScope}.client.hint`, '')"
        :label="t(`${localI18nScope}.client.label`, '')"
        :options="clientOptions"
        :prefix="t(`${localI18nScope}.client.prefix`, '')"
        :suffix="t(`${localI18nScope}.client.suffix`, '')"
        multiple
        use-input
        v-bind="uiProps.clientSelect"
        @clear="filter"
        @filter="searchClient"
        @update:model-value="filter"
      />
      <q-select
        v-model="environments"
        :hint="t(`${localI18nScope}.environment.hint`, '')"
        :label="t(`${localI18nScope}.environment.label`, '')"
        :options="environmentOptions"
        :prefix="t(`${localI18nScope}.environment.prefix`, '')"
        :suffix="t(`${localI18nScope}.environment.suffix`, '')"
        multiple
        use-input
        v-bind="uiProps.environmentSelect"
        @clear="filter"
        @filter="searchEnvironment"
        @update:model-value="filter"
      />
      <q-select
        v-model="states"
        :hint="t(`${localI18nScope}.state.hint`, '')"
        :label="t(`${localI18nScope}.state.label`, '')"
        :options="stateOptions"
        :prefix="t(`${localI18nScope}.state.prefix`, '')"
        :suffix="t(`${localI18nScope}.state.suffix`, '')"
        multiple
        use-input
        v-bind="uiProps.stateSelect"
        @clear="filter"
        @update:model-value="filter"
      />
    </q-card-section>
  </q-card>
</template>

<script setup>
import { computed, ref } from "vue";
import { getClients } from "src/services/ClientService.js";
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";
import { getEnvironments } from "src/services/EnvironmentService.js";
import { getProjects } from "src/services/ProjectService.js";

const props = defineProps({
  modelValue: Object,
  teamKey: String,
  i18nScope: String,
  nameSpace: String,
});
const emits = defineEmits(['update:model-value']);

const {t} = useI18n();
const {ui} = useUiDesign();
const clients = ref([]);
const clientOptions = ref([]);
const environments = ref([]);
const environmentOptions = ref([]);
const projects = ref([]);
const projectOptions = ref([]);
const states = ref([]);
const stateOptions = computed(() => [{
  label: t('DeploymentStatusBadge.new'),
  value: 'NEW'
}, {
  label: t('DeploymentStatusBadge.hot'),
  value: 'HOT'
}]);

const localI18nScope = computed(() => `${props.nameSpace}.DeploymentSearchCard`);
const localNameSpace = computed(() => `${props.i18nScope}.DeploymentSearchCard`);

const uiProps = computed(() => ({
  card: ui(localNameSpace.value, 'q-card'),
  cardSection: ui(localNameSpace.value, 'q-card-section'),
  clientSelect: ui(localNameSpace.value, 'client', 'q-select'),
  environmentSelect: ui(localNameSpace.value, 'environment', 'q-select'),
  projectSelect: ui(localNameSpace.value, 'project', 'q-select'),
  stateSelect: ui(localNameSpace.value, 'state', 'q-select'),
}));

function searchClient(val, update, abort) {
  return getClients({
    teamKey: props.teamKey,
    client: `lk_*${val}*`
  }).then((data) => {
    update(() => {
      clientOptions.value = data.content;
    });
  }).catch(() => {
    clientOptions.value = [];
    abort();
  });
}

function searchEnvironment(val, update, abort) {
  return getEnvironments({
    teamKey: props.teamKey,
    name: `lk_*${val}*`
  }).then((data) => {
    update(() => {
      environmentOptions.value = data.content.map((item) => item.name);
    });
  }).catch(() => {
    environmentOptions.value = [];
    abort();
  });
}

function searchProject(val, update, abort) {
  return getProjects({
    teamKey: props.teamKey,
    name: `lk_*${val}*`
  }).then((data) => {
    update(() => {
      projectOptions.value = data.content.map((item) => item.name);
    });
  }).catch(() => {
    projectOptions.value = [];
    abort();
  });
}

function filter() {
  const newFilters = {};
  if (clients.value && clients.value.length > 0) {
    newFilters.client = clients.value.join('|');
  }

  if (environments.value && environments.value.length > 0) {
    newFilters.environmentName = environments.value.join('|');
  }

  if (projects.value && projects.value.length > 0) {
    newFilters.projectName = projects.value.join('|');
  }

  if (states.value && states.value.length > 0) {
    newFilters.state = states.value.map(({value}) => value).join('|');
  }

  emits('update:model-value', newFilters);
}

</script>

<style scoped>

</style>
