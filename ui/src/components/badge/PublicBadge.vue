<template>
  <q-badge
    v-bind="uiProps.badge"
  >
    <q-icon v-if="uiProps.icon.name" v-bind="uiProps.icon"/>
    {{ t('PublicBadge.label') }}
  </q-badge>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";

const {t} = useI18n();
const {ui} = useUiDesign();

const uiProps = {
  badge: ui('PublicBadge', 'q-badge'),
  icon: ui('PublicBadge', 'q-icon')
};

</script>

<style scoped>

</style>
