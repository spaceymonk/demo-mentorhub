package com.spaceymonk.mentorhub.config.util;

import com.spaceymonk.mentorhub.domain.Role;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.RoleRepository;
import com.spaceymonk.mentorhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;


/**
 * Load user details from database and map into MyUserDetails class for users logged in with LDAP.
 *
 * @author spaceymonk
 * @version 1.0, 08/17/21
 */
@Component
public class MyUserDetailsContextMapper implements UserDetailsContextMapper {

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
    public UserDetails mapUserFromContext(DirContextOperations dirContextOperations, String username, Collection<? extends GrantedAuthority> collection) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            Role role_user = roleRepository.findByName("ROLE_USER");
            user = new User();
            user.setEnabled(true);
            user.setRoles(Set.of(role_user));
            user.setActualName(dirContextOperations.getStringAttribute("cn"));
            user.setEmail(dirContextOperations.getStringAttribute("mail"));
            user.setUsername(username);
            user.setPassword(new String((byte[]) dirContextOperations.getObjectAttribute("userPassword")));
            userRepository.save(user);
        }

        return new MyUserDetails(user);
    }

    @Override
    public void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {
        // not implemented, because no sign-up
    }
}
