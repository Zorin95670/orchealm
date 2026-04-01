<template>
  <q-list v-bind="uiProps.list">
    <template v-for="item in items" :key="item.key">
      <q-item
        :clickable="item.clickable"
        :to="item.to"
        v-bind="item.uiProps.item"
      >
        <q-item-section
          v-if="item.uiProps.avatar.icon"
          avatar
          v-bind="item.uiProps.itemSectionAvatar"
        >
          <q-avatar v-bind="item.uiProps.avatar"/>
        </q-item-section>
        <q-item-section v-bind="item.uiProps.itemSectionTitle">
          {{ item.label }}
        </q-item-section>
      </q-item>
      <q-item
        v-for="subItem in item.children" :key="`${item.key}.${subItem.key}`"
        :clickable="subItem.clickable"
        :to="subItem.to"
        class="q-ml-lg menu-sub-item"
        v-bind="subItem.uiProps.item"
      >
        <q-item-section
          v-if="subItem.uiProps.avatar.icon"
          avatar
          v-bind="subItem.uiProps.itemSectionAvatar"
        >
          <q-avatar v-bind="subItem.uiProps.avatar"/>
        </q-item-section>
        <q-item-section v-bind="subItem.uiProps.itemSectionTitle">
          {{ subItem.label }}
        </q-item-section>
      </q-item>
    </template>
    <q-space/>
  </q-list>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useUiDesign } from "src/composables/useUiDesign.js";
import { getTeams } from "src/services/TeamService.js";

const props = defineProps({
  i18nScope: String,
  nameSpace: String,
});

const localI18nScope = computed(() => `${props.i18nScope}.ApplicationMenuList`);
const localNameSpace = computed(() => `${props.nameSpace}.ApplicationMenuList`);

const {t} = useI18n();
const {ui} = useUiDesign();

const publicTeams = ref([]);
const privateTeams = ref([]);

const uiProps = {
  list: ui(localNameSpace.value, 'q-list'),
}

const items = computed(() => [{
  key: 'public-releases',
  label: t(`${localI18nScope.value}.public-releases.label`),
  uiProps: {
    avatar: ui(localNameSpace.value, 'public-releases.q-avatar', 'q-avatar'),
    item: ui(localNameSpace.value, 'public-releases.q-item', 'q-item'),
    itemSectionAvatar: ui(localNameSpace.value, 'public-releases.q-item-section-avatar', 'q-item-section'),
    itemSectionTitle: ui(localNameSpace.value, 'public-releases.q-item-section-title', 'q-item-section'),
  },
  clickable: false,
  children: publicTeams.value,
}, {
  key: 'private-releases',
  label: t(`${localI18nScope.value}.private-releases.label`),
  uiProps: {
    avatar: ui(localNameSpace.value, 'private-releases.q-avatar', 'q-avatar'),
    item: ui(localNameSpace.value, 'private-releases.q-item', 'q-item'),
    itemSectionAvatar: ui(localNameSpace.value, 'private-releases.q-item-section-avatar', 'q-item-section'),
    itemSectionTitle: ui(localNameSpace.value, 'private-releases.q-item-section-title', 'q-item-section'),
  },
  clickable: false,
  children: privateTeams.value,
}, {
  key: 'allReleases',
  label: t(`${localI18nScope.value}.allReleases.label`),
  uiProps: {
    avatar: ui(localNameSpace.value, 'allReleases.q-avatar', 'q-avatar'),
    item: ui(localNameSpace.value, 'allReleases.q-item', 'q-item'),
    itemSectionAvatar: ui(localNameSpace.value, 'allReleases.q-item-section-avatar', 'q-item-section'),
    itemSectionTitle: ui(localNameSpace.value, 'allReleases.q-item-section-title', 'q-item-section'),
  },
  clickable: true,
  to: '/all-releases',
  children: []
}, {
  key: 'calendar',
  label: t(`${localI18nScope.value}.calendar.label`),
  uiProps: {
    avatar: ui(localNameSpace.value, 'calendar.q-avatar', 'q-avatar'),
    item: ui(localNameSpace.value, 'calendar.q-item', 'q-item'),
    itemSectionAvatar: ui(localNameSpace.value, 'calendar.q-item-section-avatar', 'q-item-section'),
    itemSectionTitle: ui(localNameSpace.value, 'calendar.q-item-section-title', 'q-item-section'),
  },
  clickable: true,
  to: '/calendar',
  children: []
}, {
  key: 'teams',
  label: t(`${localI18nScope.value}.teams.label`),
  uiProps: {
    avatar: ui(localNameSpace.value, 'teams.q-avatar', 'q-avatar'),
    item: ui(localNameSpace.value, 'teams.q-item', 'q-item'),
    itemSectionAvatar: ui(localNameSpace.value, 'teams.q-item-section-avatar', 'q-item-section'),
    itemSectionTitle: ui(localNameSpace.value, 'teams.q-item-section-title', 'q-item-section'),
  },
  clickable: true,
  to: '/teams',
  children: []
}]);

function initPublicTeams() {
  getTeams({isPublic: [true]}).then((data) => {
    publicTeams.value = data.content.map(({key, name}) => ({
      key,
      label: name,
      uiProps: {
        avatar: ui(localNameSpace.value, 'public-releases.team.q-avatar', 'q-avatar'),
        item: ui(localNameSpace.value, 'public-releases.team.q-item', 'q-item'),
        itemSectionAvatar: ui(localNameSpace.value, 'public-releases.team.q-item-section-avatar', 'q-item-section'),
        itemSectionTitle: ui(localNameSpace.value, 'public-releases.team.q-item-section-title', 'q-item-section'),
      },
      to: `/public-releases/${key}`,
      clickable: true,
    }));
  });
}

function initPrivateTeams() {
  getTeams({isPublic: [false]}).then((data) => {
    privateTeams.value = data.content.map(({key, name}) => ({
      key,
      label: name,
      uiProps: {
        avatar: ui(localNameSpace.value, 'private-releases.team.q-avatar', 'q-avatar'),
        item: ui(localNameSpace.value, 'private-releases.team.q-item', 'q-item'),
        itemSectionAvatar: ui(localNameSpace.value, 'private-releases.team.q-item-section-avatar', 'q-item-section'),
        itemSectionTitle: ui(localNameSpace.value, 'private-releases.team.q-item-section-title', 'q-item-section'),
      },
      to: `/releases/${key}`,
      clickable: true,
    }));
  });
}

onMounted(() => {
  initPublicTeams();
  initPrivateTeams();
});
</script>
