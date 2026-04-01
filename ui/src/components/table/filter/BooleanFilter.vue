<template>
  <div class="column q-pa-sm" style="height: 270px">
    <span>
      {{ t(`${i18nScope}.TableSearch.filterBy`, {name: t(`${i18nScope}.columns.${column.name}`)}) }}
    </span>
    <q-option-group
      v-model="selected"
      :options="options"
      color="primary"
    />
    <q-space/>
    <q-btn
      :label="t(`${i18nScope}.TableSearch.search`)"
      v-bind="uiProps.search"
      @click="search"
    />
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";

const props = defineProps({
  column: Object,
  nameSpace: String,
  i18nScope: String,
});
const emits = defineEmits(['search']);

const {t} = useI18n();
const {ui} = useUiDesign();

const selected = ref('all');
const options = computed(() => [{
  label: t('default.all'),
  value: 'all'
}, {
  label: t('default.yes'),
  value: 'yes'
}, {
  label: t('default.no'),
  value: 'no'
}]);

const uiProps = {
  search: ui(`${props.nameSpace}.TableSearch`, 'search', 'q-btn'),
};

function search() {
  if (selected.value === 'all') {
    emits('search', '');
  } else if (selected.value === 'yes') {
    emits('search', 'true');
  } else {
    emits('search', 'false');
  }
}
</script>

<style scoped>

</style>
