<template>
  <q-dialog
    :model-value="show"
    v-bind="uiProps.dialog"
    @hide="show = false"
  >
    <q-card ref="dialogRef">
      <q-card-section class="drag-handle" v-bind="uiProps.title">
        <q-icon v-if="uiProps.icon?.name" v-bind="uiProps.icon"/>
        <span v-bind="uiProps.textTitle">
          {{ t(`${i18nScope}.title`) }}
        </span>
      </q-card-section>


      <q-form
        v-bind="uiProps.form"
        @submit="addProject"
      >
        <q-card-section v-bind="uiProps.content">
          <q-select
            v-model="parent"
            :hint="t(`${i18nScope}.parent.hint`, '')"
            :label="t(`${i18nScope}.parent.label`, '')"
            :option-label="parentOptionLabel"
            :options="parentOptions"
            :prefix="t(`${i18nScope}.parent.prefix`, '')"
            :suffix="t(`${i18nScope}.parent.suffix`, '')"
            use-input
            v-bind="uiProps.parent"
            @filter="parentFilter"
            @update:model-value="setOrganization"
          >
            <template v-slot:no-option>
              <q-item v-bind="uiProps.itemNoParent">
                <q-item-section v-bind="uiProps.itemSectionNoParent">
                  {{ t(`${i18nScope}.no-parent`, '') }}
                </q-item-section>
              </q-item>
            </template>
          </q-select>
          <q-input
            v-model="name"
            :hint="t(`${i18nScope}.name.hint`, '')"
            :label="t(`${i18nScope}.name.label`, '')"
            :prefix="t(`${i18nScope}.name.prefix`, '')"
            :suffix="t(`${i18nScope}.name.suffix`, '')"
            v-bind="uiProps.name"
          />
          <q-input
            v-model="organization"
            :hint="t(`${i18nScope}.organization.hint`, '')"
            :label="t(`${i18nScope}.organization.label`, '')"
            :prefix="t(`${i18nScope}.organization.prefix`, '')"
            :suffix="t(`${i18nScope}.organization.suffix`, '')"
            v-bind="uiProps.organization"
          />
          <q-color
            v-model="color"
            v-bind="uiProps.color"
          />
          <span v-bind="uiProps.colorHint">
            {{ t(`${i18nScope}.color`) }}
          </span>
        </q-card-section>

        <q-card-actions v-bind="uiProps.actions">
          <q-btn
            :label="t(`${i18nScope}.cancel`, '')"
            type="reset"
            v-bind="uiProps.cancel"
            @click="onClose"
          />
          <q-btn
            :label="t(`${i18nScope}.edit`, '')"
            type="submit"
            v-bind="uiProps.edit"
          />
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useUiDesign } from "src/composables/useUiDesign.js";
import { useDialog } from "src/composables/useDialog.js";
import { useI18n } from "vue-i18n";
import * as ProjectService from "src/services/ProjectService.js";
import { usePagination } from "src/composables/usePagination.js";
import { colors } from "quasar";

const {show, dialogRef} = useDialog('EditProjectDialog', onOpen);
const nameSpace = ref('EditProjectDialog');
const i18nScope = ref('EditProjectDialog');
let onConfirm;

const {t} = useI18n();
const {ui} = useUiDesign();
const {toPagination} = usePagination();
const {luminosity} = colors;

const parent = ref(null);
const parentOptions = ref([]);
const name = ref('');
const organization = ref('');
const teamKey = ref('');
const color = ref('');
let id;

const uiProps = computed(() => ({
  dialog: ui(nameSpace.value, 'q-dialog'),
  icon: ui(nameSpace.value, 'q-icon'),
  title: ui(nameSpace.value, 'q-card-section-title', 'q-card-section'),
  textTitle: ui(nameSpace.value, 'title'),
  content: ui(nameSpace.value, 'q-card-section-content', 'q-card-section'),
  actions: ui(nameSpace.value, 'q-card-actions'),
  edit: ui(nameSpace.value, 'edit', 'q-btn'),
  cancel: ui(nameSpace.value, 'cancel', 'q-btn'),
  parent: ui(nameSpace.value, 'parent', 'q-select'),
  itemNoParent: ui(`${nameSpace.value}.no-parent`, 'q-item'),
  itemSectionNoParent: ui(`${nameSpace.value}.no-parent`, 'q-item-section'),
  name: ui(nameSpace.value, 'name', 'q-input'),
  organization: ui(nameSpace.value, 'shortName', 'q-input'),
  color: ui(nameSpace.value, 'color', 'q-color'),
  colorHint: ui(nameSpace.value, 'color-hint'),
  form: ui(nameSpace.value, 'q-form'),
}));


function setOrganization() {
  if (!organization.value || organization.value.trim().length === 0) {
    organization.value = parent.value.organization;
  }
}

function parentOptionLabel(item) {
  return `${item.organization} - ${item.name}`
}

function parentFilter(val, update, abort) {
  return ProjectService.getProjects({
    teamKey: teamKey.value,
    name: `lk_*${val}*`,
    id: `not_${id}`,
  }, toPagination({sortBy: 'name', descending: true})).then((data) => {
    update(() => {
      parentOptions.value = data.content;
    });
  }).catch(abort);
}

function addProject() {
  if (!color.value) {
    color.value = '#000000';
  }

  return ProjectService.updateProject(id, {
    name: name.value,
    organization: organization.value,
    color: color.value,
    textColor: luminosity(color.value) < 0.5 ? 'white' : 'black',
    parent: parent.value?.id || null,
    teamKey: teamKey.value,
  }).then(() => {
    show.value = false;
    return onConfirm();
  });
}

function onClose() {
  show.value = false;
}

function onOpen(event) {
  id = event.id;
  name.value = event.name;
  organization.value = event.organization;
  if (event.project) {
    ProjectService.getProjectById(event.parent).then((data) => {
      parent.value = data;
    });
  }
  color.value = event.color;
  teamKey.value = event.teamKey;

  onConfirm = event.onConfirm || (() => Promise.resolve());
}
</script>

<style lang="scss" scoped>
.drag-handle {
  cursor: move;
  user-select: none;
}
</style>
