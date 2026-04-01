<template>
  <q-card :style="`background-color: ${color}`" v-bind="uiProps.card">
    <q-card-section v-bind="uiProps.clientSection">
      <span :class="`text-${deployment.projectTextColor}`">
        {{ deployment.client }}
      </span>
      <hot-badge v-if="deployment.state === 'HOT'"/>
      <new-badge v-if="deployment.state === 'HOT'"/>
    </q-card-section>
    <q-card-section v-bind="uiProps.versionSection">
      <q-icon v-if="deployment.delayed" v-bind="uiProps.icon"/>
      <span :class="`text-${deployment.projectTextColor}`">
        {{ deployment.version }}
      </span>
    </q-card-section>

    <deployment-status-tooltip :deployment="deployment"/>

    <q-linear-progress
      :color="deployment.projectTextColor"
      :style="{ opacity: inProgress ? 1 : 0 }"
      indeterminate
      v-bind="uiProps.linearProgress"
    />
  </q-card>
</template>

<script setup>
import { computed } from "vue";
import DeploymentStatusTooltip from "components/tooltip/DeploymentStatusTooltip.vue";
import { useUiDesign } from "src/composables/useUiDesign.js";
import { useColor } from "src/composables/useColor.js";
import HotBadge from "components/badge/HotBadge.vue";
import NewBadge from "components/badge/NewBadge.vue";

const props = defineProps({
  deployment: Object,
});

const inProgress = computed(() => props.deployment.status === 'IN_PROGRESS');

const {ui} = useUiDesign();
const {toRGBA} = useColor();

const namespace = 'DeploymentCard';

const uiProps = {
  card: ui(namespace, 'q-card'),
  clientSection: ui(namespace, 'clientSection', 'q-card-section'),
  versionSection: ui(namespace, 'versionSection', 'q-card-section'),
  icon: ui(namespace, 'q-icon'),
  linearProgress: ui(namespace, 'q-linear-progress'),
  badgeNew: ui('DeploymentBadge', 'new', 'q-badge'),
  badgeHot: ui('DeploymentBadge', 'hot', 'q-badge'),
  iconNew: ui('DeploymentBadge', 'new-icon', 'q-icon'),
  iconHot: ui('DeploymentBadge', 'hot-icon', 'q-icon')
};

const color = computed(() => `rgb(${toRGBA(props.deployment.projectColor, 1 - props.deployment.colorIndex * 0.1).join(',')})`);
</script>

<style scoped>
</style>
