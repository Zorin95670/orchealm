<template>
  <q-card v-bind="uiProps.card">
    <q-card-section v-bind="uiProps.cardSectionTitle">
      {{ t(`${localI18nScope}.title`) }}
    </q-card-section>

    <q-card-section v-bind="uiProps.content">
      <q-table
        :columns="columns"
        :rows="projects"
        v-bind="uiProps.table"
      >
        <template #body-cell-color="props">
          <q-td :props="props">
            <color-square
              :color="props.row.color"
              :text-color="props.row.textColor"
            />
          </q-td>
        </template>
        <template #body-cell-actions="props">
          <q-td :props="props">
            <q-btn
              :label="t(`${localI18nScope}.edit`)"
              v-bind="uiProps.edit"
              @click="() => openEditProjectDialog(props.row)"
            />
            <q-btn
              :label="t(`${localI18nScope}.delete`)"
              v-bind="uiProps.delete"
              @click="() => openDeleteProjectDialog(props.row)"
            />
          </q-td>
        </template>
      </q-table>
    </q-card-section>

    <q-card-actions v-if="isEditor">
      <q-btn
        :label="t(`${localI18nScope}.add`)"
        v-bind="uiProps.add"
        @click="openAddProjectDialog"
      />
    </q-card-actions>
  </q-card>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import * as ProjectService from "src/services/ProjectService.js";
import { usePagination } from "src/composables/usePagination.js";
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";
import UiEvent from "src/events/UiEvent.js";
import ColorSquare from "components/other/ColorSquare.vue";

const props = defineProps({
  teamKey: String,
  i18nScope: String,
  nameSpace: String,
  isEditor: Boolean,
});

const {toPagination, toQuasarPagination} = usePagination();
const {t} = useI18n();
const {ui} = useUiDesign();

const projects = ref([]);
const pagination = ref({});
const filters = {};

const localI18nScope = computed(() => `${props.i18nScope}.ProjectsCard`);
const localNameSpace = computed(() => `${props.nameSpace}.ProjectsCard`);

const uiProps = {
  card: ui(localNameSpace.value, 'q-card'),
  cardSectionTitle: ui(localNameSpace.value, 'title', 'q-card-section'),
  content: ui(localNameSpace.value, 'q-card-section'),
  actions: ui(localNameSpace.value, 'q-card-actions'),
  table: ui(localNameSpace.value, 'q-table'),
  add: ui(localNameSpace.value, "add", 'q-btn'),
  delete: ui(localNameSpace.value, 'delete', 'q-btn'),
  edit: ui(localNameSpace.value, 'edit', 'q-btn'),
};
const columns = computed(() => {
  const result = [{
    name: "parent",
    label: t(`${localI18nScope.value}.columns.parent`),
    field: "parentFullName",
    sortable: true,
    format: (val) => !val ? '-' : val,
  }, {
    name: "organization",
    label: t(`${localI18nScope.value}.columns.organization`),
    field: "organization",
    sortable: true,
  }, {
    name: "name",
    label: t(`${localI18nScope.value}.columns.name`),
    field: "name",
    sortable: true,
  }, {
    name: "color",
    label: t(`${localI18nScope.value}.columns.color`),
    field: "color",
    sortable: true,
  }];

  if (props.isEditor) {
    result.push({
      name: "actions",
      label: t(`${localI18nScope.value}.columns.actions`, ''),
      field: "id",
    });
  }

  return result;

});

watch(() => props.teamKey, () => {
  if (!props.teamKey) {
    return;
  }

  loadProjects();
});

function loadProjects() {
  filters.teamKey = props.teamKey;

  return ProjectService.getProjects(filters, toPagination(pagination)).then((data) => {
    projects.value = data.content;
    pagination.value = toQuasarPagination(data);
  });
}

function openAddProjectDialog() {
  UiEvent.next({
    key: 'AddProjectDialog',
    data: {
      type: 'open',
      teamKey: props.teamKey,
      onConfirm: () => loadProjects()
    }
  });
}

function openEditProjectDialog(project) {
  UiEvent.next({
    key: 'EditProjectDialog',
    data: {
      type: 'open',
      id: project.id,
      name: project.name,
      organization: project.organization,
      parent: project.parent,
      color: project.color,
      teamKey: props.teamKey,
      onConfirm: () => loadProjects()
    }
  });
}

function openDeleteProjectDialog(item) {
  UiEvent.next({
    key: 'ConfirmationDialog',
    data: {
      type: 'open',
      nameSpace: `${localNameSpace.value}.DeleteProjectDialog`,
      i18nScope: `${localI18nScope.value}.DeleteProjectDialog`,
      title: t(`${localI18nScope.value}.DeleteProjectDialog.title`),
      content: t(`${localI18nScope.value}.DeleteProjectDialog.content`, item),
      onConfirm: () => deleteProject(item),
    }
  });
}

function deleteProject(item) {
  return ProjectService.deleteProject(item.id).then(loadProjects);
}

</script>

<style scoped>

</style>
