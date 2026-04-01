<template>
  <q-toolbar v-bind="uiProps.toolbar">
    <div v-bind="uiProps.calendarViewContainer">
      <q-btn-toggle
        v-model="localCalendarView"
        :label="t(`${localI18nScope}.calendarView.label`, '')"
        :options="calendarViews"
        v-bind="uiProps.calendarView"
      />
    </div>
    <label v-bind="uiProps.displayDate">
      {{ displayDate }}
    </label>
    <div v-bind="uiProps.actionsContainer">
      <q-btn
        :label="t(`${localI18nScope}.previous`)"
        v-bind="uiProps.previous"
        @click="emits('previous')"
      />
      <q-btn
        :label="t(`${localI18nScope}.today`)"
        v-bind="uiProps.today"
        @click="emits('today')"
      />
      <q-btn
        :label="t(`${localI18nScope}.next`)"
        v-bind="uiProps.next"
        @click="emits('next')"
      />
    </div>
  </q-toolbar>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";

const props = defineProps({
  calendarView: {
    required: true,
    type: String,
  },
  nameSpace: String,
  i18nScope: String,
  displayDate: String,
});
const emits = defineEmits(['update:calendarView', 'next', 'previous', 'today']);

const {t} = useI18n();
const {ui} = useUiDesign();

const localNameSpace = computed(() => `${props.nameSpace}`);
const localI18nScope = computed(() => `${props.nameSpace}`);


const calendarViews = computed(() => [{
  label: t(`${localI18nScope.value}.calendarView.options.month`),
  value: 'month'
}, {
  label: t(`${localI18nScope.value}.calendarView.options.week`),
  value: 'week'
}, {
  label: t(`${localI18nScope.value}.calendarView.options.day`),
  value: 'day'
}]);

const localCalendarView = ref(props.calendarView);

const uiProps = computed(() => ({
  toolbar: ui(`${localNameSpace.value}`, 'q-toolbar'),
  displayDate: ui(`${localNameSpace.value}`, 'displayDate'),
  actionsContainer: ui(`${localNameSpace.value}`, 'actionsContainer'),
  calendarViewContainer: ui(`${localNameSpace.value}`, 'calendarViewContainer'),
  calendarView: ui(`${localNameSpace.value}`, 'calendarView', 'q-btn-toggle'),
  previous: ui(`${localNameSpace.value}`, 'previous', 'q-btn'),
  today: ui(`${localNameSpace.value}`, 'today', 'q-btn'),
  next: ui(`${localNameSpace.value}`, 'next', 'q-btn'),
}));

watch(() => localCalendarView.value, () => emits('update:calendarView', localCalendarView.value));
</script>

<style scoped>

</style>
