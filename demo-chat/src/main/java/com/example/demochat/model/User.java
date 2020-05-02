package com.example.demochat.model;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.*;
import java.util.List;
import java.io.Serializable;

@Entity
@Table(name = "users", uniqueConstraints={@UniqueConstraint(columnNames={"username"})})
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;


    @Column(name = "enabled")
    private boolean enabled;


    @Column(name = "role")
    private String role;

    @ManyToMany
    @JoinTable( name = "user_groups",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "group_id"))
    private List<Group> groups;


    @OneToMany(targetEntity = Group.class, mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Group> ownedGroups;

//    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JoinTable(name = "user_authority",
//            joinColumns = { @JoinColumn(name = "user_id") },
//            inverseJoinColumns = { @JoinColumn(name = "authority_id") })
//    private Set<Authority> authorities = new HashSet<>();


    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = "common";
    }


//    public Set<Authority> getAuthorities() {
//        return authorities;
//    }
//
//    public void setAuthorities(Set<Authority> authorities) {
//        this.authorities = authorities;
//    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Group> getOwnedGroups() {
        return ownedGroups;
    }

    public void setOwnedGroups(List<Group> ownedGroups) {
        this.ownedGroups = ownedGroups;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User() {
        super();
    }

}