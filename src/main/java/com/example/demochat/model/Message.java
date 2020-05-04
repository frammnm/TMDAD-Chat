package com.example.demochat.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "messages")
public class Message implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "body")
  private String body;

  @Column(name = "sent_from")
  private String sent_from;

  @Column(name = "sent_to")
  private String sent_to;

  @Column(name = "timestamp")
  private String timestamp;

  @ManyToOne
  @JoinColumn(name="group_id")
  private Group group;

  public Message() {
    super();
  }

  public Message(String body, String sent_from, String sent_to, String timestamp) {
    this.body = body;
    this.sent_from = sent_from;
    this.sent_to = sent_to;
    this.timestamp = timestamp;
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

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

}