package com.example.demochat.model;

public class AddMemberRequest {

    private long group_id;
    private long member_id;

    public AddMemberRequest(long group_id, long member_id) {
        this.group_id = group_id;
        this.member_id = member_id;
    }

    public long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    public long getMember_id() {
        return member_id;
    }

    public void setMember_id(long member_id) {
        this.member_id = member_id;
    }
}