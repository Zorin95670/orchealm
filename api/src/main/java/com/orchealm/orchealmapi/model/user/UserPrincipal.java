package com.orchealm.orchealmapi.model.user;

import com.orchealm.orchealmapi.persistence.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserPrincipal extends User {

    private String globalRoles;

    private String teams;

    public boolean isAdmin() {
        return globalRoles.contains("\"ADMIN\"");
    }
}
