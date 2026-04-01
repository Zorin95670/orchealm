import { api } from "boot/axios.js";

export async function getLatestDeployments(filters) {
  const deployments = [];
  const pagination = {
    page: 0,
    size: 50,
  };
  let next = true;

  do {
    const response = await api.get('/deployments/last', {params: {...filters, ...pagination}});
    deployments.push(...response.data.content);

    pagination.page += 1;
    next = !response.data.last;
  } while (next);

  return deployments;
}

export async function getDeployments(filters, pagination) {
  return api.get('/deployments', {params: {...filters, ...pagination}})
    .then(({data}) => data);
}

export async function plannedDeployment(deployment) {
  return api.post('/deployments', deployment).then(({data}) => data);
}
