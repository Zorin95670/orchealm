<template>
  <q-layout v-bind="uiProps.layout">

    <q-header v-bind="uiProps.header">
      <q-toolbar v-bind="uiProps.toolbar">
        <q-btn v-bind="uiProps.menuButton" @click="toggleLeftDrawer"/>

        <q-toolbar-title v-bind="uiProps.toolbarTitle">
          <logo v-bind="uiProps.logo"/>
          <span class="q-ml-md">{{ title }}</span>
          <span v-if="uiStore.title.value" class="q-ml-sm">{{ uiStore.title.value }}</span>
          <blur-loader v-if="uiStore.title.isLoading" class="q-ml-sm"/>
        </q-toolbar-title>
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" v-bind="uiProps.drawer">
      <application-menu-list
        :i18n-scope="localI18nScope"
        :name-space="localNameSpace"
      />
      <q-space/>
      <applicatio-settings-list/>
      <q-separator/>
      <application-information-list/>
    </q-drawer>

    <q-page-container>
      <router-view :key="$route.fullPath"/>
    </q-page-container>

    <confirmation-dialog/>
    <add-environment-dialog/>
    <add-project-dialog/>
    <add-team-dialog/>
    <edit-project-dialog/>
    <edit-team-dialog/>

  </q-layout>
</template>

<script setup>
import { computed, ref } from 'vue'
import ApplicationMenuList from "components/list/ApplicationMenuList.vue";
import { useUiDesign } from "src/composables/useUiDesign.js";
import { useI18n } from "vue-i18n";
import ConfirmationDialog from "components/dialog/ConfirmationDialog.vue";
import AddEnvironmentDialog from "components/dialog/AddEnvironmentDialog.vue";
import AddProjectDialog from "components/dialog/AddProjectDialog.vue";
import EditProjectDialog from "components/dialog/EditProjectDialog.vue";
import { useUiStore } from "stores/uiStore.js";
import BlurLoader from "components/loader/BlurLoader.vue";
import ApplicationInformationList from "components/list/ApplicationInformationList.vue";
import ApplicatioSettingsList from "components/list/ApplicationSettingsList.vue";
import Logo from 'assets/logo.svg';
import EditTeamDialog from "components/dialog/EditTeamDialog.vue";
import AddTeamDialog from "components/dialog/AddTeamDialog.vue";

const {t} = useI18n();
const {ui} = useUiDesign();

const leftDrawerOpen = ref(false)

const uiStore = useUiStore();
const localNameSpace = 'Layouts.MainLayout'
const localI18nScope = 'Layouts.MainLayout';
const title = computed(() => t(uiStore.title.label));

const uiProps = {
  avatar: ui(localNameSpace, 'q-avatar'),
  drawer: ui(localNameSpace, 'q-drawer'),
  header: ui(localNameSpace, 'q-header'),
  layout: ui(localNameSpace, 'q-layout'),
  logo: ui(localNameSpace, 'logo'),
  menuButton: ui(localNameSpace, 'menuButton', 'q-btn'),
  toolbar: ui(localNameSpace, 'q-toolbar'),
  toolbarTitle: ui(localNameSpace, 'q-toolbar-title'),
}

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value
}
</script>
