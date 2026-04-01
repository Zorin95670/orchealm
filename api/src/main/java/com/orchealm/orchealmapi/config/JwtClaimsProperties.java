package com.orchealm.orchealmapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Names of the JWT claims read by the API, bound from {@code jwt.claims.*}.
 *
 * @param externalId claim holding the user's external identifier
 * @param name       claim holding the user's display name
 * @param email      claim holding the user's email
 * @param roles      claim holding the user's global roles (e.g. ADMIN)
 * @param managerOf  claim holding the keys of the teams the user manages
 * @param viewerOf   claim holding the keys of the teams the user can view
 * @param deployerOf claim holding the keys of the teams the user can deploy to
 */
@ConfigurationProperties("jwt.claims")
public record JwtClaimsProperties(String externalId,
                                  String name,
                                  String email,
                                  String roles,
                                  String managerOf,
                                  String viewerOf,
                                  String deployerOf) {
}
