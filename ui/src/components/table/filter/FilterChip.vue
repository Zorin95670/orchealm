<template>
  <q-chip
    removable
    v-bind="uiProps.chip"
    @remove="emits('remove', filter.id)"
  >
    <q-avatar v-bind="uiProps.avatar">
      {{ t(`${i18nScope}.columns.${filter.name}`) }}
    </q-avatar>
    <div class="q-px-sm">
      <template
        v-for="(item, index) in items"
        :key="`value_${index}`"
      >
        <em
          v-if="index > 0"
          class="q-mx-sm"
        >
          {{ t('default.separator') }}
        </em>
        <span>
        {{ item }}
      </span>
      </template>
    </div>
  </q-chip>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";
import { computed } from "vue";

const props = defineProps({
  filter: Object,
  value: String,
  nameSpace: String,
  i18nScope: String,
});
const emits = defineEmits(['remove']);

const {t} = useI18n();
const {ui} = useUiDesign();

const items = computed(() => {
  if (props.filter.type === 'boolean') {
    return [
      t(`default.${props.filter.value === 'true' ? 'yes' : 'no'}`),
    ];
  }

  return props.filter.value.split('|').map((value) => {
    if (startWith(value, 'not_lk_*') && endWith(value, '*')) {
      return `${t('default.notContains', {value: clean(value, 'not_lk_*', '*')})}`
    }
    if (startWith(value, 'lk_*') && endWith(value, '*')) {
      return `${t('default.contains', {value: clean(value, 'lk_*', '*')})}`
    }
    if (startWith(value, 'not_lk_*') && notEndWith(value, '*')) {
      return `${t('default.notEndWith', {value: clean(value, 'not_lk_*', '')})}`
    }
    if (startWith(value, 'lk_*') && notEndWith(value, '*')) {
      return `${t('default.endWith', {value: clean(value, 'lk_*', '')})}`
    }
    if (startWith(value, 'not_lk_') && endWith(value, '*')) {
      return `${t('default.notStartWith', {value: clean(value, 'not_lk_', '*')})}`
    }
    if (startWith(value, 'lk_') && endWith(value, '*')) {
      return `${t('default.startWith', {value: clean(value, 'lk_', '*')})}`
    }
    if (startWith(value, 'not_')) {
      return `${t('default.notEquals', {value: clean(value, 'not_', '')})}`
    }
    if (startWith(value, 'gt_')) {
      return `${t('default.superior', {value: clean(value, 'gt_', '')})}`
    }
    if (startWith(value, 'lt_')) {
      return `${t('default.inferior', {value: clean(value, 'lt_', '')})}`
    }
    return `${t('default.equals', {value: `"${value}"`})}`
  });
});

function startWith(text, value) {
  return text.indexOf(value) === 0;
}

function endWith(text, value) {
  return text.lastIndexOf(value) === text.length - value.length;
}

function notEndWith(text, value) {
  return text.lastIndexOf(value) !== text.length - value.length;
}

function clean(text, start, end) {
  return `"${text.substring(start.length, text.length - end.length)}"`;
}

const uiProps = {
  chip: ui(`${props.nameSpace}.TableSearch`, 'q-chip'),
  avatar: ui(`${props.nameSpace}.TableSearch`, 'q-chip-avatar', 'q-avatar'),
};
</script>

<style scoped>
em {
  font-style: italic;
  color: #888;
}

.q-avatar {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
  width: auto;
}
</style>
