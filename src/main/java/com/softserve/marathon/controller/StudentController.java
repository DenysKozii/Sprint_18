package com.softserve.marathon.controller;

import com.softserve.marathon.model.Marathon;
import com.softserve.marathon.model.User;
import com.softserve.marathon.repository.RoleRepository;
import com.softserve.marathon.service.MarathonService;
import com.softserve.marathon.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
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
    @GetMapping("")
    public String studentsList(Model model) {
        logger.info("Rendering student/create.html view");
        model.addAttribute("students", userService.getAllByRole(roleRepository.findByRole("STUDENT")));
        return "student/list";
    }

    //Students from specified marathon
    @GetMapping("/{marathonId}")
    public String studentsListByMarathon(Model model, @PathVariable Long marathonId) {
        logger.info("Rendering student/listByMarathon.html view");
        List<User> students = marathonService.getMarathonById(marathonId).getUsers();
        List<User> allStudents = userService.getAll();
        model.addAttribute("students", students);
        model.addAttribute("allStudents", allStudents);
        return "student/listByMarathon";
    }

    //Delete user from marathon
    @GetMapping("/{marathonId}/delete/{studentId}")
    public String deleteStudentFromMarathon(@PathVariable Long marathonId, @PathVariable Long studentId) {
        User student = userService.getUserById(studentId);
        userService.deleteUserFromMarathon(student.getId(), marathonId);
        logger.info("Deleting student id " + studentId + " from marathon id " + marathonId);
        return "redirect:/students/{marathonId}";
    }

    //Edit user
    @GetMapping("/{marathonId}/edit/{studentId}")
    public String editStudentForm(@PathVariable Long marathonId,
                                  @PathVariable Long studentId,
                                  Model model) {
        logger.info("Rendering student/edit.html view");
        model.addAttribute("student", userService.getUserById(studentId));
        return "student/edit";
    }

    //Edit user
    @PostMapping("/{marathonId}/edit/{studentId}")
    public String editStudentFormSubmit(@Valid @ModelAttribute("student") User student,
                                        BindingResult bindingResult,
                                        @PathVariable Long studentId,
                                        @PathVariable Long marathonId) {
        if (bindingResult.hasErrors()) {
            logger.error("Error(s) updating student id " + studentId + ": " + bindingResult.getAllErrors());
            return "student/edit";
        }
        userService.createOrUpdateUser(student);
        logger.info("Updating student id: " + studentId);
        return "redirect:/students/{marathonId}";
    }

    //Create user
    @GetMapping("/{marathonId}/create")
    public String createStudentForm(@PathVariable Long marathonId, Model model,
                                    @ModelAttribute("errorMessage") String errorMessage,
                                    @ModelAttribute("student") User student) {
        logger.info("Rendering student/create.html view");
        model.addAttribute(marathonId);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("student", student != null ? student : new User());
        return "student/create";
    }

    //Create user
    @PostMapping("/{marathonId}/create")
    public String createStudentFormSubmit(@Valid @ModelAttribute("student") User student,
                                          BindingResult bindingResult,
                                          @PathVariable Long marathonId,
                                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            logger.error("Error(s) creating student" + bindingResult.getAllErrors());
            return "student/create";
        }
        logger.info("Creating new student for marathon " + marathonId);
        student.setRole(roleRepository.findByRole("STUDENT"));
        try {
            /*User newStudent = */userService.createOrUpdateUser(student);
            Marathon marathon = marathonService.getMarathonById(marathonId);
            userService.addUserToMarathon(student, marathon);
        } catch (DataIntegrityViolationException e) {
            logger.info("DataIntegrityViolationException occurred::" + "Error=" + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "This email is already in use.");
            redirectAttributes.addFlashAttribute("student", student);
            return "redirect:/students/{marathonId}/create";
        }
        return "redirect:/students/{marathonId}";
    }

    //Add user to marathon
    @GetMapping("/{marathonId}/add")
    public String addStudent(@RequestParam("studentId") long studentId,
                             @PathVariable long marathonId) {
        logger.info("Adding student id " + studentId + " to marathon " + marathonId);
        userService.addUserToMarathon(
                userService.getUserById(studentId),
                marathonService.getMarathonById(marathonId));
        return "redirect:/students/{marathonId}";
    }
}
