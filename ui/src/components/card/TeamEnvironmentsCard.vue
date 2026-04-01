<template>
  <q-card v-bind="uiProps.card">
    <q-card-section v-bind="uiProps.cardSectionTitle">
      {{ t(`${localI18nScope}.title`) }}
    </q-card-section>

    <q-card-section v-if="environments.length > 0" v-bind="uiProps.cardSectionEnvironments">
      <slot
        :environments="environments"
        name="environments"
      >
        <slot
          v-for="environment in environments"
          :key="environment.id"
          :environment="environment"
          name="environment"
        >
          <environment-card
            :environment="environment"
            :loading="loading"
            :name-space="localNameSpace"
          />
        </slot>
      </slot>
    </q-card-section>

    <q-card-section v-else v-bind="uiProps.cardSectionNoData">
      <q-icon v-if="uiProps.iconNoData?.name" v-bind="uiProps.iconNoData"/>
      <span>{{ t(`${localI18nScope}.noData`) }}</span>
    </q-card-section>

    <slot name="actions"/>
  </q-card>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";
import EnvironmentCard from "components/card/EnvironmentCard.vue";

const props = defineProps({
  i18nScope: String,
  nameSpace: String,
  loading: Boolean,
  environments: {
    type: Array,
    default: () => []
  }
});
const localI18nScope = computed(() => `${props.i18nScope}.TeamEnvironmentsCard`);
const localNameSpace = computed(() => `${props.nameSpace}.TeamEnvironmentsCard`);

const {t} = useI18n();
const {ui} = useUiDesign();

const uiProps = {
  card: ui(localNameSpace.value, 'q-card'),
  cardSectionTitle: ui(localNameSpace.value, 'title', 'q-card-section'),
  cardSectionEnvironments: ui(`${localNameSpace.value}.environments`, 'q-card-section'),
  cardSectionNoData: ui(`${localNameSpace.value}.noData`, 'q-card-section'),
  iconNoData: ui(`${localNameSpace.value}.noData`, 'q-icon'),
};

</script>

<style scoped>

</style>
