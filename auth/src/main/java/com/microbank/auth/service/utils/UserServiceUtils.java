package com.microbank.auth.service.utils;

import com.microbank.auth.dto.response.UserResponse;
import com.microbank.auth.model.User;
import com.microbank.auth.model.enums.UserRole;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserServiceUtils {

    public UserResponse buildUserResponse(User u) {
        return new UserResponse(
                u.getId(),
                u.getKeycloakId(),
                u.getUsername(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName()
        );
    }

    public List<UserResponse> buildUserResponses(List<User> users) {
        List<UserResponse> userResponses = new ArrayList<>();
        for (User u : users) {
            userResponses.add(buildUserResponse(u));
        }
        return userResponses;
    }

    public User buildUserFromJwt(Jwt jwt) {
        User user = new User();
        user.setKeycloakId(jwt.getSubject());
        user.setUsername(jwt.getClaim("preferred_username"));
        user.setEmail(jwt.getClaim("email"));
        user.setFirstName(jwt.getClaim("given_name"));
        user.setLastName(jwt.getClaim("family_name"));
        user.setActivated(true);
        user.setBanned(false);
        user.setPassword("{keycloak}"); // Password not used for Keycloak users
        user.setRole(UserRole.USER); // default role
        return user;
    }

}
