import { defineStore } from 'pinia'

export const useUiStore = defineStore('ui', {
  state: () => ({
    title: {
      label: '',
      value: '',
      isLoading: false,
    }
  }),

  getters: {},

  actions: {
    setTitle(label, value = '', isLoading = false) {
      this.title = {label, value, isLoading};
    }
  }
});
