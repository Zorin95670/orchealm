<template>
  <q-chip v-bind="uiProps.chip">
    <q-spinner v-bind="uiProps.spinner"/>

    {{ t('DeploymentStatusChip.inProgress') }}
  </q-chip>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";

const {t} = useI18n();
const {ui} = useUiDesign();

const uiProps = {
  chip: ui('DeploymentStatusChip', 'inProgress-chip', 'q-chip'),
  spinner: ui('DeploymentStatusChip', 'inProgress-spinner', 'q-spinner'),
};
</script>
