import { api } from "boot/axios.js";

export async function getEnvironmentsByTeamKey(teamKey, optionalFilters) {
  const environments = [];
  const filters = {teamKey, ...optionalFilters};
  const pagination = {
    page: 0,
    size: 20,
    sort: 'position',
    direction: 'asc'
  };
  let next = true;

  do {
    const data = await getEnvironments(filters, pagination);
    environments.push(...data.content);

    next = false;
  } while (next);

  return environments;
}

export async function getEnvironments(filters, pagination) {
  return api.get('/environments', {params: {...filters, ...pagination}})
    .then(({data}) => data);
}

export async function createEnvironment(environment) {
  return api.post('/environments', environment)
    .then(({data}) => data);
}

export async function updateEnvironment(id, environment) {
  return api.put(`/environments/${id}`, environment)
    .then(({data}) => data);
}

export async function deleteEnvironment(id) {
  return api.delete(`/environments/${id}`).then(() => {
  });
}
