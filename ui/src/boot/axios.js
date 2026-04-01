import axios from 'axios';
import { defineBoot } from '#q-app/wrappers';
import { authService } from "src/services/AuthService.js";
import { Notify } from 'quasar';

let vueApp;


const api = axios.create({baseURL: '/api', timeout: 30000});

api.interceptors.request.use(async (config) => {
  const token = await authService.getAccessToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      await authService.login();
    }

    // Optimistic locking: the item was updated by someone else since it was loaded.
    if (error.response?.status === 409) {
      Notify.create({
        type: 'warning',
        message: vueApp.config.globalProperties.$t('default.errors.conflict'),
      });
    }

    return Promise.reject(error);
  }
);

export default defineBoot(({app}) => {
  // i18n is installed by a later boot file, $t is resolved when an error occurs.
  vueApp = app;
});

export { api };
