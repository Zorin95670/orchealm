<template>
  <q-card v-bind="uiProps.card">
    <q-card-section v-bind="uiProps.titleSection">
      {{ t(`${localI18nScope}.title`) }}
    </q-card-section>
    <q-card-section v-bind="uiProps.visibilitySection">
      <p v-if="!isEditor" v-bind="uiProps.disclaimer">
        {{ t(`${localI18nScope}.disclaimer`) }}
      </p>
      <q-toggle
        v-model="visibility"
        :disable="!isEditor"
        :label="t(`${localI18nScope}.visibility.label`, '')"
        v-bind="uiProps.visibility"
      />
    </q-card-section>
    <q-card-actions v-if="isEditor" v-bind="uiProps.actions">
      <q-btn
        :disable="visibility === team?.isPublic || false"
        :label="t(`${localI18nScope}.confirm`, '')"
        v-bind="uiProps.confirm"
        @click="openConfirmationDialog"
      />
    </q-card-actions>
  </q-card>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";
import { computed, ref, watch } from "vue";
import { useAcl } from "vue-simple-acl";
import UiEvent from "src/events/UiEvent.js";
import { changeTeamVisibility } from "src/services/TeamService.js";

const props = defineProps({
  team: Object,
  nameSpace: String,
  i18nScope: String,
});
const emits = defineEmits(['update:visibility']);

const {t} = useI18n();
const {ui} = useUiDesign();
const acl = useAcl();

const visibility = ref(props.team?.isPublic || false);

const localNameSpace = computed(() => `${props.nameSpace}.TeamVisibilityCard`);
const localI18nScope = computed(() => `${props.i18nScope}.TeamVisibilityCard`);
const uiProps = computed(() => ({
  card: ui(localNameSpace.value, 'q-card'),
  actions: ui(localNameSpace.value, 'q-card-actions'),
  disclaimer: ui(localNameSpace.value, 'disclaimer', 'p'),
  titleSection: ui(localNameSpace.value, 'q-card-section-title', 'q-card-section'),
  visibilitySection: ui(localNameSpace.value, 'q-card-section-visibility', 'q-card-section'),
  confirm: ui(localNameSpace.value, 'confirm', 'q-btn'),
}));

const isEditor = computed(() => acl.can('edit-team-visibility'));

watch(() => props.team, () => {
  visibility.value = props.team?.isPublic || false;
});

function openConfirmationDialog() {
  const label = visibility.value ? "Public" : "Private";
  UiEvent.next({
    key: 'ConfirmationDialog',
    data: {
      type: 'open',
      nameSpace: `${props.nameSpace}.ChangeTeam${label}VisibilityDialog`,
      i18nScope: `${props.i18nScope}.ChangeTeam${label}VisibilityDialog`,
      title: t(`${props.i18nScope}.ChangeTeam${label}VisibilityDialog.title`),
      content: t(`${props.i18nScope}.ChangeTeam${label}VisibilityDialog.content`),
      onConfirm: changeVisibility,
    }
  });
}

function changeVisibility() {
  changeTeamVisibility(props.team.key, visibility.value)
    .then(() => {
      emits('update:visibility', visibility.value);
    });
}
</script>

<style scoped>

</style>
