package com.softserve.marathon.controller;

import com.softserve.marathon.config.JwtProvider;
import com.softserve.marathon.dto.OperationResponce;
import com.softserve.marathon.dto.TokenResponse;
import com.softserve.marathon.dto.UserRequest;
import com.softserve.marathon.dto.UserResponce;
import com.softserve.marathon.model.User;
import com.softserve.marathon.service.UserService;
import com.softserve.marathon.service.imp.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class LoginController {

    Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    UserService userService;


//    @PostMapping("/registration")
//    public String addUser(@Valid User user, Errors errors, Model model) {
//        if (errors.hasErrors()) {
//            model.addAttribute("error", true);
//            return "registration";
//        }
//        if (!user.getPassword().equals(user.getConfirmPassword())) {
//            model.addAttribute("message", "Check your password input");
//            return "registration";
//        }
//
//        boolean addUser = userService.createOrUpdateUser(user);
//        if (addUser) {
//            return "redirect:/login-form";
//        }
//        model.addAttribute("message", "User already exists!");
//        return "login";
//    }
//
//    @GetMapping("/registration")
//    public String register() {
//        return "registration";
//    }

    @PostMapping("/signin")
    public TokenResponse signIn(
            @RequestParam(value = "login", required = true)
                    String login,
            @RequestParam(value = "password", required = true)
                    String password) {
        logger.info("**/signin userLogin = " + login);
        UserRequest userRequest = new UserRequest(login, password);
        UserResponce userResponce = userService.findByLoginAndPassword(userRequest);
        return new TokenResponse(jwtProvider.generateToken(userResponce.getLogin()));
    }

    @PostMapping("/signup")
    public OperationResponce signUp(
            @RequestParam(value = "login", required = true)
                    String login,
            @RequestParam(value = "password", required = true)
                    String password) {
        logger.info("**/signup userLogin = " + login);
        UserRequest userRequest = new UserRequest(login, password);
        return new OperationResponce(String.valueOf(userService.createOrUpdateUser(userRequest)));
    }

}
