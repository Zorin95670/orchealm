import { api } from "boot/axios.js";

export async function getProjects(filters, pagination) {
  return api.get('/projects', {params: {...filters, ...pagination}})
    .then(({data}) => data);
}

export async function getProjectById(id) {
  return api.get(`/projects/${id}`)
    .then(({data}) => data);
}

export async function createProject(project) {
  return api.post('/projects', project)
    .then(({data}) => data);
}

export async function updateProject(id, project) {
  return api.put(`/projects/${id}`, project)
    .then(({data}) => data);
}

export async function deleteProject(id) {
  return api.delete(`/projects/${id}`).then(() => {
  });
}
