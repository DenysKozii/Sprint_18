package com.softserve.marathon.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    public IndexController() {
    }

    @GetMapping(value = {""})
    public String showIndex() {
        logger.info("Rendering index.html");
        return "index";
    }

    @GetMapping(value = {"/index"})
    public String redirectIndex() {
        return "redirect:/";
    }

}
