package com.softserve.marathon.repository;

import com.softserve.marathon.model.Role;
import com.softserve.marathon.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    private final UserRepository userRepository;
    Role trainee = new Role("STUDENT");
    Role mentor = new Role("MENTOR");

    @Autowired
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    public void newUserTest() {
        User user = new User();
        user.setRole(trainee);
        user.setEmail("user@test.com");
        user.setFirstName("First name");
        user.setLastName("Last name");
        user.setPassword("password");

        userRepository.save(user);
        User actual = userRepository.getUserByEmail("user@test.com");
        Assertions.assertEquals("First name", actual.getFirstName());
        Assertions.assertEquals("Last name", actual.getLastName());
        Assertions.assertEquals("user@test.com", actual.getEmail());
        Assertions.assertEquals("password", actual.getPassword());
    }

    @Test
    public void getAllByRoleTest() {
        User user1 = new User();
        user1.setRole(trainee);
        user1.setEmail("user1@test.com");
        user1.setFirstName("First name");
        user1.setLastName("Last name");
        user1.setPassword("password");
        userRepository.save(user1);
        User user2 = new User();
        user2.setRole(mentor);
        user2.setEmail("user2@test.com");
        user2.setFirstName("First name");
        user2.setLastName("Last name");
        user2.setPassword("password");
        userRepository.save(user2);

        Assertions.assertEquals(1, userRepository.getAllByRole(trainee).size());
        Assertions.assertEquals(1, userRepository.getAllByRole(mentor).size());
    }

    @Test
    public void getAllByMarathonTest() {
        User user1 = new User();
        user1.setRole(trainee);
        user1.setEmail("user1@test.com");
        user1.setFirstName("First name");
        user1.setLastName("Last name");
        user1.setPassword("password");
        userRepository.save(user1);
        Assertions.assertEquals(0, userRepository.getAllByMarathon(1L).size());
    }

    @Test
    public void getUserByEmailTest() {
        User user1 = new User();
        user1.setRole(trainee);
        user1.setEmail("user1@test.com");
        user1.setFirstName("First name");
        user1.setLastName("Last name");
        user1.setPassword("password");
        userRepository.save(user1);
        userRepository.save(user1);
        User user2 = new User();
        user2.setRole(mentor);
        user2.setEmail("user2@test.com");
        user2.setFirstName("First name");
        user2.setLastName("Last name");
        user2.setPassword("password");
        userRepository.save(user2);
        Assertions.assertEquals(user1, userRepository.getUserByEmail("user1@test.com"));
        Assertions.assertNotEquals(user2, userRepository.getUserByEmail("user1@test.com"));
    }
}
