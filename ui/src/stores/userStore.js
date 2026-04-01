import { acceptHMRUpdate, defineStore } from 'pinia'
import { authService } from 'src/services/AuthService.js';

export const useUserStore = defineStore('user', {
  state: () => ({
    email: '',
    name: '',
    roles: [],
    givenName: '',
    familyName: '',
    managerOf: [],
    viewerOf: [],
    deployerOf: [],
  }),

  getters: {},

  actions: {
    init(profile) {
      this.email = profile.email;
      this.name = profile.name;
      this.familyName = profile.family_name;
      this.givenName = profile.given_name;

      const claims = authService.claims;
      this.roles = profile[claims.roles] ?? [];
      this.managerOf = profile[claims.managerOf] ?? [];
      this.viewerOf = profile[claims.viewerOf] ?? [];
      this.deployerOf = profile[claims.deployerOf] ?? [];
    },
  }
})

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot))
}
