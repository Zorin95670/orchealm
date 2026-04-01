<template>
  <q-page v-bind="uiProps.page">
    <img
      alt="Quasar logo"
      src="~assets/quasar-logo-vertical.svg"
      style="width: 200px; height: 200px"
    >
  </q-page>
</template>

<script setup>
import { computed } from "vue";
import { useUiDesign } from "src/composables/useUiDesign.js";

const localNameSpace = computed(() => `Pages.IndexPage`);

const {ui} = useUiDesign();

const uiProps = {
  page: ui(localNameSpace, "q-page"),
}
</script>
