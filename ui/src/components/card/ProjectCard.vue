<template>
  <q-card v-bind="uiProps.card">
    <q-card-section v-bind="uiProps.organizationSection">
      {{ project.organization }}
    </q-card-section>
    <q-card-section v-bind="uiProps.nameSection">
      {{ project.name }}
    </q-card-section>
  </q-card>
</template>

<script setup>
import { useUiDesign } from "src/composables/useUiDesign.js";

defineProps({
  project: Object,
});

const namespace = 'ProjectCard';
const {ui} = useUiDesign();

const uiProps = {
  card: ui(namespace, 'q-card'),
  organizationSection: ui(namespace, 'organization-section', 'q-card-section'),
  nameSection: ui(namespace, 'name-section', 'q-card-section'),
};
</script>

<style scoped>

</style>
