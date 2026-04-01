<template>
  <div class="column q-pa-sm" style="height: 270px">
    <span>
      {{ t(`${i18nScope}.TableSearch.filterBy`, {name: t(`${i18nScope}.columns.${column.name}`)}) }}
    </span>
    <q-scroll-area style="height: 185px;">
      <div
        v-for="(filter, index) in filters"
        :key="`value_${index}`"
        class="q-pa-sm flex"
      >
        <q-select
          v-model="filter.type"
          :display-value="t(`default.${filter.type}`)"
          :label="t(`${i18nScope}.TableSearch.filterType`)"
          :options="options"
          v-bind="uiProps.filterType"
        >
          <template v-slot:option="scope">
            <q-item v-bind="scope.itemProps">
              <q-item-section>
                <q-item-label>{{ t(`default.${scope.opt}`) }}</q-item-label>
              </q-item-section>
            </q-item>
          </template>
        </q-select>
        <q-input
          v-model="filter.value"
          :label="t(`${i18nScope}.TableSearch.inputText`)"
          class="q-mx-sm"
          v-bind="uiProps.inputText"
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
    <q-space/>
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

const options = computed(() => [
  'contains', 'notContains',
  'equals', 'notEquals',
  'startWith', 'notStartWith',
  'endWith', 'notEndWith'

]);
const filters = ref([{
  type: 'contains',
  value: '',
}]);
const uiProps = {
  search: ui(`${props.nameSpace}.TableSearch`, 'search', 'q-btn'),
  delete: ui(`${props.nameSpace}.TableSearch`, 'delete', 'q-btn'),
  add: ui(`${props.nameSpace}.TableSearch`, 'add', 'q-btn'),
  filterType: ui(`${props.nameSpace}.TableSearch`, 'filterType', 'q-select'),
  inputText: ui(`${props.nameSpace}.TableSearch`, 'inputText', 'q-input'),
};

const startOperators = {
  contains: 'lk_*',
  notContains: 'not_lk_*',
  equals: 'lk_*',
  notEquals: 'not_',
  startWith: 'lk_',
  notStartWith: 'not_lk_',
  endWith: 'lk_',
  notEndWith: 'not_lk_'
};
const endOperators = {
  contains: '*',
  notContains: '*',
  equals: '*',
  notEquals: '',
  startWith: '*',
  notStartWith: '*',
  endWith: '',
  notEndWith: ''
};

function addFilter() {
  filters.value.push({
    type: 'contains',
    value: '',
  });
}

function removeFilter(index) {
  filters.value = filters.value.filter((_, value) => value !== index);
}

function search() {

  emits('search', filters.value.map((filter) => ({
    ...filter,
    value: filter.value.trim()
  }))
    .filter(({value}) => value !== '')
    .map((filter) => `${startOperators[filter.type]}${filter.value}${endOperators[filter.type]}`)
    .join('|'));
}
</script>

<style scoped>

</style>
