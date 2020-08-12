package com.softserve.marathon.controller;

import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.model.Marathon;
import com.softserve.marathon.repository.UserRepository;
import com.softserve.marathon.service.MarathonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/marathons")
public class MarathonController {
    Logger logger = LoggerFactory.getLogger(MarathonController.class);

    private final MarathonService marathonService;
    private final UserRepository userRepository;

    public MarathonController(MarathonService marathonService, UserRepository userRepository) {
        this.marathonService = marathonService;
        this.userRepository = userRepository;
    }

    @GetMapping("")
    public ResponseEntity<?> showMarathons(Model model, HttpServletRequest request) {
        List<Marathon> marathons;
        String userEmail = request.getUserPrincipal().getName();
        Long studentId = userRepository.getUserByEmail(userEmail).getId();
        if (request.isUserInRole("ROLE_STUDENT")) {
            marathons = marathonService.getAllByStudentId(studentId);
        } else {
            marathons = marathonService.getAll();
        }
        logger.info("GET marathons");
       // model.addAttribute("marathons", marathons);
       // return "marathon/list";
        return new ResponseEntity<>(marathons, HttpStatus.OK);
    }

    @GetMapping("/add")
    @Secured({"ROLE_ADMIN","ROLE_MENTOR"})
    public String createMarathon(Model model) {
        logger.info("Rendering marathon/create.html view");
        model.addAttribute("marathon", new Marathon());
        return "marathon/create";
    }

    @PostMapping("/add")
    @Secured({"ROLE_ADMIN","ROLE_MENTOR"})
    public ResponseEntity<Marathon> createMarathon(@Valid @RequestBody Marathon marathon,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
//            logger.error("Error(s) creating marathon" + bindingResult.getAllErrors());
//            return "marathon/create";
            logger.error("Error(s) creating marathon" + bindingResult.getAllErrors());
            return new ResponseEntity<>(marathon, HttpStatus.BAD_REQUEST);
        }
        logger.info("Creating new marathon");
        marathonService.createOrUpdateMarathon(marathon);
        return new ResponseEntity<>(marathon, HttpStatus.CREATED);
    }


//    @GetMapping("/edit/{id}")
//    @Secured({"ROLE_ADMIN","ROLE_MENTOR"})
//    public String editMarathon(Model model, @PathVariable Long id) {
//        model.addAttribute("marathon", marathonService.getMarathonById(id));
//        return "marathon/edit";
//    }

    @PutMapping("/edit/{id}")
    @Secured({"ROLE_ADMIN","ROLE_MENTOR"})
    public ResponseEntity<?> editMarathon(@Valid @RequestBody Marathon marathon,
                               BindingResult bindingResult,
                               @PathVariable Long id) {
        if (bindingResult.hasErrors()) {
//            logger.error("Error(s) editing marathon Id " + id + bindingResult.getAllErrors());
//            return "marathon/edit";
            logger.error("Error(s) editing marathon" + bindingResult.getAllErrors());
            return new ResponseEntity<>(marathon, HttpStatus.NOT_MODIFIED);

        }
        logger.info("Editing marathon Id " + id);
        marathonService.createOrUpdateMarathon(marathon);
        return new ResponseEntity<>(marathon, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @Secured({"ROLE_ADMIN","ROLE_MENTOR"})
    public ResponseEntity<?> close(@PathVariable Long id) {
        logger.info("DELETE Close marathon");
        Marathon marathonFromDb = null;
        try {
            marathonFromDb = marathonService.getMarathonById(id);
            marathonFromDb.setClosed(true);
            marathonService.createOrUpdateMarathon(marathonFromDb);
        } catch (EntityNotFoundException e) {
            logger.error("Exception:" + e);
            e.printStackTrace();
            return new ResponseEntity<>(marathonFromDb, HttpStatus.NOT_MODIFIED);
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }
}


