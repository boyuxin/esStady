package com.yuxin.yuxinesjd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author boyuxin
 * @description
 * @date 2020/7/5 13:40
 */
@Controller
public class IndexController {

    @GetMapping(value = "/")
    public String index(){
        return "index";
    }

}
