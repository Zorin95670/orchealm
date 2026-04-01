<template>
  <q-card class="row environment-card" v-bind="uiProps.card">
    <slot name="prepend"/>
    <q-card-section
      class="column environment-card"
      v-bind="uiProps.cardSection"
    >
      <slot v-if="!loading" :ui-props="uiProps.title" name="title">
        <span v-bind="uiProps.title">
          {{ environment.name }}
        </span>
      </slot>
      <blur-loader v-else :name-space="`${localNameSpace}.title`"/>
      <slot v-if="!loading" :ui-props="uiProps.subTitle" name="sub-title">
        <span v-bind="uiProps.subTitle">
          {{ environment.shortName }}
        </span>
      </slot>
      <blur-loader
        v-else
        :name-space="`${localNameSpace}.sub-title`"
      />
    </q-card-section>
    <slot name="append"/>
  </q-card>
</template>

<script setup>
import { useUiDesign } from "src/composables/useUiDesign.js";
import { computed } from "vue";
import BlurLoader from "components/loader/BlurLoader.vue";

const props = defineProps({
  nameSpace: {
    type: String,
    required: true
  },
  environment: {
    type: Object,
    required: true,
  },
  loading: {
    type: Boolean,
    default: false,
  }
});

const {ui} = useUiDesign();

const localNameSpace = computed(() => `${props.nameSpace}.EnvironmentCard`);
const uiProps = {
  card: ui(localNameSpace.value, 'q-card'),
  cardSection: ui(localNameSpace.value, 'q-card-section'),
  title: ui(`${localNameSpace.value}.title`, 'span'),
  subTitle: ui(`${localNameSpace.value}.sub-title`, 'span'),
};
</script>

<style scoped>

</style>
