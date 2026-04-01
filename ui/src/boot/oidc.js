import { defineBoot } from '@quasar/app-vite/wrappers';
import { authService } from 'src/services/AuthService.js';
import { useUserStore } from "stores/userStore.js";

export default defineBoot(async () => {
  await authService.init();

  const user = await authService.getUser();

  if (user) {
    const userStore = useUserStore();

    userStore.init(user.profile);
  }
});
