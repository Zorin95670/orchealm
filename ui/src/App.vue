<template>
  <router-view/>
</template>
<script setup>
import { onMounted } from "vue";
import { useDarkMode } from "src/composables/useDarkMode.js";

const {initDarkMode} = useDarkMode();

onMounted(() => {
  initDarkMode();
})
</script>
