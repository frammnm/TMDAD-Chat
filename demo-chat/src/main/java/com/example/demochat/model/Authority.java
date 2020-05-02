//package com.example.demochat.model;
//
//import com.bitMiners.pdf.domain.types.AuthorityType;
//import javax.persistence.*;
//
//@Entity
//@Table(name = "authority")
//public class Authority {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Integer id;
//
//    @Enumerated(EnumType.STRING)
//    private AuthorityType name;
//
//    public Authority() {}
//
//    public Authority(AuthorityType name) {
//        this.name = name;
//    }
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public AuthorityType getName() {
//        return name;
//    }
//
//    public void setName(AuthorityType name) {
//        this.name = name;
//    }
//}