package com.example.demochat.model;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.*;
import java.util.List;
import java.util.Random;
import java.time.Instant;
import java.lang.Math;
import java.io.Serializable;

@Entity
@Table(name = "groups")
public class Group implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(AppViews.Public.class)
    private long id;

    @Column(name = "name",  unique = true)
    @JsonView(AppViews.Public.class)
    private String name;

    @ManyToOne
    @JoinColumn(name="owner")
    @JsonView(AppViews.Internal.class)
    private User owner;

    @Column(name = "url", unique = true)
    @JsonView(AppViews.Public.class)
    private String url;

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonView(AppViews.Internal.class)
    private List<Message> messages;

    @ManyToMany(mappedBy = "groups", cascade = CascadeType.ALL)
    @JsonView(AppViews.Internal.class)
    private List<User> members;

    public Group(String name, User owner) {
        this.name = name;
        this.owner = owner;

        Random rand = new Random();
        rand.setSeed(Instant.now().getEpochSecond());
        this.url = "/groups/" + name + Math.abs(rand.nextInt());
    }

    public Group() {
        super();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

}