<template>
  <q-list v-bind="uiProps.list">
    <q-item v-bind="uiProps.item">
      <q-item-section v-bind="uiProps.itemSection">
        {{ appConfig.name }} {{ appConfig.version }}
      </q-item-section>
    </q-item>
    <q-item v-bind="uiProps.item">
      <q-item-section v-bind="uiProps.itemSection">
        Environment: {{ appConfig.environment }}
      </q-item-section>
    </q-item>
    <q-item v-bind="uiProps.item">
      <q-item-section v-bind="uiProps.itemSection">
        <a href="https://www.apache.org/licenses/LICENSE-2.0" target="_blank">Apache License version 2.0</a>
      </q-item-section>
    </q-item>
    <q-item>
      <q-item-section avatar v-bind="uiProps.itemSectionAvatar">
        <q-img
          fit="contain"
          height="16px"
          src="icons/github.webp"
          width="16px"
        />
      </q-item-section>

      <q-item-section v-bind="uiProps.itemSection">
        <a href="https://github.com/Zorin95670/orchealm" target="_blank">
          Github project
        </a>
        <span class="q-mx-xs">by</span>
        <a href="https://github.com/Zorin95670" target="_blank">
          Zorin95670
        </a>
      </q-item-section>
    </q-item>
  </q-list>
</template>

<script setup>
import { useUiDesign } from "src/composables/useUiDesign.js";
import { appConfig } from "boot/config.js";

const {ui} = useUiDesign();

const nameSpace = 'Layouts.MainLayout.ApplicationInformationList';
const uiProps = {
  list: ui(nameSpace, 'q-list'),
  item: ui(nameSpace, 'q-item'),
  itemSection: ui(nameSpace, 'q-item-section'),
  itemSectionAvatar: ui(nameSpace, 'q-item-section-avatar', 'q-item-section')
}
</script>

<style scoped>

</style>
