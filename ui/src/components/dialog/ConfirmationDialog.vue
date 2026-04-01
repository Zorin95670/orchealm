<template>
  <q-dialog
    :model-value="show"
    v-bind="uiProps.dialog"
    @hide="show = false"
  >
    <q-card ref="dialogRef" data-cy="confirmation_dialog_card">
      <q-card-section class="drag-handle" v-bind="uiProps.title">
        <q-icon v-if="uiProps.icon?.name" v-bind="uiProps.icon"/>
        <span v-bind="uiProps.textTitle">
          {{ title }}
        </span>
      </q-card-section>

      <!-- eslint-disable vue/no-v-text-v-html-on-component vue/no-v-html -->
      <q-card-section
        v-bind="uiProps.content"
        v-html="content"
      />
      <!-- eslint-enable vue/no-v-text-v-html-on-component vue/no-v-html -->

      <q-card-actions v-bind="uiProps.actions">
        <q-btn
          :label="t(`${i18nScope}.cancel`, '')"
          v-bind="uiProps.cancel"
          @click="onClose"
        />
        <q-btn
          :label="t(`${i18nScope}.confirm`, '')"
          v-bind="uiProps.confirm"
          @click="handleConfirm"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useUiDesign } from "src/composables/useUiDesign.js";
import { useDialog } from "src/composables/useDialog.js";
import { useI18n } from "vue-i18n";

const {show, dialogRef} = useDialog('ConfirmationDialog', onOpen);
const title = ref('');
const content = ref('');
const nameSpace = ref('');
const i18nScope = ref('');
let onConfirm;

const {t} = useI18n();
const {ui} = useUiDesign();

const uiProps = computed(() => ({
  dialog: ui(nameSpace.value, 'q-dialog'),
  icon: ui(nameSpace.value, 'q-icon'),
  title: ui(nameSpace.value, 'q-card-section-title', 'q-card-section'),
  textTitle: ui(nameSpace.value, 'title'),
  content: ui(nameSpace.value, 'q-card-section-content', 'q-card-section'),
  actions: ui(nameSpace.value, 'q-card-actions'),
  confirm: ui(nameSpace.value, 'confirm', 'q-btn'),
  cancel: ui(nameSpace.value, 'cancel', 'q-btn'),
}));

async function handleConfirm() {
  await onConfirm();
  show.value = false;
}

function onClose() {
  show.value = false;
}

function onOpen(event) {
  nameSpace.value = event.nameSpace || '';
  i18nScope.value = event.i18nScope || '';
  title.value = event.title || '';
  content.value = event.content || '';
  onConfirm = event.onConfirm || (() => Promise.resolve());
}
</script>

<style lang="scss" scoped>
.drag-handle {
  cursor: move;
  user-select: none;
}
</style>
