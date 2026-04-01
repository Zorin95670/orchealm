<template>
  <div
    :class="['blur-box', widthClass, heightClass, primaryClass]"
    v-bind="uiProps"
  />
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { useUiDesign } from "src/composables/useUiDesign.js";

const props = defineProps({
  nameSpace: String,
  width: {
    type: String,
    default: 'md',
    validator: (val) => ['xs', 'sm', 'md', 'lg', 'xl'].includes(val),
  },
  height: {
    type: String,
    default: 'md',
    validator: (val) => ['xs', 'sm', 'md', 'lg', 'xl'].includes(val),
  },
  primary: {
    type: Boolean,
    default: false,
  },
});

const { ui } = useUiDesign();

const uiProps = ui(props.nameSpace, 'BlurLoader');

const widthClass = computed(() => {
  return {
    xs: 'blur-loader--width-xs',
    sm: 'blur-loader--width-sm',
    md: 'blur-loader--width-md',
    lg: 'blur-loader--width-lg',
    xl: 'blur-loader--width-xl',
  }[props.width];
});

const heightClass = computed(() => {
  return {
    xs: 'blur-loader--height-xs',
    sm: 'blur-loader--height-sm',
    md: 'blur-loader--height-md',
    lg: 'blur-loader--height-lg',
    xl: 'blur-loader--height-xl',
  }[props.height];
});

const primaryClass = computed(() => {
  return props.primary ? 'blur-loader--primary' : '';
});
</script>

<style lang="scss" scoped>
.blur-box {
  height: 1.25rem;
  background-color: #e0e0e0;
  position: relative;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    height: 100%;
    width: 100%;
    background: linear-gradient(
      90deg,
      transparent,
      rgba(255, 255, 255, 0.6),
      transparent
    );
    animation: shimmer 1.5s infinite ease-in-out;
  }
}

.blur-loader--primary {
  background-color: rgba(255, 255, 255, 0.15);

  &::after {
    background: linear-gradient(
      90deg,
      transparent,
      rgba(255, 255, 255, 0.4),
      transparent
    );
  }
}

.blur-loader--width-xs {
  width: 25px;
}

.blur-loader--width-sm {
  width: 50px;
}

.blur-loader--width-md {
  width: 100px;
}

.blur-loader--width-lg {
  width: 150px;
}

.blur-loader--width-xl {
  width: 300px;
}

.blur-loader--height-xs {
  height: 0.75rem;
}

.blur-loader--height-sm {
  height: 1rem;
}

.blur-loader--height-md {
  height: 1.25rem;
}

.blur-loader--height-lg {
  height: 2rem;
}

.blur-loader--height-xl {
  height: 2.5rem;
}

@keyframes shimmer {
  100% {
    left: 100%;
  }
}
</style>
