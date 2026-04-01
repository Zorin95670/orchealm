<template>
  <q-markup-table>
    <thead>
    <tr>
      <th></th>
      <template v-for="(environment) in environments" :key="`${environment.name}`">
        <th :colspan="environment.maxClients || 1">
          <environment-card
            :environment="environment"
            :name-space="nameSpace"
          />
        </th>
      </template>
    </tr>
    </thead>

    <tbody>
    <tr v-for="project in projects" :key="`${project.id}`">
      <td class="col-2">
        <project-card :project="project"/>
      </td>

      <template v-for="(environment) in environments" :key="`${environment.shortName}-group`">
        <template v-if="loading">
          <td :colspan="environment.maxClients || 1">
            <div class="flex flex-center">
              <blur-loader height="lg" width="lg"/>
            </div>
          </td>
        </template>
        <template v-else>
          <td
            v-for="client in environment.clients[project.id]"
            :key="`${project.id}-${environment.shortName}-${client}`"
            :colspan="environment.clients[project.id].length === 1 ? environment.maxClients : 1">
            <deployment-card
              :deployment="deployments[project.id][environment.shortName][client]"
            />
          </td>

          <td
            v-if="(environment.maxClients - environment.clients[project.id]?.length > 0
              && environment.clients[project.id]?.length > 1)
              || environment.clients[project.id]?.length === 0"
            :key="`${project.id}-${environment.shortName}-none`"
            :colspan="environment.maxClients - environment.clients[project.id].length">
          </td>
        </template>
      </template>
    </tr>
    </tbody>
  </q-markup-table>
</template>

<script setup>
import DeploymentCard from "components/card/DeploymentCard.vue";
import ProjectCard from "components/card/ProjectCard.vue";
import EnvironmentCard from "components/card/EnvironmentCard.vue";
import BlurLoader from "components/loader/BlurLoader.vue";

defineProps({
  deployments: Object,
  environments: Array,
  projects: Array,
  nameSpace: String,
  loading: Boolean,
});

</script>

<style scoped>

</style>
