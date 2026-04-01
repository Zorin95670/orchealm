<template>
  <div class="column q-pa-sm" style="height: 270px">
    <span>
      {{ t(`${i18nScope}.TableSearch.filterBy`, {name: t(`${i18nScope}.columns.${column.name}`)}) }}
    </span>
    <q-space/>
    <q-scroll-area style="height: 185px;">
      <q-option-group
        v-model="filter"
        :options="options"
        type="checkbox"
      />
    </q-scroll-area>
    <q-btn
      :label="t(`${i18nScope}.TableSearch.search`)"
      v-bind="uiProps.search"
      @click="search"
    />
  </div>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";
import { computed, ref } from "vue";

const props = defineProps({
  column: Object,
  nameSpace: String,
  i18nScope: String,
});
const emits = defineEmits(['search']);

const {t} = useI18n();
const {ui} = useUiDesign();

const filter = ref([]);
const options = computed(() => props.column.values.map((item) => ({
  label: t(item.label),
  value: item.value,
})));
const uiProps = {
  search: ui(`${props.nameSpace}.TableSearch`, 'search', 'q-btn'),
};

function search() {
  emits('search', filter.value.join('|'));
}
</script>

<style scoped>

</style>
