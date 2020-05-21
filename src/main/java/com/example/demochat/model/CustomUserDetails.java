package com.example.demochat.model;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class CustomUserDetails extends User implements UserDetails {

    private  User user;

    public CustomUserDetails(final User _user) {
        this.user = _user;
    }

    public CustomUserDetails() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();

        list.add(new SimpleGrantedAuthority("ROLE_" + this.user.getRole()));

        return list;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    public String getRole() {
        return this.user.getRole();
    }

    @Override
    public String getUsername() {
        if (this.user == null) {
            return null;
        }
        return this.user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
//        return this.user.isAccountNonExpired();
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
//        return this.user.isAccountNonLocked();
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
//        return this.user.isCredentialsNonExpired();
        return true;
    }


    public boolean belongsToGroup(long id) {

        if (this.user.isGroupOwner(id) || this.user.isGroupMember(id)) {
            return true;
        };

        return false;
    }

    @Override
    public boolean isGroupOwner(long id) {
        return this.user.isGroupOwner(id);
    }

    @Override
    public boolean isGroupMember(long id) {
        return this.user.isGroupMember(id);
    }

    @Override
    public long getId() {
        return this.user.getId();
    }

    @Override
    public boolean isEnabled() {
        return this.user.isEnabled();
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "CustomUserDetails [user=" + user + "]";
    }


}