package com.spaceymonk.mentorhub.config.util;

import com.spaceymonk.mentorhub.domain.Role;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.RoleRepository;
import com.spaceymonk.mentorhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;


/**
 * Load user details from database and map into OidcUser class for users logged in with Google Auth.
 * Sets roles as saved in the database.
 *
 * @author spaceymonk
 * @version 1.0, 08/17/21
 */
@Component
public class CustomOidcUserService extends OidcUserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    /**
     * Sets user repository.
     *
     * @param userRepository the user repository
     */
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Sets role repository.
     *
     * @param roleRepository the role repository
     */
    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // Delegate to the default implementation for loading a user
        OidcUser oidcUser = super.loadUser(userRequest);
        OAuth2AccessToken accessToken = userRequest.getAccessToken();
        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

        User user = userRepository.findByGoogleId(oidcUser.getName());
        if (user == null) {
            // register user to db
            user = new User();
            user.setActualName(oidcUser.getFullName());
            user.setGoogleId(oidcUser.getName());
            user.setEnabled(true);
            user.setEmail(oidcUser.getEmail());
            Role role_user = roleRepository.findByName("ROLE_USER");
            user.setRoles(Set.of(role_user));
            userRepository.save(user);
        }
        user.getRoles().forEach(role -> {
            mappedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        });

        oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
        return oidcUser;
    }
}
