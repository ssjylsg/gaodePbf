package com.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@Controller
@RequestMapping(value = "/home")
public class HomeControl {
    @RequestMapping()
    public String index() {
        SimpleDateFormat bartDateFormat = new SimpleDateFormat
                ("yyyy-MM-dd HH:mm:ss");
        return bartDateFormat.format(new Date());
    }
}
