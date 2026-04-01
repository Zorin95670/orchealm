<template>
  <q-tooltip class="deployment-status-tooltip" v-bind="uiProps.tooltip">
    <div :class="`title-section title-section--${status}`">
      <q-icon v-if="uiProps.icon[status].name" v-bind="uiProps.icon[status]"/>
      {{ t(`${i18nScope}.title.${status}`) }}
    </div>

    <template v-for="section in sections" :key="section.key">
      <div
        v-if="section.enabled"
        :class="`content-section content-section--${section.key}`"
      >
        <span class="title">
          {{ t(`${i18nScope}.${section.key}`) }}
        </span>
        <span class="content">
          {{ section.value }}
        </span>
      </div>
    </template>
  </q-tooltip>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { computed } from "vue";
import { date } from 'quasar'
import { useUiDesign } from "src/composables/useUiDesign.js";

const props = defineProps({
  deployment: Object,
});

const {t} = useI18n();
const {ui} = useUiDesign();

const i18nScope = 'DeploymentStatusTooltip';
const nameSpace = 'DeploymentStatusTooltip';

const sections = computed(() => [{
  key: 'start',
  value: formatDate(props.deployment.actualPeriod.start),
  enabled: true,
}, {
  key: 'end',
  value: formatDate(props.deployment.actualPeriod.end),
  enabled: isCompleted.value,
}, {
  key: 'duration',
  value: getDuration(props.deployment.actualPeriod.start, props.deployment.actualPeriod.end),
  enabled: isCompleted.value,
}, {
  key: 'plannedEnd',
  value: formatDate(props.deployment.plannedPeriod.end),
  enabled: isInProgress.value || isDelay.value,
}, {
  key: 'elapsedTime',
  value: getDuration(props.deployment.actualPeriod.start, new Date()),
  enabled: isInProgress.value || isDelay.value,
}, {
  key: 'delay',
  value: getDuration(props.deployment.plannedPeriod.end, new Date()),
  enabled: isDelay.value,
}]);

const uiProps = {
  tooltip: ui(nameSpace, 'q-tooltip'),
  icon: {
    COMPLETED: ui(nameSpace, 'icon.COMPLETED', 'q-icon'),
    IN_PROGRESS: ui(nameSpace, 'icon.IN_PROGRESS', 'q-icon'),
    DELAY: ui(nameSpace, 'icon.DELAY', 'q-icon'),
  },
};

const status = computed(() => props.deployment.delayed ? 'DELAY' : props.deployment.status);
const isCompleted = computed(() => status.value === 'COMPLETED');
const isInProgress = computed(() => status.value === 'IN_PROGRESS');
const isDelay = computed(() => props.deployment.delayed);

function formatDate(value) {
  if (!value) {
    return '-'
  }

  return date.formatDate(value, t('application.dateTimeFormat'))
}

function getDuration(start, end) {
  if (!start || !end) {
    return '-'
  }

  const diff = new Date(end) - new Date(start)

  const totalMinutes = Math.floor(diff / (1000 * 60))

  const hours = Math.floor(totalMinutes / 60)
  const minutes = totalMinutes % 60

  const parts = []

  if (hours > 0) {
    parts.push(`${hours} ${t('application.hour', hours)}`)
  }

  if (minutes > 0 || hours === 0) {
    parts.push(`${minutes} ${t('application.minute', minutes)}`)
  }

  return parts.join(' ')
}

</script>

<style scoped>

</style>
