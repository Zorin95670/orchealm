import { acceptHMRUpdate, defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    email: '',
    name: '',
    globalRoles: [],
    givenName: '',
    familyName: '',
    teams: [],
  }),

  getters: {},

  actions: {
    init(profile) {
      this.email = profile.email;
      this.name = profile.name;
      this.globalRoles = profile.global_roles;
      this.familyName = profile.family_name;
      this.givenName = profile.given_name;
      this.teams = profile.teams;
    },
  }
})

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot))
}
