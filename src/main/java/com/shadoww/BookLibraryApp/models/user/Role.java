package com.shadoww.BookLibraryApp.models.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum Role {
//    GUEST,
    USER,
//    USER(List.of(Privilege.PRIVILEGE_ADD_BOOKMARK, Privilege.PRIVILEGE_API_CATALOG, Privilege.PRIVILEGE_API_USER)),
//    MODERATOR,

    ADMIN,
//    ADMIN(Arrays.stream(Privilege.values()).filter(privilege -> !privilege.equals(Privilege.PRIVILEGE_SUPER_ADMIN)).toList()),

    SUPER_ADMIN;

    private List<Privilege> privileges;

    Role() {}
    Role(List<Privilege> privileges) {
        this.privileges = privileges;
    }


/*
    public static Role getRole(List<GrantedAuthority> authorities) {
//        System.out.println("USER ROLES:" + authorities);
        List<Role> roles = Arrays.stream(Role.values()).toList();

        Optional<Role> foundRole = roles.stream().filter(r -> r.hasSameAuthorities(authorities)).findFirst();

        return foundRole.orElse(null);
    }

    public List<GrantedAuthority> getAuthorities() {
        return privileges.stream().map(p->new SimpleGrantedAuthority(p.name())).collect(Collectors.toList());
    }


    public boolean hasSameAuthorities(List<GrantedAuthority> authorities) {
        List<GrantedAuthority> as = this.getAuthorities();

        System.out.println("Порівнювальне: " + (as instanceof ArrayList<GrantedAuthority>));
        System.out.println(as);
        System.out.println("З чим порівнюється: " + (authorities instanceof ArrayList<GrantedAuthority>));
        System.out.println(authorities);

        if (as.size() != authorities.size()) return false;

        for(int i = 0; i < as.size(); i++) {
            String prev = authorities.get(i).getAuthority();
//            String  otherPrev = authorities.get(i).getAuthority();

            if (!as.stream().map(GrantedAuthority::getAuthority).toList().contains(prev)) return false;
        }

        return true;
    }
*/

    public boolean equals(Role role) {
        if (role == null) return false;


        return this.getRoleName().equals(role.getRoleName());
//        return this.privileges.equals(role.privileges);
    }


    public String getRoleName() {
        return  "ROLE_" + name();
    }
    @Override
    public String toString() {
        return "Role{" +
                "name=" + this.name() +
                '}';
    }
}
