package com.example.demochat.model;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "messages")
public class Message implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonView(AppViews.Public.class)
  private long id;

  @Column(name = "body")
  @JsonView(AppViews.Public.class)
  private String body;

  @Column(name = "sent_from")
  @JsonView(AppViews.Public.class)
  private String sent_from;

  @Column(name = "sent_to")
  @JsonView(AppViews.Public.class)
  private String sent_to;

  @Column(name = "timestamp")
  @JsonView(AppViews.Public.class)
  private String timestamp;


  @ManyToOne
  @JoinColumn(name="group_id")
  @JsonView(AppViews.Internal.class)
  private Group group;

  @JoinColumn(name="type")
  @JsonView(AppViews.Public.class)
  private String type;

  public Message() {
    super();
  }

  public Message(String body, String sent_from, String sent_to, String timestamp) {
    this.body = body;
    this.sent_from = sent_from;
    this.sent_to = sent_to;
    this.timestamp = timestamp;
    this.type = "Direct";
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getSent_from() {
    return sent_from;
  }

  public void setSent_from(String sent_from) {
    this.sent_from = sent_from;
  }

  public String getSent_to() {
    return sent_to;
  }

  public void setSent_to(String sent_to) {
    this.sent_to = sent_to;
  }

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

}