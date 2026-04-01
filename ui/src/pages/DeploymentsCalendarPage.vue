<template>
  <q-page v-bind="uiProps.page">
    <calendar-search-card
      v-model="filters"
      v-model:displayFields="displayFields"
      :i18n-scope="localI18nScope"
      :name-space="localNameSpace"
      @update:model-value="loadData"
    />
    <deployments-calendar
      :deployments="deployments"
      :display-fields="displayFields"
      :i18n-scope="localI18nScope"
      :name-space="localNameSpace"
      @change="({start, end}) => loadData(start, end)"
      @planned="openPlannedDeploymentDialog"
    />
    <planned-deployment-dialog/>
  </q-page>
</template>

<script setup>
import { useUiDesign } from "src/composables/useUiDesign.js";
import DeploymentsCalendar from "components/calendar/DeploymentsCalendar.vue";
import { onMounted, ref } from "vue";
import { getDeployments, plannedDeployment } from "src/services/DeploymentService.js";
import { usePagination } from "src/composables/usePagination.js";
import { useUiStore } from "stores/uiStore.js";
import CalendarSearchCard from "components/calendar/CalendarSearchCard.vue";
import PlannedDeploymentDialog from "components/dialog/PlannedDeploymentDialog.vue";
import UiEvent from "src/events/UiEvent.js";

const {ui} = useUiDesign();
const {toQuasarPagination, toPagination} = usePagination();
const uiStore = useUiStore();

const localI18nScope = 'Pages.DeploymentsCalendarPage';
const localNameSpace = 'Pages.DeploymentsCalendarPage';

const deployments = ref([]);
const displayFields = ref(['environment', 'project']);
const filters = ref({});
let pagination = {
  page: 1,
  rowsPerPage: 50,
  sortBy: "updateDate",
  descending: false,
};

const uiProps = {
  page: ui(localNameSpace, "q-page"),
};

async function loadData(start, end) {
  if (start && end) {
    filters.value.plannedPeriodStart = `${start}_bt_${end}`;
  }

  const data = await getDeployments(filters.value, toPagination(pagination));
  deployments.value = data.content;
  pagination = toQuasarPagination(data);
}

function getFirstDayTime(month = new Date().getMonth(), year = new Date().getFullYear()) {
  return new Date(year, month, 1, 0, 0, 0, 0).getTime();
}

function getLastDayTime(month = new Date().getMonth(), year = new Date().getFullYear()) {
  return new Date(year, month + 1, 0, 23, 59, 59, 999).getTime();
}

function openPlannedDeploymentDialog(date) {
  UiEvent.next({
    key: 'PlannedDeploymentDialog',
    data: {
      type: 'open',
      date,
      onConfirm: planned
    }
  });
}

function planned(event) {
  return plannedDeployment(event).then(loadData);
}

onMounted(() => {
  uiStore.setTitle('Pages.DeploymentsCalendarPage.title');
  loadData(getFirstDayTime(), getLastDayTime());
})
</script>

<style scoped>

</style>
