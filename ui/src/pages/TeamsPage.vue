<template>
  <q-page v-bind="uiProps.page">
    <q-btn
      v-if="isCreator"
      :label="t(`${localI18nScope}.add`, '')"
      v-bind="uiProps.add"
      @click="openAddTeamDialog()"
    />
    <q-table
      v-model:pagination="pagination"
      :columns="columns"
      :filter="filter"
      :rows="teams"
      grid
      hide-header
      row-key="name"
      v-bind="uiProps.table"
    >
      <template v-slot:item="props">
        <div>
          <team-card
            :i18n-scope="localI18nScope"
            :name-space="localNameSpace"
            :team="props.row"
            @edit="openEditTeamDialog(props.row)"
          />
        </div>
      </template>
    </q-table>
  </q-page>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useUiDesign } from "src/composables/useUiDesign.js";
import UiEvent from "src/events/UiEvent.js";
import { getTeams } from "src/services/TeamService.js";
import { usePagination } from "src/composables/usePagination.js";
import TeamCard from "components/card/TeamCard.vue";
import { useUiStore } from "stores/uiStore.js";
import { useI18n } from "vue-i18n";
import { useAcl } from "vue-simple-acl";

const {t} = useI18n();
const {ui} = useUiDesign();
const acl = useAcl();
const {toQuasarPagination, toPagination} = usePagination();
const uiStore = useUiStore();

const localNameSpace = 'Pages.TeamsPage';
const localI18nScope = 'Pages.TeamsPage';

const uiProps = {
  page: ui(localNameSpace, "q-page"),
  table: ui(localNameSpace, "q-table"),
  add: ui(localNameSpace, 'add', 'q-btn'),
}
const teams = ref([]);
const pagination = ref({
  page: 1,
  rowsPerPage: 10,
  sortBy: "name",
  descending: false,
});

const filter = ref("");
const columns = [{
  name: 'key',
  label: 'Key',
  field: 'key'
}];

const isCreator = computed(() => acl.can('create-team'));

async function loadTeams() {
  const data = await getTeams({}, toPagination(pagination.value));
  teams.value = data.content;
  pagination.value = toQuasarPagination(data);
}

function openEditTeamDialog(team) {
  UiEvent.next({
    key: 'EditTeamDialog',
    data: {
      type: 'open',
      team,
      onConfirm: loadTeams,
    }
  });
}

function openAddTeamDialog() {
  UiEvent.next({
    key: 'AddTeamDialog',
    data: {
      type: 'open',
      onConfirm: loadTeams,
    }
  });
}

onMounted(() => {
  uiStore.setTitle('Pages.TeamsPage.title');
  UiEvent.next({
    key: "loading",
    value: true
  });

  loadTeams().finally(() => {
    UiEvent.next({
      key: "loading",
      value: false,
    });
  });
});
</script>

<style>
.q-table__grid-content {
  justify-content: center;
}
</style>
