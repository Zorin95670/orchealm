<template>
  <div
    :data-color-scheme="$q.dark.isActive ? 'dark' : 'light'"
    v-bind="uiProps.container"
  >
    <calendar-toolbar
      v-model:calendar-view="calendarView"
      :display-date="displayDate"
      :i18n-scope="localI18nScope"
      :name-space="localNameSpace"
      @next="calendarRef.getApi().next()"
      @previous="calendarRef.getApi().prev()"
      @today="calendarRef.getApi().today()"
    />
    <full-calendar
      ref="calendarRef"
      :options="calendarOptions"
      v-bind="uiProps.fullCalendar"
    />
    <calendar-menu
      ref="dateMenuRef"
      v-model="dateMenuVisibility"
      v-model:display-date="clickedDateInfo"
      :i18n-scope="i18nScope"
      :name-space="nameSpace"
      @planned="(event) => emits('planned', event)"
    />
  </div>
</template>

<script setup>
import FullCalendar from "@fullcalendar/vue3";
import dayGridPlugin from "@fullcalendar/vue3/daygrid";
import interactionPlugin from "@fullcalendar/vue3/interaction";
import themePlugin from '@fullcalendar/vue3/themes/classic';
import timeGridPlugin from "@fullcalendar/vue3/timegrid";
import '@fullcalendar/vue3/skeleton.css';
import '@fullcalendar/vue3/themes/classic/theme.css';
import '@fullcalendar/vue3/themes/classic/palette.css';
import CalendarToolbar from "components/calendar/CalendarToolbar.vue";
import { capitalize, computed, nextTick, ref, watch } from 'vue';
import { useI18n } from "vue-i18n";
import { useQuasar } from "quasar";
import { useUiDesign } from "src/composables/useUiDesign.js";
import CalendarMenu from "components/calendar/CalendarMenu.vue";

const props = defineProps({
  deployments: Array,
  displayFields: Array,
  nameSpace: String,
  i18nScope: String,
});

const emits = defineEmits(['change', 'planned']);

const {locale} = useI18n();
const {ui} = useUiDesign();
const $q = useQuasar();

const currentDate = ref(new Date());
const currentDateStart = ref(new Date())
const currentDateEnd = ref(new Date())
const calendarRef = ref();
const calendarView = ref('month');
const dateMenuVisibility = ref(false);
const dateMenuRef = ref()
const clickedDateInfo = ref(null)

const localNameSpace = computed(() => `${props.nameSpace}.Calendar`);
const localI18nScope = computed(() => `${props.i18nScope}.Calendar`);

const formatterMonthYear = new Intl.DateTimeFormat(locale.value, {
  month: 'long',
  year: 'numeric'
});

const formatterDay = new Intl.DateTimeFormat(locale.value, {
  day: 'numeric',
  month: 'long',
  year: 'numeric'
});

const uiProps = computed(() => ({
  container: ui(localNameSpace.value, 'container'),
  fullCalendar: ui(localNameSpace.value, 'full-calendar'),
}));

const events = computed(() => props.deployments.map(deployment => {
  const environment = props.displayFields.includes('environment') ? `[${deployment.environmentShortName}] ` : '';
  const project = props.displayFields.includes('project') ? `${deployment.projectName} ` : '';
  const client = props.displayFields.includes('client') ? `${deployment.client}` : '';
  const separator = props.displayFields.includes('project') && props.displayFields.includes('client') ? '- ' : '';

  return {
    id: deployment.id,
    title: `${environment}${project}${separator}${client}`.trim(),
    start:
      deployment.status === 'COMPLETED'
        ? deployment.actualPeriodStart
        : deployment.plannedPeriodStart,
    end:
      deployment.status === 'COMPLETED'
        ? deployment.actualPeriodEnd
        : deployment.plannedPeriodEnd,
    color:
      deployment.status === 'COMPLETED'
        ? '#21ba45'
        : '#f2c037'
  };
}));

const calendarOptions = computed(() => {
  return {
    colorScheme: $q.dark.isActive ? 'dark' : 'light',
    height: '100%',
    plugins: [
      themePlugin,
      dayGridPlugin,
      timeGridPlugin,
      interactionPlugin,
    ],
    initialView: 'dayGridMonth',
    events: events.value,
    datesSet(info) {
      currentDate.value = info.view.currentStart;
      currentDateStart.value = info.start;
      currentDateEnd.value = info.end;
      reloadData();
    },
    dateClick(info) {
      clickedDateInfo.value = info.date;

      if (dateMenuVisibility.value) {
        dateMenuVisibility.value = false;
        nextTick(() => {
          dateMenuRef.value.show(info.jsEvent)
        })
      } else {
        dateMenuRef.value.show(info.jsEvent)
      }
    }
  };
});

const displayDate = computed(() => {
  switch (calendarView.value) {
    case 'month':
      return capitalize(
        formatterMonthYear.format(currentDateStart.value)
      )

    case 'week':
      return `${capitalize(
        new Intl.DateTimeFormat(locale.value, {
          day: 'numeric',
          month: 'long'
        }).format(currentDateStart.value)
      )} - ${capitalize(
        new Intl.DateTimeFormat(locale.value, {
          day: 'numeric',
          month: 'long',
          year: 'numeric'
        }).format(
          new Date(currentDateEnd.value.getTime() - 1)
        )
      )}`;

    default:
      return capitalize(
        formatterDay.format(currentDateStart.value)
      );
  }
})

watch(() => calendarView.value, () => {
  if (calendarView.value === 'week') {
    calendarRef.value.getApi().changeView('timeGridWeek');
  } else if (calendarView.value === 'day') {
    calendarRef.value.getApi().changeView('timeGridDay');
  } else {
    calendarRef.value.getApi().changeView('dayGridMonth');
  }

});

function reloadData() {
  emits('change', {
    start: currentDateStart.value.getTime(),
    end: currentDateEnd.value.getTime()
  });
}
</script>

<style>
.synchronized-color {
  background-color: var(--fc-classic-background);
  border: 1px solid var(--fc-classic-border);
  flex-shrink: 0;
}

.fc {
  flex: 1;
  min-height: 0; /* indispensable en flexbox, sinon le contenu interne force le débordement */
}
</style>
