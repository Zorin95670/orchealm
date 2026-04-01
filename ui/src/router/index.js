import { defineRouter } from '#q-app/wrappers'
import { createMemoryHistory, createRouter, createWebHashHistory, createWebHistory } from 'vue-router'
import routes from './routes'
import { authService } from "src/services/AuthService.js";

const PUBLIC_PATHS = ['/callback', '/silent-renew', '/logout'];

export default defineRouter((/* { store, ssrContext } */) => {
  const createHistory = process.env.SERVER
    ? createMemoryHistory
    : (process.env.VUE_ROUTER_MODE === 'history' ? createWebHistory : createWebHashHistory)

  const Router = createRouter({
    scrollBehavior: () => ({left: 0, top: 0}),
    routes,

    // Leave this as is and make changes in quasar.conf.js instead!
    // quasar.conf.js -> build -> vueRouterMode
    // quasar.conf.js -> build -> publicPath
    history: createHistory(process.env.VUE_ROUTER_BASE)
  })

  Router.beforeEach(async (to) => {
    if (PUBLIC_PATHS.includes(to.path)) {
      return true;
    }

    const user = await authService.getUser();

    if (!user) {
      await authService.login(to.fullPath);
      return false;
    }

    return true;
  });

  return Router
})
