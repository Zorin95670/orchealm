<template>
  <q-chip v-bind="uiProps.chip">
    <q-icon v-if="uiProps.icon.name" v-bind="uiProps.icon"/>
    {{ t('DeploymentStatusChip.failed') }}
  </q-chip>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";

const {t} = useI18n();
const {ui} = useUiDesign();

const uiProps = {
  chip: ui('DeploymentStatusChip', 'failed-chip', 'q-chip'),
  icon: ui('DeploymentStatusChip', 'failed-icon', 'q-chip'),
};
</script>
