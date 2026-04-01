<template>
  <q-chip v-bind="uiProps.chip">
    <q-icon v-if="uiProps.icon.name" v-bind="uiProps.icon"/>
    {{ t('DeploymentStatusChip.new') }}
  </q-chip>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";

const {t} = useI18n();
const {ui} = useUiDesign();

const uiProps = {
  chip: ui('DeploymentStatusChip', 'new-chip', 'q-chip'),
  icon: ui('DeploymentStatusChip', 'new-icon', 'q-chip'),
};
</script>
