package com.example.demo.controller;

import com.example.demo.domain.Test;
import com.example.demo.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class TestController {

    @Resource
    private TestService testService;

    @RequestMapping("/hello")
    public String hello(){
        return "Hello Word1";
    }

    @GetMapping("/test/list")
    public List<Test> list() {
        return testService.list();
    }
}
