package com.softserve.marathon.controller;

import com.softserve.marathon.model.User;
import com.softserve.marathon.service.imp.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class LoginController {
    @Autowired
    UserServiceImpl userServiceimpl;
    @PostMapping("/registration")
    public String addUser(@Valid User user, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("error", true);
            return "student/registration";
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("message", "Check your password input");
            return "student/registration";
        }
        boolean addUser = userServiceimpl.createOrUpdateUser(user);
        if (addUser) {
            return "redirect:/login";
        }
        model.addAttribute("message", "User already exists!");
        return "student/login";
    }

    @GetMapping("/registration")
    public String register() {
        return "student/registration";
    }
}
