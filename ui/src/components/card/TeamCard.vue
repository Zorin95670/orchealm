<template>
  <q-card v-bind="uiProps.card">
    <q-card-section v-bind="uiProps.titleCardSection">
      <div class="text-h6">{{ team.name }}</div>
      <div class="text-subtitle2">
        {{ team.key }}
        <public-badge v-if="team.isPublic"/>
      </div>
    </q-card-section>
    <q-card-section v-if="team.description" v-bind="uiProps.descriptionCardSection">
      {{ team.description }}
    </q-card-section>
    <q-card-actions v-bind="uiProps.cardActions">
      <q-btn
        v-if="isEditor"
        :label="t(`${localI18nScope}.edit`)"
        v-bind="uiProps.edit"
        @click="emits('edit')"
      />
      <q-btn
        :label="t(`${localI18nScope}.see`)"
        v-bind="uiProps.see"
        @click="router.push(`/teams/${team.key}`)"
      />
    </q-card-actions>
  </q-card>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";
import { useRouter } from "vue-router";
import PublicBadge from "components/badge/PublicBadge.vue";
import { useAcl } from "vue-simple-acl";

const props = defineProps({
  i18nScope: String,
  nameSpace: String,
  team: Object,
});
const emits = defineEmits(['edit']);
const localI18nScope = computed(() => `${props.i18nScope}.TeamCard`);
const localNameSpace = computed(() => `${props.nameSpace}.TeamCard`);

const router = useRouter();
const {t} = useI18n();
const {ui} = useUiDesign();
const acl = useAcl();

const uiProps = {
  card: ui(localNameSpace.value, 'q-card'),
  titleCardSection: ui(localNameSpace.value, 'title-card-section', 'q-card-section'),
  descriptionCardSection: ui(localNameSpace.value, 'description-card-section', 'q-card-section'),
  cardActions: ui(localNameSpace.value, 'q-card-actions'),
  edit: ui(localNameSpace.value, 'edit', 'q-btn'),
  see: ui(localNameSpace.value, 'see', 'q-btn'),
};

const isEditor = computed(() => acl.can('edit-team', {teamKey: props.team?.key}));
</script>

<style scoped>

</style>
