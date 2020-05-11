package com.example.demochat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    @RequestMapping("/")
    public String greeting() {
        return "index";
    }

    @RequestMapping("/old")
    public String greeting_old() {
        return "index_old";
    }
}
