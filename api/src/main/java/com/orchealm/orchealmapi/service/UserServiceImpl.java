package com.orchealm.orchealmapi.service;

import com.orchealm.orchealmapi.persistence.model.User;
import com.orchealm.orchealmapi.persistence.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createOrUpdateUser(String externalId, String name, String email) {
        User user = userRepository.findByExternalId(externalId)
            .orElseGet(() -> {
                log.info("Creating user '{}' with name '{}' and email '{}'", externalId, name, email);
                return userRepository.save(User.builder()
                    .externalId(externalId)
                    .name(name)
                    .email(email)
                    .build());
            });

        if (!Strings.CS.equals(name, user.getName())
            || !Strings.CS.equals(email, user.getEmail())) {
            log.info("Updating user '{}' with name '{}' and email '{}'", externalId, name, email);
            user.setName(name);
            user.setEmail(email);

            return userRepository.save(user);
        }

        return user;
    }
}
