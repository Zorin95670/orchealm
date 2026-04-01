<template>
  <q-list v-bind="uiProps.list">
    <q-item v-bind="uiProps.item">
      <q-item-section v-bind="uiProps.itemSection">
        <q-select
          v-model="currentLocale"
          :label="t('application.language')"
          :options="locales"
          v-bind="uiProps.language"
        >
          <template v-slot:prepend>
            <q-icon name="flag"/>
          </template>
          <template #option="scope">
            <q-item v-bind="scope.itemProps">
              <q-item-section avatar>
                <q-img :src="`icons/${scope.opt}.svg`" width="32px"/>
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ scope.opt }}</q-item-label>
              </q-item-section>
            </q-item>
          </template>
        </q-select>
      </q-item-section>
    </q-item>
    <q-item v-bind="uiProps.item">
      <q-item-section v-bind="uiProps.itemSection">
        <q-select
          v-model="darkMode"
          :label="t('application.darkMode')"
          :options="darkModeOptions"
          v-bind="uiProps.darkMode"
          @update:model-value="() => updateDarkMode(darkMode.value)"
        >
          <template v-slot:prepend>
            <q-icon name="contrast"/>
          </template>
        </q-select>
      </q-item-section>
    </q-item>
  </q-list>
</template>

<script setup>
import { useUiDesign } from "src/composables/useUiDesign.js";
import { useI18n } from "vue-i18n";
import { computed, ref } from "vue";
import { useDarkMode } from "src/composables/useDarkMode.js";

const {mode, updateDarkMode} = useDarkMode();
const {t} = useI18n();
const {ui} = useUiDesign();
const i18n = useI18n();

const darkModeOptions = computed(() => [{
  label: t('application.darkModeOptions.on'),
  value: 'on',
}, {
  label: t('application.darkModeOptions.off'),
  value: 'off',
}, {
  label: t('application.darkModeOptions.auto'),
  value: 'auto',
}]);
const darkMode = ref(darkModeOptions.value.find(({value}) => value === mode))
const currentLocale = ref(i18n.locale.value);
const locales = computed(() => i18n.availableLocales);

const nameSpace = 'Layouts.MainLayout.ApplicationSettingsList';
const uiProps = {
  language: ui(nameSpace, 'language', 'q-select'),
  darkMode: ui(nameSpace, 'darkMode', 'q-select'),
  list: ui(nameSpace, 'q-list'),
  item: ui(nameSpace, 'q-item'),
  itemSection: ui(nameSpace, 'q-item-section'),
  itemSectionAvatar: ui(nameSpace, 'q-item-section-avatar', 'q-item-section')
}
</script>

<style scoped>

</style>
