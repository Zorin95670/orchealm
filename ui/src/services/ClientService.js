import { api } from "boot/axios.js";

export function getClients(filters, pagination) {
  return api.get('/clients', {
    params: {
      ...filters,
      ...pagination,
    },
  })
    .then(({data}) => data);
}
