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
    @Secured({"ROLE_ADMIN","ROLE_MENTOR"})
    @GetMapping("")
    public ResponseEntity<List<User>> studentsList(Model model) {
       // logger.info("Rendering student/create.html view");
        logger.info("GET All students");
        List<User> students = userService.getAllByRole(roleRepository.findByRole("STUDENT"));
        return students != null &&  !students.isEmpty()
                ? new ResponseEntity<>(students, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
       // return "student/list";
    }

    //Students from specified marathon
    @GetMapping("/{marathonId}")
    public ResponseEntity<List<User>> studentsListByMarathon(Model model, @PathVariable Long marathonId) {
        //logger.info("Rendering student/listByMarathon.html view");
        logger.info("GET All students by marathonId " + marathonId);
        List<User> students = marathonService.getMarathonById(marathonId).getUsers();
//        List<User> allStudents = userService.getAll();
//        model.addAttribute("students", students);
//        model.addAttribute("allStudents", allStudents);
//        return "student/listByMarathon";
        return new ResponseEntity<>(students, HttpStatus.OK);

    }

    //Delete user from marathon
    @DeleteMapping("/{marathonId}/delete/{studentId}")
    public ResponseEntity<?> deleteStudentFromMarathon(@PathVariable Long marathonId, @PathVariable Long studentId) {
        logger.info("DELETE student with id" + studentId + "by marathonId " + marathonId);
        User student = userService.getUserById(studentId);
        boolean deleted = userService.deleteUserFromMarathon(student.getId(), marathonId);


        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
//        logger.info("Deleting student id " + studentId + " from marathon id " + marathonId);
//        return "redirect:/students/{marathonId}";
    }

    //Edit user
   /* @GetMapping("/{marathonId}/edit/{studentId}")
    public String editStudentForm(@PathVariable Long marathonId,
                                  @PathVariable Long studentId,
                                  Model model) {
        logger.info("Rendering student/edit.html view");
        model.addAttribute("student", userService.getUserById(studentId));
        return "student/edit";
    }*/

    //Edit user
    @PutMapping("/{marathonId}/edit/{studentId}")
    public ResponseEntity<?> editStudentFormSubmit(@Valid @RequestBody User student,
                                        BindingResult bindingResult,
                                        @PathVariable Long studentId,
                                        @PathVariable Long marathonId) {

        logger.info("PUT editing student with id " + studentId);
        if (bindingResult.hasErrors()) {
            logger.error("Error(s) updating student id " + studentId + ": " + bindingResult.getAllErrors());
            throw new EntityNotFoundException("Error(s) updating student id " + studentId + ": " + bindingResult.getAllErrors());
//            return "student/edit";
        }
       boolean updated = userService.createOrUpdateUser(student);

        return updated == false
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

//        logger.info("Updating student id: " + studentId);
//        return "redirect:/students/{marathonId}";
    }

    //Create user
    /*@GetMapping("/{marathonId}/create")
    public String createStudentForm(@PathVariable Long marathonId, Model model,
                                    @ModelAttribute("errorMessage") String errorMessage,
                                    @ModelAttribute("student") User student) {
        logger.info("Rendering student/create.html view");
        model.addAttribute(marathonId);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("student", student != null ? student : new User());
        return "student/create";
    }*/

    //Create user
    @PostMapping("/{marathonId}/create")
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
            /*User newStudent = */userService.createOrUpdateUser(student);
            Marathon marathon = marathonService.getMarathonById(marathonId);
            userService.addUserToMarathon(student, marathon);
        } catch (DataIntegrityViolationException e) {
            logger.info("DataIntegrityViolationException occurred::" + "Error=" + e.getMessage());
//            redirectAttributes.addFlashAttribute("errorMessage", "This email is already in use.");
//            redirectAttributes.addFlashAttribute("student", student);
            return new ResponseEntity<>(student, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(student, HttpStatus.CREATED);
    }



    //Add user to marathon
   /* @GetMapping("/{marathonId}/add")
    public String addStudent(@RequestParam("studentId") long studentId,
                             @PathVariable long marathonId) {
        logger.info("Adding student id " + studentId + " to marathon " + marathonId);
        userService.addUserToMarathon(
                userService.getUserById(studentId),
                marathonService.getMarathonById(marathonId));
        return "redirect:/students/{marathonId}";
    }*/
}
