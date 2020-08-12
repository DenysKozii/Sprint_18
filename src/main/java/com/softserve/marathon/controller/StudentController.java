package com.softserve.marathon.controller;

import com.softserve.marathon.config.JwtProvider;
import com.softserve.marathon.dto.RoleResponce;
import com.softserve.marathon.dto.TokenResponse;
import com.softserve.marathon.dto.UserRequest;
import com.softserve.marathon.dto.UserResponce;
import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.model.Marathon;
import com.softserve.marathon.model.User;
import com.softserve.marathon.repository.RoleRepository;
import com.softserve.marathon.service.MarathonService;
import com.softserve.marathon.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    Logger logger = LoggerFactory.getLogger(StudentController.class);
    private final UserService userService;
    private final MarathonService marathonService;
    private final RoleRepository roleRepository;


    public StudentController(UserService userService, MarathonService marathonService, RoleRepository roleRepository) {
        this.userService = userService;
        this.marathonService = marathonService;
        this.roleRepository = roleRepository;

    }

    //All students list
    @Secured({"ROLE_ADMIN", "ROLE_MENTOR"})
    @GetMapping("")
    public ResponseEntity<List<User>> studentsList(Model model) {
        // logger.info("Rendering student/create.html view");
        logger.info("GET All students");
        List<User> students = userService.getAllByRole(roleRepository.findByRole("ROLE_STUDENT"));
        return students != null && !students.isEmpty()
                ? new ResponseEntity<>(students, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //Students from specified marathon
    @GetMapping("/{marathonId}")
    @Secured({"ROLE_ADMIN", "ROLE_MENTOR"})
    public ResponseEntity<List<User>> studentsListByMarathon(Model model, @PathVariable Long marathonId) {
        //logger.info("Rendering student/listByMarathon.html view");
        logger.info("GET All students by marathonId " + marathonId);
        List<User> students = marathonService.getMarathonById(marathonId).getUsers();
        return new ResponseEntity<>(students, HttpStatus.OK);

    }

    //Delete user from marathon
    @DeleteMapping("/{marathonId}/delete/{studentId}")
    @Secured({"ROLE_ADMIN", "ROLE_MENTOR"})
    public ResponseEntity<?> deleteStudentFromMarathon(@PathVariable Long marathonId, @PathVariable Long studentId) {
        logger.info("DELETE student with id" + studentId + "by marathonId " + marathonId);
        User student = userService.getUserById(studentId);
        boolean deleted = userService.deleteUserFromMarathon(student.getId(), marathonId);
        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }


    //Edit user
    @PutMapping("/{marathonId}/edit/{studentId}")
    @Secured({"ROLE_ADMIN", "ROLE_MENTOR"})
    public ResponseEntity<?> editStudentFormSubmit(@Valid @RequestBody User student,
                                                   BindingResult bindingResult,
                                                   @PathVariable Long studentId,
                                                   @PathVariable Long marathonId) {

        logger.info("PUT editing student with id " + studentId);
        if (bindingResult.hasErrors()) {
            logger.error("Error(s) updating student id " + studentId + ": " + bindingResult.getAllErrors());
            throw new EntityNotFoundException("Error(s) updating student id " + studentId + ": " + bindingResult.getAllErrors());
        }
        boolean updated = userService.createOrUpdateUser(student);

        return updated == false
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }


    //Create user
    @PostMapping("/{marathonId}/create")
    @Secured({"ROLE_ADMIN", "ROLE_MENTOR"})
    public ResponseEntity<?> createStudentFormSubmit(@Valid @RequestBody User student,
                                                     BindingResult bindingResult,
                                                     @PathVariable Long marathonId,
                                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            logger.error("Error(s) creating student" + bindingResult.getAllErrors());
            return new ResponseEntity<>(student, HttpStatus.BAD_REQUEST);
        }
        logger.info("Creating new student for marathon " + marathonId);
        student.setRole(roleRepository.findByRole("STUDENT"));
        try {
            userService.createOrUpdateUser(student);
            Marathon marathon = marathonService.getMarathonById(marathonId);
            userService.addUserToMarathon(student, marathon);
        } catch (DataIntegrityViolationException e) {
            logger.info("DataIntegrityViolationException occurred::" + "Error=" + e.getMessage());
            return new ResponseEntity<>(student, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(student, HttpStatus.CREATED);
    }
}
