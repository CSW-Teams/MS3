package org.cswteams.ms3.enums;

import lombok.RequiredArgsConstructor;
import org.cswteams.ms3.entity.TenantUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

/**
 * This enumeration provides all the <i>roles</i> that an <code>TenantUser</code> can have.
 *
 * @see <a href="https://github.com/CSW-Teams/MS3/wiki#attori">Glossary</a>.
 * @see TenantUser
 * @see org.cswteams.ms3.control.actors.ISystemActorController
 */
@RequiredArgsConstructor
public enum SystemActor {
    DOCTOR(),

    PLANNER(),

    CONFIGURATOR();

    public List<SimpleGrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.name()));
    }
}
