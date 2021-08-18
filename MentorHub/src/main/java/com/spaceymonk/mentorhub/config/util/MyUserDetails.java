package com.spaceymonk.mentorhub.config.util;

import com.spaceymonk.mentorhub.domain.Role;
import com.spaceymonk.mentorhub.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * Custom UserDetails class for implementation of LDAP based login.
 *
 * @author spaceymonk
 * @version 1.0, 08/17/21
 */
public class MyUserDetails implements UserDetails {

    private final User user;

    /**
     * Instantiates a new My user details.
     *
     * @param user the user
     */
    public MyUserDetails(User user) {
        this.user = user;
    }

    /**
     * Get roles/authorities of this user.
     *
     * @return roles of this user
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = user.getRoles();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return authorities;
    }

    /**
     * Get this user's password.
     * Password will be encoded as defined in LDAP configuration.
     *
     * @return password of this user
     * @see com.spaceymonk.mentorhub.config.SecurityConfigurer
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Get this user's username.
     *
     * @return username of this user
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Checks if this user account has expired.
     *
     * @return always true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Checks if this user account is not locked.
     *
     * @return always true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Checks if this user account credentials has not yet expired.
     *
     * @return always true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Checks if this user is enabled or not.
     * In current implementation (v1.0) all users are enabled.
     *
     * @return user is enabled or not
     */
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

}
