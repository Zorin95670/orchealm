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
        @submit="addEnvironment"
      >
        <q-card-section v-bind="uiProps.content">
          <q-input
            v-model="name"
            :hint="t(`${i18nScope}.name.hint`, '')"
            :label="t(`${i18nScope}.name.label`, '')"
            :prefix="t(`${i18nScope}.name.prefix`, '')"
            :suffix="t(`${i18nScope}.name.suffix`, '')"
            v-bind="uiProps.name"
          />
          <q-input
            v-model="shortName"
            :hint="t(`${i18nScope}.shortName.hint`, '')"
            :label="t(`${i18nScope}.shortName.label`, '')"
            :prefix="t(`${i18nScope}.shortName.prefix`, '')"
            :suffix="t(`${i18nScope}.shortName.suffix`, '')"
            v-bind="uiProps.shortName"
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
            :label="t(`${i18nScope}.add`, '')"
            type="submit"
            v-bind="uiProps.add"
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
import * as EnvironmentService from "src/services/EnvironmentService.js";

const {show, dialogRef} = useDialog('AddEnvironmentDialog', onOpen);
const nameSpace = ref('AddEnvironmentDialog');
const i18nScope = ref('AddEnvironmentDialog');
let onConfirm;

const {t} = useI18n();
const {ui} = useUiDesign();

const name = ref('');
const shortName = ref('');
const position = ref(0);
const teamKey = ref('');

const uiProps = computed(() => ({
  dialog: ui(nameSpace.value, 'q-dialog'),
  icon: ui(nameSpace.value, 'q-icon'),
  title: ui(nameSpace.value, 'q-card-section-title', 'q-card-section'),
  textTitle: ui(nameSpace.value, 'title'),
  content: ui(nameSpace.value, 'q-card-section-content', 'q-card-section'),
  actions: ui(nameSpace.value, 'q-card-actions'),
  add: ui(nameSpace.value, 'add', 'q-btn'),
  cancel: ui(nameSpace.value, 'cancel', 'q-btn'),
  name: ui(nameSpace, 'name', 'q-input'),
  shortName: ui(nameSpace, 'shortName', 'q-input'),
  form: ui(nameSpace, 'q-form'),
}));

function addEnvironment() {
  return EnvironmentService.createEnvironment({
    name: name.value,
    shortName: shortName.value,
    position: position.value,
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
  name.value = '';
  shortName.value = '';
  position.value = event.position;
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
