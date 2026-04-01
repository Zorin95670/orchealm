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
      <q-input
        v-model="environments"
        :hint="t(`${localI18nScope}.environment.hint`, '')"
        :label="t(`${localI18nScope}.environment.label`, '')"
        :prefix="t(`${localI18nScope}.environment.prefix`, '')"
        :suffix="t(`${localI18nScope}.environment.suffix`, '')"
        v-bind="uiProps.environmentInput"
        @update:model-value="filter"
      />
      <q-select
        v-model="displayFields"
        :hint="t(`${localI18nScope}.displayFields.hint`, '')"
        :label="t(`${localI18nScope}.displayFields.label`, '')"
        :options="displayFieldOptions"
        :prefix="t(`${localI18nScope}.displayFields.prefix`, '')"
        :suffix="t(`${localI18nScope}.displayFields.suffix`, '')"
        multiple
        v-bind="uiProps.displayFieldsSelect"
      />
    </q-card-section>
  </q-card>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { getClients } from "src/services/ClientService.js";
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";
import { getProjects } from "src/services/ProjectService.js";

const props = defineProps({
  modelValue: Object,
  displayFields: Array,
  i18nScope: String,
  nameSpace: String,
});
const emits = defineEmits(['update:model-value', 'update:display-fields']);

const {t} = useI18n();
const {ui} = useUiDesign();

const localI18nScope = computed(() => `${props.nameSpace}.CalendarSearchCard`);
const localNameSpace = computed(() => `${props.i18nScope}.CalendarSearchCard`);

const clients = ref([]);
const clientOptions = ref([]);
const environments = ref('');
const projects = ref([]);
const projectOptions = ref([]);
const displayFieldOptions = computed(() => [
  'environment',
  'project',
  'client'
].map((value) => ({
  label: t(`${localI18nScope.value}.displayFieldOptions.${value}`),
  value,
})));
const displayFields = ref(displayFieldOptions.value.filter(({value}) => props.displayFields.includes(value)));


const uiProps = computed(() => ({
  card: ui(localNameSpace.value, 'q-card'),
  cardSection: ui(localNameSpace.value, 'q-card-section'),
  clientSelect: ui(localNameSpace.value, 'client', 'q-select'),
  environmentInput: ui(localNameSpace.value, 'environment', 'q-input'),
  projectSelect: ui(localNameSpace.value, 'project', 'q-select'),
  displayFieldsSelect: ui(localNameSpace.value, 'displayFields', 'q-select'),
}));

watch(displayFields, (newVal, oldVal) => {
  if (!newVal || newVal.length === 0) {
    displayFields.value = oldVal
  }
  emits('update:display-fields', displayFields.value.map(({value}) => value));
})

function searchClient(val, update, abort) {
  return getClients({
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

function searchProject(val, update, abort) {
  return getProjects({
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
  const newFilters = {...props.modelValue};
  if (clients.value && clients.value.length > 0) {
    newFilters.client = clients.value.join('|');
  } else {
    delete newFilters.client;
  }

  if (environments.value && environments.value.length > 0) {
    newFilters.environmentShortName = `lk_${environments.value}`;
  } else {
    delete newFilters.environmentShortName;
  }

  if (projects.value && projects.value.length > 0) {
    newFilters.projectName = projects.value.join('|');
  } else {
    delete newFilters.projectName;
  }

  emits('update:model-value', newFilters);
}

</script>

<style scoped>

</style>
