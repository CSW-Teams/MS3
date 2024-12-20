package org.cswteams.ms3.entity;

import lombok.Getter;
import org.cswteams.ms3.enums.SystemActor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom implementation of the {@link UserDetails} interface for integrating
 * the application-specific User entity with Spring Security.
 *
 * <p>
 * This class acts as a wrapper around the {@link User} entity and provides
 * the necessary methods required by Spring Security to handle authentication
 * and authorization processes. It includes the user's credentials, role,
 * and basic information needed by Spring Security.
 *
 * <p>
 * The role of the user is determined by the {@link SystemActor} enum, which is
 * mapped to a {@link SimpleGrantedAuthority} for Spring Security's authorization
 * framework.
 *
 * <p>
 * This class also provides default implementations for account expiration,
 * account locking, credentials expiration, and user enabling statuses,
 * which can be customized according to the system's logic.
 *
 * @author Ralisin
 */
@Getter
public class CustomUserDetails implements UserDetails {
    /**
     * The user entity containing the user's information.
     */
    private final User user;

    /**
     * The system actor associated with the user.
     * Represents the role of the user within the application which will be mapped to roles in Spring Security.
     */
    private final SystemActor systemActor;

    /**
     * Constructs a new CustomUserDetails object wrapping the provided {@link User} entity.
     *
     * @param user The User entity to be wrapped.
     */
    public CustomUserDetails(User user) {
        this.user = user;

        this.systemActor = null;
    }

    /**
     * Constructs a new CustomUserDetails object wrapping the provided {@link User} entity
     * and associates it with a specified {@link SystemActor}.
     *
     * @param user The User entity to be wrapped.
     * @param systemActor The system actor (role) of the user.
     */
    public CustomUserDetails(User user, String systemActor) {
        this.user = user;

        this.systemActor = SystemActor.valueOf(systemActor);
    }

    /**
     * Returns the collection of authorities granted to the user.
     * Each role from the user's {@link SystemActor} set is mapped to a
     * {@link SimpleGrantedAuthority}.
     *
     * <p>
     * If the system actor is not null, a single authority is returned,
     * representing the user's role in the system, prefixed with "ROLE_".
     * If no system actor is assigned, null is returned, meaning the user has
     * no role granted in Spring Security.
     *
     * @return A collection of {@link GrantedAuthority} objects representing the user's role.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (systemActor != null) {
//            return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + this.systemActor));
            return Collections.singleton(new SimpleGrantedAuthority(this.systemActor.toString()));
        }

        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Indicates whether the user's account has expired.
     * This implementation always returns {@code true}, meaning the account is not expired.
     * This method can be customized based on the business logic of your system.
     *
     * @return {@code true} if the account is not expired.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // TODO: Customise according to the logic of your system
    }

    /**
     * Indicates whether the user's account is locked.
     * This implementation always returns {@code true}, meaning the account is not locked.
     * This method can be customized based on the business logic of your system.
     *
     * @return {@code true} if the account is not locked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // TODO: Customise according to the logic of your system
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     * This implementation always returns {@code true}, meaning the credentials are not expired.
     * This method can be customized based on the business logic of your system.
     *
     * @return {@code true} if the credentials are not expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // TODO: Customise according to the logic of your system
    }

    /**
     * Indicates whether the user is enabled.
     * This implementation always returns {@code true}, meaning the user is enabled.
     * This method can be customized based on the business logic of your system.
     *
     * @return {@code true} if the user is enabled.
     */
    @Override
    public boolean isEnabled() {
        return true; // TODO: Customise according to the logic of your system
    }
}
