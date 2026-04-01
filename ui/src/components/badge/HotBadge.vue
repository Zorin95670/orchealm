<template>
  <q-badge
    v-bind="uiProps.badge"
  >
    <q-icon v-if="uiProps.icon.name" v-bind="uiProps.icon"/>
    {{ t('DeploymentStatusBadge.hot') }}
  </q-badge>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";

const {t} = useI18n();
const {ui} = useUiDesign();

const uiProps = {
  badge: ui('DeploymentStatusBadge', 'hot-badge', 'q-badge'),
  icon: ui('DeploymentStatusBadge', 'hot-icon', 'q-icon')
};
</script>
