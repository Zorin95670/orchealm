package com.orchealm.orchealmapi.service;

import com.orchealm.orchealmapi.persistence.model.User;

public interface UserService {
    User createOrUpdateUser(String externalId, String name, String email);
}
