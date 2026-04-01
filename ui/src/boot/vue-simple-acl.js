import { defineBoot } from '#q-app/wrappers'
import { useUserStore } from 'src/stores/userStore.js';
import { createAcl, defineAclRules } from "vue-simple-acl";

function isAdmin(user) {
  return user.globalRoles?.includes('ADMIN');
}

export default defineBoot(async ({app, router}) => {
  const userStore = useUserStore();

  const rules = defineAclRules((setRule) => {
    setRule('create-team', isAdmin);
    setRule('edit-team-visibility', isAdmin);
    setRule('edit-team', (user, {teamKey}) => isAdmin(user) || user.teams.includes(`${teamKey}:EDITOR`));
  });

  const acl = createAcl({
    user: userStore,
    rules,
    router,
  });

  app.use(acl);
});
