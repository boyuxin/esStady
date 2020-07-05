package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author boyuxin
 * @description
 * @date 2020/7/5 15:04
 */
@Controller
public class MYController {

    @GetMapping("/")
    public String get(){
        return "index";
    }

    @GetMapping("/search/{keyWords}")
    public String getww(@PathVariable("keyWords") String keyWords){

        return "index";
    }
}
