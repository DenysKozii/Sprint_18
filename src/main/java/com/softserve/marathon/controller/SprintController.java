package com.softserve.marathon.controller;

import com.softserve.marathon.model.Sprint;
import com.softserve.marathon.repository.UserRepository;
import com.softserve.marathon.service.SprintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/sprints")
public class SprintController {
    Logger logger = LoggerFactory.getLogger(SprintController.class);

    SprintService sprintService;
    UserRepository userRepository;

    public SprintController(SprintService sprintService, UserRepository userRepository) {
        this.sprintService = sprintService;
        this.userRepository = userRepository;
    }

    @GetMapping("/marathon/{marathonId}")
    public ResponseEntity<?> SprintListByMarathon(@PathVariable Long marathonId,
                                       Model model,
                                       HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        Long studentId = userRepository.getUserByEmail(userEmail).getId();
        List<Sprint> sprints;
        if (request.isUserInRole("ROLE_STUDENT")) {
            sprints = sprintService.getSprintByUserIdAndMarathon(studentId, marathonId);
        } else {
            sprints = sprintService.getSprintsByMarathon(marathonId);
        }

        logger.info("GET sprints by marathon id");

        return new ResponseEntity<>(sprints, HttpStatus.OK);
//        model.addAttribute("sprints", sprints);
//        logger.info("Rendering sprint/list.html view");
//        return "sprint/list";



    }
}