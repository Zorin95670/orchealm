<template>
  <q-page v-bind="uiProps.page">
    <table-search
      v-model:columns="columnOptions"
      v-model:filters="filters"
      :i18n-scope="localI18nScope"
      :name-space="localNameSpace"
      :order="columnKeys"
      @update:filters="loadData"
      @update:columns="updateColumn"
    />
    <q-table
      v-model:pagination="pagination"
      :columns="columns"
      :rows="deployments"
      binary-state-sort
      row-key="id"
      v-bind="uiProps.table"
      @request="onRequest"
    >
      <template #body-cell-status="data">
        <q-td :props="data">
          <completed-chip v-if="data.row.status === 'COMPLETED'"/>
          <failed-chip v-else-if="data.row.status === 'FAILED'"/>
          <in-progress-chip v-else/>
        </q-td>
      </template>
      <template #body-cell-state="data">
        <q-td :props="data">
          <hot-chip v-if="data.row.state === 'HOT'"/>
          <new-chip v-else-if="data.row.state === 'NEW'"/>
        </q-td>
      </template>
    </q-table>
  </q-page>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useUiDesign } from "src/composables/useUiDesign.js";
import UiEvent from "src/events/UiEvent.js";
import { usePagination } from "src/composables/usePagination.js";
import { useI18n } from "vue-i18n";
import { getDeployments } from "src/services/DeploymentService.js";
import CompletedChip from "components/chip/CompletedChip.vue";
import FailedChip from "components/chip/FailedChip.vue";
import InProgressChip from "components/chip/InProgressChip.vue";
import NewChip from "components/chip/NewChip.vue";
import HotChip from "components/chip/HotChip.vue";
import { useUiStore } from "stores/uiStore.js";
import TableSearch from "components/table/filter/TableSearch.vue";

const {t} = useI18n();
const {ui} = useUiDesign();
const {toQuasarPagination, toPagination} = usePagination();
const uiStore = useUiStore();

const localNameSpace = 'Pages.DeploymentsPage';
const localI18nScope = 'Pages.DeploymentsPage';

const columnKeys = [
  'id',
  'teamKey',
  'projectId',
  'plannedPeriod',
  'actualPeriod',
  'delayed',
  'masterProjectId',
  'projectName',
  'masterProjectName',
  'color',
  'environmentName',
  'environmentShortName',
  'environmentPosition',
  'version',
  'client',
  'status',
  'state'
];
const uiProps = {
  page: ui(localNameSpace, "q-page"),
  table: ui(localNameSpace, "q-table"),
  columns: columnKeys.reduce((acc, key) => {
    acc[key] = ui(`${localNameSpace}.columns`, key);
    return acc;
  }, {}),
}
const deployments = ref([]);
const filters = ref([]);
const pagination = ref({
  page: 1,
  rowsPerPage: 10,
  sortBy: "updateDate",
  descending: true,
});

const columnOptions = ref(initColumns());
const columns = computed(() => columnOptions.value.filter(({isVisible}) => isVisible));

function updateColumn() {
  window.localStorage.setItem(
    `${localNameSpace}.columns`,
    columnOptions.value
      .filter(({isVisible}) => isVisible)
      .map(({name}) => name).join(',')
  );
}

async function loadData() {
  const data = await getDeployments(filters.value.reduce((acc, item) => {
    if (!acc[item.name]) {
      acc[item.name] = [];
    }

    acc[item.name].push(item.value);

    return acc;
  }, {}), toPagination(pagination.value));
  deployments.value = data.content;
  pagination.value = toQuasarPagination(data);
}

function onRequest(props) {
  pagination.value = props.pagination;

  return loadData();
}

function initColumns() {
  const visibleKeys = window.localStorage.getItem(`${localNameSpace}.columns`)?.split(',') || [];

  if (visibleKeys.length === 0) {
    return columnKeys.map((key, index) => ({
      index,
      isVisible: true,
      isFilter: true,
      ...uiProps.columns[key],
      name: key,
      field: key,
      label: t(`${localI18nScope}.columns.${key}`),
    })).map((item) => ({
      ...item,
      index: item.isVisible ? item.index : item.index + columnKeys.length,
    })).sort((a, b) => a.index - b.index);
  }

  return columnKeys.map((key, index) => ({
    isFilter: true,
    ...uiProps.columns[key],
    index: visibleKeys.includes(key) ? visibleKeys.indexOf(key) : index + visibleKeys.length,
    isVisible: visibleKeys.includes(key),
    name: key,
    field: key,
    label: t(`${localI18nScope}.columns.${key}`),
  })).sort((a, b) => a.index - b.index);
}

onMounted(() => {
  uiStore.setTitle('Pages.DeploymentsPage.title');
  UiEvent.next({
    key: "loading",
    value: true
  });

  loadData().finally(() => {
    UiEvent.next({
      key: "loading",
      value: false,
    });
  });
});
</script>
