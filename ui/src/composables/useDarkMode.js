import { useQuasar } from "quasar";

export function useDarkMode() {
  const $q = useQuasar();
  const mode = window.localStorage.getItem('dark') || 'auto';

  function updateDarkMode(mode) {
    window.localStorage.setItem("dark", mode);

    if (mode === 'on') {
      $q.dark.set(true);
    } else if (mode === 'off') {
      $q.dark.set(false);
    } else {
      $q.dark.set('auto');
    }
  }

  function initDarkMode() {
    updateDarkMode(mode);
  }

  return {
    initDarkMode,
    updateDarkMode,
    mode,
  }
}
