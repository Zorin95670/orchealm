<template>
  <div
    :class="`flex flex-center text-bold color-square text-${textColor}`"
    :style="style"
  >
    X
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useColor } from "src/composables/useColor.js";

const props = defineProps({
  color: String,
  textColor: String,
});

const {toRGB} = useColor();

const style = computed(() => ({
  'background-color': `rgb(${toRGB(props.color).join(',')})`,
}));
</script>

<style scoped>
.color-square {
  width: 24px;
  height: 24px;
  border: 1px solid black;

}
</style>
