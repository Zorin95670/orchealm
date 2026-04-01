<template>
  <q-field
    :hint="t(`${localI18nScope}.hint`, '')"
    :label="t(`${localI18nScope}.label`, '')"
    :prefix="t(`${localI18nScope}.prefix`, '')"
    :stack-label="(filters?.length || 0) > 0"
    :suffix="t(`${localI18nScope}.suffix`, '')"
    v-bind="uiProps.field"
  >
    <template v-if="uiProps.iconSearch.name" #prepend>
      <q-icon class="self-center" v-bind="uiProps.iconSearch"/>
    </template>

    <template
      v-if="filters?.length"
      #control
    >
      <div class="q-mt-md">
        <filter-chip
          v-for="filter in filters"
          :key="filter.name"
          :filter="filter"
          :i18n-scope="i18nScope"
          :name-space="nameSpace"
          @remove="removeFilter"
        />
      </div>
    </template>

    <template #append>
      <q-icon
        v-if="!isMenuOpen"
        v-bind="uiProps.iconMenuOpen"
      />
      <q-icon
        v-else
        v-bind="uiProps.iconMenuClose"
      />
    </template>

    <q-menu
      v-model="isMenuOpen"
      :persistent="isMenuPersistent"
      v-bind="uiProps.menu"
    >
      <div class="flex column" style="min-width: 600px">
        <q-tabs
          v-model="tab"
          v-bind="uiProps.tabs"
        >
          <q-tab :label="t(`${localI18nScope}.tabs.filter`)" name="filter"/>
          <q-tab :label="t(`${localI18nScope}.tabs.columnSettings`)" name="columnSettings"/>
        </q-tabs>

        <q-separator/>

        <q-tab-panels v-model="tab" animated>
          <q-tab-panel class="q-pa-none row" name="filter">
            <q-scroll-area class="col-auto" style="height: 270px; min-width: 200px">
              <q-list>
                <q-item
                  v-for="filter in localFilters"
                  :key="filter.name"
                  :active="filter.name === currentFilter.name"
                  clickable
                  @click="currentFilter = filter"
                >
                  <q-item-section>
                    {{ t(`${i18nScope}.columns.${filter.name}`) }}
                  </q-item-section>
                </q-item>
              </q-list>
            </q-scroll-area>
            <q-separator vertical/>
            <uuid-filter
              v-if="currentFilter.type === 'uuid'"
              :column="currentFilter"
              :i18n-scope="i18nScope"
              :name-space="nameSpace"
              class="col"
              @search="(value) => search(currentFilter, value)"
            />
            <text-filter
              v-else-if="currentFilter.type === 'text'"
              :column="currentFilter"
              :i18n-scope="i18nScope"
              :name-space="nameSpace"
              class="col"
              @search="(value) => search(currentFilter, value)"
            />
            <number-filter
              v-else-if="currentFilter.type === 'number'"
              :column="currentFilter"
              :i18n-scope="i18nScope"
              :name-space="nameSpace"
              class="col"
              @search="(value) => search(currentFilter, value)"
            />
            <list-filter
              v-else-if="currentFilter.type === 'list'"
              :column="currentFilter"
              :i18n-scope="i18nScope"
              :name-space="nameSpace"
              class="col"
              @search="(value) => search(currentFilter, value)"
            />
            <boolean-filter
              v-else-if="currentFilter.type === 'boolean'"
              :column="currentFilter"
              :i18n-scope="i18nScope"
              :name-space="nameSpace"
              class="col"
              @search="(value) => search(currentFilter, value)"
            />
          </q-tab-panel>

          <q-tab-panel class="q-pa-none" name="columnSettings">
            <q-scroll-area style="height: 270px;">
              <q-list>
                <draggable
                  v-model="localColumns"
                  :animation="200"
                  :fallback-tolerance="3"
                  :force-fallback="true"
                  :scroll="true"
                  :scroll-sensitivity="25"
                  :scroll-speed="5"
                  handle=".drag-handle"
                  item-key="name"
                  @change="emits('update:columns', localColumns)"
                >
                  <template #item="{ element }">
                    <q-item>
                      <q-item-section avatar class="drag-handle cursor-pointer">
                        <q-icon
                          v-bind="uiProps.columnDrag"
                        />
                      </q-item-section>
                      <q-item-section>
                        {{ t(`${i18nScope}.columns.${element.name}`) }}
                      </q-item-section>
                      <q-item-section avatar>
                        <q-btn
                          v-if="element.isVisible"
                          v-bind="uiProps.columnShow"
                          @click="changeColumnVisibility(element.name, false)"
                        />
                        <q-btn
                          v-else
                          v-bind="uiProps.columnHide"
                          @click="changeColumnVisibility(element.name, true)"
                        />
                      </q-item-section>
                    </q-item>
                  </template>
                </draggable>
              </q-list>
            </q-scroll-area>
          </q-tab-panel>
        </q-tab-panels>
      </div>
    </q-menu>
  </q-field>
</template>

<script setup>
import draggable from 'vuedraggable';
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";
import TextFilter from "components/table/filter/TextFilter.vue";
import UuidFilter from "components/table/filter/UuidFilter.vue";
import NumberFilter from "components/table/filter/NumberFilter.vue";
import ListFilter from "components/table/filter/ListFilter.vue";
import BooleanFilter from "components/table/filter/BooleanFilter.vue";
import FilterChip from "components/table/filter/FilterChip.vue";

const props = defineProps({
  filters: Array,
  columns: Array,
  order: Array,
  nameSpace: String,
  i18nScope: String,
});
const emits = defineEmits(['update:columns', 'update:filters']);

const {t} = useI18n();
const {ui} = useUiDesign();

const currentFilter = ref(props.columns.find(({name}) => name === props.order[0]));
const localFilters = computed(() => props.order
  .map((key) => props.columns.find(({name}) => name === key))
  .filter(({isFilter}) => isFilter));
const localColumns = ref(props.columns);
const isMenuOpen = ref(false);
const isMenuPersistent = ref(false);
const tab = ref('filter');
const localNameSpace = computed(() => `${props.nameSpace}.TableSearch`);
const localI18nScope = computed(() => `${props.i18nScope}.TableSearch`);

const uiProps = computed(() => ({
  field: ui(localNameSpace.value, 'q-field'),
  menu: ui(localNameSpace.value, 'q-menu'),
  tabs: ui(localNameSpace.value, 'q-tabs'),
  iconSearch: ui(localNameSpace.value, 'searchIcon', 'q-icon'),
  iconMenuOpen: ui(localNameSpace.value, 'menuOpenIcon', 'q-icon'),
  iconMenuClose: ui(localNameSpace.value, 'menuCloseIcon', 'q-icon'),
  filterContainer: ui(localNameSpace.value, 'filterContainer'),
  columnDrag: ui(localNameSpace.value, 'columnSettings.drag', 'q-icon'),
  columnShow: ui(localNameSpace.value, 'columnSettings.show', 'q-btn'),
  columnHide: ui(localNameSpace.value, 'columnSettings.hide', 'q-btn')
}));

function changeColumnVisibility(key, value) {
  localColumns.value.find(({name}) => name === key).isVisible = value;
  emits('update:columns', localColumns.value);
}

function search(filter, value) {
  let filters = [...props.filters];

  if (filter.type === 'boolean') {
    filters = filters.filter(({name}) => name === filter.name);
  }

  if (value !== '') {
    filters.push({
      id: crypto.randomUUID(),
      name: filter.name,
      type: filter.type,
      value,
    });
  }

  emits('update:filters', filters);
  isMenuOpen.value = false;
}

function removeFilter(uuid) {
  let filters = props.filters.filter(({id}) => id !== uuid);

  emits('update:filters', filters);
}
</script>

<style>
.q-field__control-container {
  padding-top: 8px !important;
}
</style>
