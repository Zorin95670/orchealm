const routes = [
  {
    path: '/callback',
    name: 'AuthenticationCallback',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/AuthenticationCallbackPage.vue'),
      },
    ],
  },
  {
    path: '/logout',
    name: 'AuthenticationLogoutCallback',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/AuthenticationLogoutPage.vue'),
      },
    ],
  },
  {
    path: '/silent-renew',
    name: 'AuthenticationSilentRenewPage',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/AuthenticationSilentRenewPage.vue'),
      },
    ],
  },
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/IndexPage.vue')
      },
      {
        path: 'public-releases/:key',
        component: () => import('pages/LatestDeploymentsPage.vue')
      },
      {
        path: 'releases/:key',
        component: () => import('pages/LatestDeploymentsPage.vue')
      },
      {
        path: 'all-releases',
        component: () => import('pages/DeploymentsPage.vue')
      },
      {
        path: 'calendar',
        component: () => import('pages/DeploymentsCalendarPage.vue')
      },
      {
        path: 'teams',
        component: () => import('pages/TeamsPage.vue')
      },
      {
        path: 'teams/:key',
        component: () => import('pages/TeamDetailPage.vue')
      }
    ],
    meta: {requiresAuth: true},
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue')
  }
]

export default routes
