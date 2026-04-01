<template>
  <q-dialog
    :model-value="show"
    v-bind="uiProps.dialog"
    @hide="show = false"
  >
    <q-card
      ref="dialogRef"
      v-bind="uiProps.card"
    >
      <q-form @submit="handleConfirm">
        <q-card-section class="drag-handle" v-bind="uiProps.title">
          <q-icon v-if="uiProps.icon?.name" v-bind="uiProps.icon"/>
          <span v-bind="uiProps.textTitle">
          {{ t(`${i18nScope}.title`) }}
        </span>
        </q-card-section>

        <q-card-section v-bind="uiProps.section">
          <q-select
            v-model="team"
            :hint="t(`${i18nScope}.team.hint`, '')"
            :label="t(`${i18nScope}.team.label`, '')"
            :options="teamOptions"
            :prefix="t(`${i18nScope}.team.prefix`, '')"
            :rules="[required]"
            :suffix="t(`${i18nScope}.team.suffix`, '')"
            v-bind="uiProps.teamSelect"
            @clear="clearTeam"
            @filter="searchTeam"
          />
        </q-card-section>

        <q-card-section v-bind="uiProps.section">
          <q-input
            v-model="startDateDisplay"
            :hint="t(`${i18nScope}.startDate.hint`, '')"
            :label="t(`${i18nScope}.startDate.label`, '')"
            :mask="t('application.mask.date-time')"
            :prefix="t(`${i18nScope}.startDate.prefix`, '')"
            :rules="[required]"
            :suffix="t(`${i18nScope}.startDate.suffix`, '')"
            v-bind="uiProps.startDateInput"
          >
            <template v-slot:prepend>
              <q-icon v-bind="uiProps.startDateIconDate">
                <q-popup-proxy cover transition-hide="scale" transition-show="scale">
                  <q-date
                    v-model="startDate"
                    :options="limitStartDate"
                    v-bind="uiProps.startDateDate"
                  >
                    <div class="row items-center justify-end">
                      <q-btn v-close-popup color="primary" flat label="Close"/>
                    </div>
                  </q-date>
                </q-popup-proxy>
              </q-icon>
            </template>

            <template v-slot:append>
              <q-icon v-bind="uiProps.startDateIconTime">
                <q-popup-proxy cover transition-hide="scale" transition-show="scale">
                  <q-time
                    v-model="startTime"
                    :format24h="t('application.time.use24HourFormat') === 'true'"
                    :options="limitStartTime"
                    v-bind="uiProps.startDateTime"
                  >
                    <div class="row items-center justify-end">
                      <q-btn v-close-popup color="primary" flat label="Close"/>
                    </div>
                  </q-time>
                </q-popup-proxy>
              </q-icon>
            </template>
          </q-input>
          <q-input
            v-model="endDateDisplay"
            :hint="t(`${i18nScope}.endDate.hint`, '')"
            :label="t(`${i18nScope}.endDate.label`, '')"
            :mask="t('application.mask.date-time')"
            :prefix="t(`${i18nScope}.endDate.prefix`, '')"
            :rules="[required, limit]"
            :suffix="t(`${i18nScope}.endDate.suffix`, '')"
            v-bind="uiProps.endDateInput"
          >
            <template v-slot:prepend>
              <q-icon v-bind="uiProps.endDateIconDate">
                <q-popup-proxy cover transition-hide="scale" transition-show="scale">
                  <q-date
                    v-model="endDate"
                    :options="limitEndDate"
                    v-bind="uiProps.endDateDate"
                  >
                    <div class="row items-center justify-end">
                      <q-btn v-close-popup color="primary" flat label="Close"/>
                    </div>
                  </q-date>
                </q-popup-proxy>
              </q-icon>
            </template>

            <template v-slot:append>
              <q-icon v-bind="uiProps.endDateIconTime">
                <q-popup-proxy cover transition-hide="scale" transition-show="scale">
                  <q-time
                    v-model="endTime"
                    :format24h="t('application.time.use24HourFormat') === 'true'"
                    :options="limitEndTime"
                    v-bind="uiProps.endDateTime"
                  >
                    <div class="row items-center justify-end">
                      <q-btn v-close-popup color="primary" flat label="Close"/>
                    </div>
                  </q-time>
                </q-popup-proxy>
              </q-icon>
            </template>
          </q-input>
        </q-card-section>

        <q-card-section v-bind="uiProps.section">
          <q-select
            v-model="project"
            :disable="!team"
            :hint="t(`${i18nScope}.project.hint`, '')"
            :label="t(`${i18nScope}.project.label`, '')"
            :options="projectOptions"
            :prefix="t(`${i18nScope}.project.prefix`, '')"
            :rules="[required]"
            :suffix="t(`${i18nScope}.project.suffix`, '')"
            use-input
            v-bind="uiProps.projectSelect"
            @filter="searchProject"
          />
          <q-select
            v-model="environment"
            :disable="!team"
            :hint="t(`${i18nScope}.environment.hint`, '')"
            :label="t(`${i18nScope}.environment.label`, '')"
            :options="environmentOptions"
            :prefix="t(`${i18nScope}.environment.prefix`, '')"
            :rules="[required]"
            :suffix="t(`${i18nScope}.environment.suffix`, '')"
            use-input
            v-bind="uiProps.environmentSelect"
            @filter="searchEnvironment"
          />
          <q-select
            v-model="client"
            :disable="!team"
            :hint="t(`${i18nScope}.client.hint`, '')"
            :label="t(`${i18nScope}.client.label`, '')"
            :options="clientOptions"
            :prefix="t(`${i18nScope}.client.prefix`, '')"
            :rules="[required]"
            :suffix="t(`${i18nScope}.client.suffix`, '')"
            new-value-mode="add-unique"
            use-input
            v-bind="uiProps.clientSelect"
            @filter="searchClient"
          />
        </q-card-section>
        <q-card-section v-bind="uiProps.section">
          <q-input
            v-model="version"
            :hint="t(`${i18nScope}.version.hint`, '')"
            :label="t(`${i18nScope}.version.label`, '')"
            :prefix="t(`${i18nScope}.version.prefix`, '')"
            :rules="[required]"
            :suffix="t(`${i18nScope}.version.suffix`, '')"
            v-bind="uiProps.version"
          />
        </q-card-section>

        <q-card-actions v-bind="uiProps.actions">
          <q-btn
            :label="t(`${i18nScope}.cancel`, '')"
            v-bind="uiProps.cancel"
            @click="onClose"
          />
          <q-btn
            :label="t(`${i18nScope}.confirm`, '')"
            type="submit"
            v-bind="uiProps.confirm"
          />
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useUiDesign } from "src/composables/useUiDesign.js";
import { useDialog } from "src/composables/useDialog.js";
import { useI18n } from "vue-i18n";
import { date } from 'quasar';
import { getTeams } from "src/services/TeamService.js";
import { getProjects } from "src/services/ProjectService.js";
import { getEnvironments } from "src/services/EnvironmentService.js";
import { getClients } from "src/services/ClientService.js";

const {show} = useDialog('PlannedDeploymentDialog', onOpen);
const nameSpace = 'PlannedDeploymentDialog';
const i18nScope = 'PlannedDeploymentDialog';
let onConfirm;

const {t} = useI18n();
const {ui} = useUiDesign();

const team = ref(null);
const teamOptions = ref([]);
const project = ref(null);
const projectOptions = ref([]);
const environment = ref(null);
const environmentOptions = ref([]);
const client = ref(null);
const clientOptions = ref([]);
const startDate = ref('');
const startTime = ref('');
const endDate = ref('');
const endTime = ref('');
const version = ref('');

const uiProps = computed(() => ({
  dialog: ui(nameSpace, 'q-dialog'),
  icon: ui(nameSpace, 'q-icon'),
  title: ui(nameSpace, 'q-card-section-title', 'q-card-section'),
  textTitle: ui(nameSpace, 'title'),
  card: ui(nameSpace, 'q-card'),
  section: ui(nameSpace, 'q-card-section'),
  actions: ui(nameSpace, 'q-card-actions'),
  confirm: ui(nameSpace, 'confirm', 'q-btn'),
  cancel: ui(nameSpace, 'cancel', 'q-btn'),
  version: ui(nameSpace, 'versionInput', 'q-input'),
  teamSelect: ui(nameSpace, 'teamSelect', 'q-select'),
  projectSelect: ui(nameSpace, 'projectSelect', 'q-select'),
  environmentSelect: ui(nameSpace, 'environmentSelect', 'q-select'),
  clientSelect: ui(nameSpace, 'clientSelect', 'q-select'),
  startDateInput: ui(`${nameSpace}.startDate`, "q-input"),
  startDateDate: ui(`${nameSpace}.startDate`, "q-date"),
  startDateTime: ui(`${nameSpace}.startDate`, "q-time"),
  startDateIconDate: ui(`${nameSpace}.startDate`, 'dateIcon', "q-icon"),
  startDateIconTime: ui(`${nameSpace}.startDate`, 'timeIcon', "q-icon"),
  endDateInput: ui(`${nameSpace}.endDate`, "q-input"),
  endDateDate: ui(`${nameSpace}.endDate`, "q-date"),
  endDateTime: ui(`${nameSpace}.endDate`, "q-time"),
  endDateIconDate: ui(`${nameSpace}.endDate`, 'dateIcon', "q-icon"),
  endDateIconTime: ui(`${nameSpace}.endDate`, 'timeIcon', "q-icon"),
}));

const startDateDisplay = computed({
  get() {
    if (!startDate.value) {
      return '';
    }

    return `${startDate.value} ${startTime.value || '00:00'}`;
  },
  set() {
  }
});

const endDateDisplay = computed({
  get() {
    if (!endDate.value) {
      return '';
    }

    return `${endDate.value} ${endTime.value || '00:00'}`;
  },
  set() {
  }
});

function required(v) {
  return (v && v !== '') || t('default.rules.required');
}

function clearTeam() {
  team.value = null;
  project.value = null;
  projectOptions.value = [];
  environment.value = null;
  environmentOptions.value = [];
  client.value = null;
  clientOptions.value = [];
  version.value = '';
}

function searchTeam(val, update, abort) {
  return getTeams({name: [`lk_*${val}*`]})
    .then((data) => {
      update(() => {
        teamOptions.value = data.content.map((item) => ({
          label: item.name,
          value: item.key,
        }));
      });
    }).catch(() => {
      teamOptions.value = [];
      abort();
    });
}

function searchProject(val, update, abort) {
  return getProjects({
    name: [`lk_*${val}*`],
    teamKey: [team.value.value],
  }).then((data) => {
    update(() => {
      projectOptions.value = data.content.map((item) => ({
        label: `${item.organization} - ${item.name}`,
        name: item.name,
        organization: item.organization,
      }));
    });
  }).catch(() => {
    projectOptions.value = [];
    abort();
  });
}

function searchEnvironment(val, update, abort) {
  return getEnvironments({
    name: [`lk_*${val}*`],
    teamKey: [team.value.value],
  }).then((data) => {
    update(() => {
      environmentOptions.value = data.content.map((item) => ({
        label: item.name,
        value: item.shortName,
      }));
    });
  }).catch(() => {
    environmentOptions.value = [];
    abort();
  });
}

function searchClient(val, update, abort) {
  return getClients({
    client: [`lk_*${val}*`],
  }).then((data) => {
    update(() => {
      clientOptions.value = data.content;
    });
  }).catch(() => {
    clientOptions.value = [];
    abort();
  });
}

async function handleConfirm() {
  await onConfirm({
    teamKey: team.value.value,
    environment: environment.value.value,
    projectOrganization: project.value.organization,
    projectName: project.value.name,
    version: version.value.trim(),
    client: client.value,
    plannedPeriod: {
      start: date.extractDate(
        startDateDisplay.value,
        t('application.dateTimeFormat')
      ).toISOString(),
      end: date.extractDate(
        endDateDisplay.value,
        t('application.dateTimeFormat')
      ).toISOString(),
    },
  });
  show.value = false;
}

function onClose() {
  show.value = false;
}

function onOpen(event) {
  onConfirm = event.onConfirm || (() => Promise.resolve());
  team.value = null;
  startDate.value = date.formatDate(event.date, t('application.dateFormat'));
  startTime.value = date.formatDate(event.date, t('application.timeFormat'));
  endDate.value = date.formatDate(event.date, t('application.dateFormat'));
  endTime.value = date.formatDate(event.date, t('application.timeFormat'));
  project.value = null;
  environment.value = null;
  client.value = null;
  version.value = '';
}

function limitStartDate(d) {
  const today = new Date();

  today.setHours(0, 0, 0, 0);

  const selected = new Date(d);

  return selected >= today;
}

function limitStartTime(hour, minute) {
  if (!startDate.value) {
    return true;
  }

  const now = new Date();

  const selectedDate = new Date(startDate.value);

  const isToday =
    selectedDate.getFullYear() === now.getFullYear() &&
    selectedDate.getMonth() === now.getMonth() &&
    selectedDate.getDate() === now.getDate();

  if (!isToday) {
    return true;
  }

  const selectedMinutes = hour * 60 + minute;
  const currentMinutes = now.getHours() * 60 + now.getMinutes();

  return selectedMinutes > currentMinutes;
}

function limit() {
  if (endDate.value !== startDate.value) {
    return true;
  }

  const [startHour, startMinute] = startTime.value.split(':').map(Number);
  const [hour, minute] = (endTime.value || '00:00').split(':').map(Number);

  const selectedMinutes = hour * 60 + minute;
  const startMinutes = startHour * 60 + startMinute;

  return selectedMinutes > startMinutes || t('default.rules.invalidDate');
}

function limitEndDate(d) {
  if (!startDate.value) {
    return true;
  }

  return d >= startDate.value;
}

function limitEndTime(hour, minute) {
  if (!startDate.value || !startTime.value || !endDate.value) {
    return true;
  }

  if (endDate.value !== startDate.value) {
    return true;
  }

  const [startHour, startMinute] = startTime.value
    .split(':')
    .map(Number);

  const selectedMinutes = hour * 60 + minute;
  const startMinutes = startHour * 60 + startMinute;

  return selectedMinutes > startMinutes;
}
</script>

<style lang="scss" scoped>
.drag-handle {
  cursor: move;
  user-select: none;
}
</style>
