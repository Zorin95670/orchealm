<template>
  <div class="column q-pa-sm" style="height: 270px">
    <span>
      {{ t(`${i18nScope}.TableSearch.filterBy`, {name: t(`${i18nScope}.columns.${column.name}`)}) }}
    </span>
    <q-space/>
    <q-scroll-area style="height: 185px;">
      <div
        v-for="(filter, index) in filters"
        :key="`value_${index}`"
        class="q-pa-sm flex"
      >
        <q-input
          v-model="filter.value"
          :label="t(`${i18nScope}.TableSearch.inputUuid`)"
          class="q-mx-sm"
          v-bind="uiProps.input"
        />
        <q-btn
          v-if="index !== filters.length - 1"
          :label="t(`${i18nScope}.TableSearch.delete`)"
          v-bind="uiProps.delete"
          @click="removeFilter(index)"
        />
        <q-btn
          v-else
          :label="t(`${i18nScope}.TableSearch.add`)"
          v-bind="uiProps.add"
          @click="addFilter"
        />
      </div>
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
import { ref } from "vue";

const props = defineProps({
  column: Object,
  nameSpace: String,
  i18nScope: String,
});
const emits = defineEmits(['search']);

const {t} = useI18n();
const {ui} = useUiDesign();
const filters = ref([{value: ''}]);

const uiProps = {
  search: ui(`${props.nameSpace}.TableSearch`, 'search', 'q-btn'),
  delete: ui(`${props.nameSpace}.TableSearch`, 'delete', 'q-btn'),
  add: ui(`${props.nameSpace}.TableSearch`, 'add', 'q-btn'),
  input: ui(`${props.nameSpace}.TableSearch`, 'inputUuid', 'q-input'),
};

function addFilter() {
  filters.value.push({value: ''});
}

function removeFilter(index) {
  filters.value = filters.value.filter((_, value) => value !== index);
}

function search() {
  emits('search', filters.value
    .map(({value}) => value.trim())
    .filter((value) => value !== '')
    .join('|'));
}
</script>

<style scoped>

</style>
