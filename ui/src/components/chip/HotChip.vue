<template>
  <q-chip v-bind="uiProps.chip">
    <q-icon v-if="uiProps.icon.name" v-bind="uiProps.icon"/>
    {{ t('DeploymentStatusChip.hot') }}
  </q-chip>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";

const {t} = useI18n();
const {ui} = useUiDesign();

const uiProps = {
  chip: ui('DeploymentStatusChip', 'hot-chip', 'q-chip'),
  icon: ui('DeploymentStatusChip', 'hot-icon', 'q-chip'),
};
</script>
