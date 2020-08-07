package com.softserve.marathon.controller;

import com.softserve.marathon.model.Role;
import com.softserve.marathon.service.MarathonService;
import com.softserve.marathon.service.UserService;
import com.softserve.marathon.model.Marathon;
import com.softserve.marathon.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StudentControllerTest {
    private final MockMvc mockMvc;
    private final UserService userService;
    private final MarathonService marathonService;
    Role trainee = new Role("STUDENT");

    @Autowired
    public StudentControllerTest(MockMvc mockMvc, UserService userService, MarathonService marathonService) {
        this.mockMvc = mockMvc;
        this.userService = userService;
        this.marathonService = marathonService;
        setup();
    }

    public void setup() {
        //CREATE MARATHONS
        Marathon marathon1 = new Marathon("Marathon 1");
        marathonService.createOrUpdateMarathon(marathon1);
        Marathon marathon2 = new Marathon("Marathon 2");
        marathonService.createOrUpdateMarathon(marathon2);
        // CREATE STUDENTS
        for (int i = 0; i < 10; i++) {
            User u = new User();
            u.setEmail("student_" + i + "@email.com");
            u.setFirstName("name_" + i);
            u.setLastName("surname_" + i);
            u.setPassword("password" + i);
            u.setRole(trainee);
            userService.createOrUpdateUser(u);
            userService.addUserToMarathon(u, i % 2 == 0 ? marathon1 : marathon2);
        }
        //CREATE MENTORS
        for (int i = 0; i < 3; i++) {
            User u = new User();
            u.setEmail("mentor_" + i + "@email.com");
            u.setFirstName("name_" + i);
            u.setLastName("surname_" + i);
            u.setPassword("password" + i);
            u.setRole(trainee);
            userService.createOrUpdateUser(u);
        }

    }

    @Test
    public void studentsListTest() throws Exception {
        List<User> expected = userService.getAllByRole(trainee);
        mockMvc.perform(MockMvcRequestBuilders.get("/students"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.model().attribute("students", expected));
    }

    @Test
    public void studentsListByMarathonTest() throws Exception {
        Marathon marathon = new Marathon("test");
        marathonService.createOrUpdateMarathon(marathon);

        User student = new User();
        student.setRole(trainee);
        student.setEmail("user1@test.com");
        student.setFirstName("First name");
        student.setLastName("Last name");
        student.setPassword("password");
        userService.createOrUpdateUser(student);
        userService.addUserToMarathon(student,marathon);

        List<User> expected = userService.getAllByMarathon(marathon.getId());
        List<User> allStudents = userService.getAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/students/{marathonId}", marathon.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("allStudents"))
                .andExpect(MockMvcResultMatchers.model().attribute("allStudents", allStudents))
                .andExpect(MockMvcResultMatchers.model().attribute("students", expected));
    }

    @Test
    public void deleteStudentFromMarathonTest() throws Exception {
        User student = new User();
        student.setRole(trainee);
        student.setEmail("user2@test.com");
        student.setFirstName("First name");
        student.setLastName("Last name");
        student.setPassword("password");
        userService.createOrUpdateUser(student);
        Marathon marathon = new Marathon("test");
        marathonService.createOrUpdateMarathon(marathon);
        mockMvc.perform(MockMvcRequestBuilders.get("/students/{marathonId}/delete/{studentId}", marathon.getId(), student.getId()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        assertEquals(-1, marathon.getUsers().indexOf(student));
    }

    @Test
    public void editStudentFormTest()  throws Exception {
        User expected = new User();
        expected.setRole(trainee);
        expected.setEmail("user3@test.com");
        expected.setFirstName("First name");
        expected.setLastName("Last name");
        expected.setPassword("password");
        Marathon marathon = new Marathon("test");
        marathonService.createOrUpdateMarathon(marathon);
        userService.createOrUpdateUser(expected);
        mockMvc.perform(MockMvcRequestBuilders.get("/students/{marathonId}/edit/{studentId}", marathon.getId(), expected.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("student", expected));
    }

    @Test
    public void editStudentFormSubmitTest() throws Exception {
        User user = new User();
        user.setRole(trainee);
        user.setEmail("user4@test.com");
        user.setFirstName("First name");
        user.setLastName("Last name");
        user.setPassword("password");
        Marathon marathon = new Marathon("test");
        marathonService.createOrUpdateMarathon(marathon);
        userService.createOrUpdateUser(user);
        mockMvc.perform(MockMvcRequestBuilders.post("/students/{marathonId}/edit/{studentId}", marathon.getId(), user.getId())
                .param("id", String.valueOf(user.getId()))
                .param("email", "user10@test.com")
                .param("firstName", "First name new")
                .param("lastName", "Last name new")
                .param("password", "password1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        mockMvc.perform(MockMvcRequestBuilders.post("/students/{marathonId}/edit/{studentId}", marathon.getId(), user.getId())
                .param("id", String.valueOf(user.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertEquals("user10@test.com", userService.getUserById(user.getId()).getEmail());
    }

    @Test
    public void createStudentFormTest() throws Exception {
        Marathon marathon = new Marathon("test");
        marathonService.createOrUpdateMarathon(marathon);
        mockMvc.perform(MockMvcRequestBuilders.get("/students/{marathonId}/create", marathon.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("student"));
    }

    @Test
    public void createStudentFormSubmitTest() throws Exception {
        Marathon marathon = new Marathon("test");
        marathonService.createOrUpdateMarathon(marathon);
        mockMvc.perform(MockMvcRequestBuilders.post("/students/{marathonId}/create", marathon.getId())
                .param("email", "user5@test.com")
                .param("firstName", "First name")
                .param("lastName", "Last name")
                .param("password", "password"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        User expected = userService.getUserByEmail("user5@test.com");
        assertTrue(marathonService.getMarathonById(marathon.getId()).getUsers().contains(expected));
    }

    @Test
    public void addStudentTest() throws Exception {
        Marathon marathon = new Marathon("test");
        marathonService.createOrUpdateMarathon(marathon);
        User user = new User();
        user.setRole(trainee);
        user.setEmail("user6@test.com");
        user.setFirstName("First name");
        user.setLastName("Last name");
        user.setPassword("password");
        userService.createOrUpdateUser(user);
        mockMvc.perform(MockMvcRequestBuilders.get("/students/{marathonId}/add", marathon.getId())
                .param("studentId", String.valueOf(user.getId())))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        User expected = userService.getUserByEmail("user6@test.com");
        assertTrue(marathonService.getMarathonById(marathon.getId()).getUsers().contains(expected));
    }
}
