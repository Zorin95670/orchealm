import axios from 'axios';
import { defineBoot } from '#q-app/wrappers';
import { authService } from "src/services/AuthService.js";


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

    return Promise.reject(error);
  }
);

export default defineBoot(() => {
});

export { api };
