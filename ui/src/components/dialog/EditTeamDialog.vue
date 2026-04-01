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
        @submit="updateTeam"
      >
        <q-card-section v-bind="uiProps.content">
          <q-input
            v-model="name"
            :hint="t(`${i18nScope}.name.hint`, '')"
            :label="t(`${i18nScope}.name.label`, '')"
            :prefix="t(`${i18nScope}.name.prefix`, '')"
            :rules="[required]"
            :suffix="t(`${i18nScope}.name.suffix`, '')"
            v-bind="uiProps.name"
          />
          <q-input
            v-model="description"
            :hint="t(`${i18nScope}.description.hint`, '')"
            :label="t(`${i18nScope}.description.label`, '')"
            :prefix="t(`${i18nScope}.description.prefix`, '')"
            :suffix="t(`${i18nScope}.description.suffix`, '')"
            type="textarea"
            v-bind="uiProps.description"
          />
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
import * as TeamService from "src/services/TeamService.js";

const {show, dialogRef} = useDialog('EditTeamDialog', onOpen);
const nameSpace = ref('EditTeamDialog');
const i18nScope = ref('EditTeamDialog');
let onConfirm;

const {t} = useI18n();
const {ui} = useUiDesign();

const name = ref('');
const description = ref('');
let team;

const uiProps = computed(() => ({
  dialog: ui(nameSpace.value, 'q-dialog'),
  icon: ui(nameSpace.value, 'q-icon'),
  title: ui(nameSpace.value, 'q-card-section-title', 'q-card-section'),
  textTitle: ui(nameSpace.value, 'title'),
  content: ui(nameSpace.value, 'q-card-section-content', 'q-card-section'),
  actions: ui(nameSpace.value, 'q-card-actions'),
  edit: ui(nameSpace.value, 'edit', 'q-btn'),
  cancel: ui(nameSpace.value, 'cancel', 'q-btn'),
  name: ui(nameSpace.value, 'name', 'q-input'),
  description: ui(nameSpace.value, 'description', 'q-input'),
  form: ui(nameSpace.value, 'q-form'),
}));

function required(v) {
  return (v && v !== '') || t('default.rules.required');
}

function updateTeam() {
  return TeamService.updateTeam(team.key, {
    key: team.key,
    name: name.value,
    description: description.value,
  }).then(() => {
    show.value = false;
    return onConfirm();
  });
}

function onClose() {
  show.value = false;
}

function onOpen(event) {
  team = event.team;
  name.value = event.team.name;
  description.value = event.team.description;

  onConfirm = event.onConfirm || (() => Promise.resolve());
}
</script>

<style lang="scss" scoped>
.drag-handle {
  cursor: move;
  user-select: none;
}
</style>
