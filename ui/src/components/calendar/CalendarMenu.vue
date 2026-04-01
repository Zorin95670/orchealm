<template>
  <q-menu
    ref="menuRef"
    v-model="modelValue"
    context-menu
    no-parent-event
    touch-position
    v-bind="uiProps.menu"
  >
    <q-list>
      <q-item-label header v-bind="uiProps.header">
        {{ title }}
      </q-item-label>
      <q-separator v-bind="uiProps.separator"/>
      <q-item v-if="isPast" v-bind="uiProps.noActionItem">
        <q-item-section v-bind="uiProps.noActionItemSection">
          <q-item-label v-bind="uiProps.noActionItemLabel">
            {{ t(`${localI18nScope}.noAction`) }}
          </q-item-label>
        </q-item-section>
      </q-item>
      <template v-else>
        <q-item v-bind="uiProps.actionPlannedItem" @click="planned">
          <q-item-section
            v-if="uiProps.actionPlannedIcon.name"
            avatar
            v-bind="uiProps.actionPlannedIconSection"
          >
            <q-icon v-bind="uiProps.actionPlannedIcon"/>
          </q-item-section>
          <q-item-section v-bind="uiProps.actionPlannedItemSection">
            <q-item-label v-bind="uiProps.actionPlannedItemLabel">
              {{ t(`${localI18nScope}.plannedDeployment`) }}
            </q-item-label>
          </q-item-section>
        </q-item>
      </template>
    </q-list>
  </q-menu>
</template>

<script setup>

import { useI18n } from "vue-i18n";
import { capitalize, computed, ref } from "vue";
import { useUiDesign } from "src/composables/useUiDesign.js";

const props = defineProps({
  displayDate: Date,
  nameSpace: String,
  i18nScope: String,
})

const emits = defineEmits(['planned']);

const {t, locale} = useI18n();
const {ui} = useUiDesign();
const menuRef = ref();
const modelValue = defineModel({type: Boolean, default: false});

defineExpose({
  show: (evt) => menuRef.value.show(evt),
  hide: () => menuRef.value.hide(),
})

const localNameSpace = computed(() => `${props.nameSpace}.CalendarMenu`);
const localI18nScope = computed(() => `${props.i18nScope}.CalendarMenu`);

const formatterDay = new Intl.DateTimeFormat(locale.value, {
  day: 'numeric',
  month: 'long',
  year: 'numeric'
});

const title = computed(() => {
  let date = capitalize(formatterDay.format(props.displayDate));

  return t(`${localI18nScope.value}.title`, {date});
});

const uiProps = computed(() => ({
  menu: ui(localNameSpace.value, 'q-menu'),
  separator: ui(localNameSpace.value, 'q-separator'),
  header: ui(localNameSpace.value, 'header', 'q-item-label'),
  noActionItem: ui(localNameSpace.value, 'noAction-item', 'q-item'),
  noActionItemSection: ui(localNameSpace.value, 'noAction-item-section', 'q-item-section'),
  noActionItemLabel: ui(localNameSpace.value, 'noAction-item-label', 'q-item-label'),
  actionPlannedItem: ui(localNameSpace.value, 'action-planned-item', 'q-item'),
  actionPlannedItemSection: ui(localNameSpace.value, 'action-planned-item-section', 'q-item-section'),
  actionPlannedItemLabel: ui(localNameSpace.value, 'action-planned-item-label', 'q-item-label'),
  actionPlannedIconSection: ui(localNameSpace.value, 'action-planned-iconSection', 'q-item-section'),
  actionPlannedIcon: ui(localNameSpace.value, 'action-planned-icon', 'q-icon')
}));

const isPast = computed(() => {
  if (!props.displayDate) {
    return false;
  }

  const date = new Date(props.displayDate);
  date.setHours(0, 0, 0, 0);

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  return date < today;
});

function planned() {
  emits('planned', props.displayDate);
  menuRef.value.hide();
}
</script>

<style scoped>

</style>
