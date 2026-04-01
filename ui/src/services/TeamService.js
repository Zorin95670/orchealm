import { api } from "boot/axios.js";

export function getTeams(filters, pagination) {
  return api.get('/teams', {
    params: {
      ...filters,
      ...pagination,
    },
  })
    .then(({data}) => data);
}

export function getTeam(key) {
  return api.get(`/teams/${key}`)
    .then(({data}) => data);
}

export function updateTeam(key, team) {
  return api.put(`/teams/${key}`, team)
    .then(({data}) => data);
}

export function createTeam(team) {
  return api.post(`/teams`, team)
    .then(({data}) => data);
}

export function changeTeamVisibility(key, isPublic) {
  return api.put(`teams/${key}/visibility`, {isPublic});
}
