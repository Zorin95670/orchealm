import { UserManager, WebStorageStateStore, } from 'oidc-client-ts';

class AuthService {
  userManager = null;
  initPromise = null;
  loginPromise = null;

  /**
   * Initializes the OIDC client and creates the underlying {@link UserManager} instance.
   *
   * Multiple calls return the same initialization promise.
   *
   * @returns A promise resolving to the configured {@link UserManager}.
   */
  async init() {
    if (this.initPromise) {
      return this.initPromise;
    }

    this.initPromise = this.buildUserManager();
    return this.initPromise;
  }

  /**
   * Loads the OIDC configuration and creates a configured {@link UserManager} instance.
   *
   * This method also registers authentication event handlers for token expiration, silent renewal failures, and remote
   * sign-out.
   *
   * @returns A promise resolving to the configured {@link UserManager}.
   * @throws {Error} If the OIDC configuration file cannot be loaded.
   */
  async buildUserManager() {
    const response = await fetch('/config/oidc.json');
    if (!response.ok) {
      throw new Error(`Unable to load /config/oidc.json (${response.status})`);
    }
    const config = await response.json();

    const settings = {
      authority: config.authority,
      client_id: config.client_id,
      redirect_uri: config.redirect_uri,
      post_logout_redirect_uri: config.post_logout_redirect_uri,
      silent_redirect_uri: config.silent_redirect_uri,
      response_type: config.response_type,
      scope: config.scope,
      automaticSilentRenew: true,
      loadUserInfo: true,
      userStore: new WebStorageStateStore({store: window.localStorage}),
    };

    const userManager = new UserManager(settings);

    userManager.events.addAccessTokenExpired(() => {
      console.warn('[auth] access token expired, redirecting to login');
      void this.login();
    });

    userManager.events.addSilentRenewError((err) => {
      console.error('[auth] silent renew failed', err);
      void this.login();
    });

    userManager.events.addUserSignedOut(() => {
      void this.login();
    });

    this.userManager = userManager;
    return userManager;
  }

  /**
   * Returns the initialized {@link UserManager}.
   *
   * @returns The configured user manager instance.
   * @throws {Error} If the service has not been initialized.
   */
  getManager() {
    if (!this.userManager) {
      throw new Error('AuthService not initialized, call init() first');
    }
    return this.userManager;
  }

  /**
   * Retrieves the currently authenticated user.
   *
   * @returns The authenticated user, or `null` if no valid session exists or the access token has expired.
   */
  async getUser() {
    const manager = this.getManager();
    const user = await manager.getUser();
    if (!user || user.expired) {
      return null;
    }
    return user;
  }

  /**
   * Initiates the OIDC authentication flow by redirecting the user to the Identity Provider.
   *
   * The current application route is stored in the OIDC state and can be used to restore navigation after successful
   * authentication.
   *
   * This method is protected against concurrent invocations: if a login flow is already in progress, the existing
   * promise is returned instead of triggering a new redirect.
   *
   * @remarks
   *   The returned promise does not represent a post-login completion. The browser will typically be redirected away
   *   before resolution context is useful.
   * @param url Optional return URL to store in the authentication state. If not provided, the current path and query
   *   string are used.
   * @returns A promise that resolves once the redirect request has been initiated.
   */
  async login(url) {
    if (this.loginPromise) {
      return this.loginPromise;
    }

    const manager = this.getManager();

    this.loginPromise = (async () => {
      const redirectUrl =
        url ?? window.location.pathname + window.location.search;

      try {
        await manager.signinRedirect({
          state: {redirectUrl},
        });
      } finally {
        this.loginPromise = null;
      }
    })();

    return this.loginPromise;
  }

  /**
   * Processes the authentication callback returned by the Identity Provider after a successful login.
   *
   * @returns The authenticated user.
   */
  async handleCallback() {
    const manager = this.getManager();
    return manager.signinRedirectCallback();
  }

  /**
   * Processes the silent token renewal callback.
   *
   * This method should be invoked from the page configured as the silent renewal redirect URI.
   *
   * @returns A promise that resolves when the callback has been processed.
   */
  async handleSilentRenewCallback() {
    const manager = this.getManager();
    await manager.signinSilentCallback();
  }

  /**
   * Starts the logout flow by redirecting the user to the Identity Provider logout endpoint.
   *
   * @returns A promise that resolves once the logout redirect has been initiated.
   */
  async logout() {
    const manager = this.getManager();
    await manager.signoutRedirect();
  }

  /**
   * Returns the current access token.
   *
   * @returns The access token if a valid user session exists, otherwise `null`.
   */
  async getAccessToken() {
    const user = await this.getUser();
    return user?.access_token ?? null;
  }
}

export const authService = new AuthService();
