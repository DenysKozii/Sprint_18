package com.softserve.marathon.repository;

import com.softserve.marathon.model.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class ProgressRepositoryTest {
    private final TestEntityManager entityManager;
    private final ProgressRepository progressRepository;
    Role trainee = new Role("STUDENT");

    @Autowired
    public ProgressRepositoryTest(TestEntityManager entityManager, ProgressRepository progressRepository) {
        this.entityManager = entityManager;
        this.progressRepository = progressRepository;
    }

    @Test
    public void checkProgressExistTest() {
        //given
        User user = new User("testt@email", "ivan", "ivanov", "password", trainee);
        entityManager.persistAndFlush(user);
        Task task = new Task("junit");
        entityManager.persistAndFlush(task);
        Progress progress = new Progress();
        progress.setStarted(LocalDate.now());
        progress.setStatus("start");
        progress.setTask(task);
        progress.setUser(user);
        progress.setId(9L);
        entityManager.merge(progress);
        //when
        boolean progressExist = progressRepository.checkProgressExist(user.getId(), task.getId());
        //then
        assertTrue(progressExist);
    }

    @Test
    public void allProgressByUserIdAndMarathonIdTest() {
//        //given
        User user = new User("test2@email", "petro", "petro", "password2", trainee);
        entityManager.persistAndFlush(user);
        Marathon marathon = new Marathon("second marathon");
        marathon.setUsers(Collections.singletonList(user));
        entityManager.persistAndFlush(marathon);
        Sprint sprint = new Sprint("OOP");
        sprint.setMarathon(marathon);
        entityManager.persistAndFlush(sprint);
        Task task = new Task("task");
        task.setSprint(sprint);
        entityManager.persistAndFlush(task);
        Progress progress = new Progress();
        progress.setUser(user);
        progress.setTask(task);
        progress.setStatus("start");
        entityManager.persistAndFlush(progress);
        //when
        List<Progress> progresses = progressRepository.allProgressByUserIdAndMarathonId(user.getId(), marathon.getId());
        //then
        assertEquals(1, progresses.size());
        assertEquals(user.getId(), progresses.get(0).getUser().getId());
        assertEquals(task.getId(), progresses.get(0).getTask().getId());
    }

    @Test
    public void allProgressByUserIdAndSprintIdTest() {
        //        //given
        User user = new User("test2@email", "petro", "petro", "password2", trainee);
        entityManager.persistAndFlush(user);
        Sprint sprint = new Sprint("OOP");
        entityManager.persistAndFlush(sprint);
        Task task = new Task("task");
        task.setSprint(sprint);
        entityManager.persistAndFlush(task);
        Progress progress = new Progress();
        progress.setUser(user);
        progress.setTask(task);
        progress.setStatus("start");
        entityManager.persistAndFlush(progress);
        //when
        List<Progress> progresses = progressRepository.allProgressByUserIdAndSprintId(user.getId(), sprint.getId());
        //then
        assertEquals(1, progresses.size());
        assertEquals(user.getId(), progresses.get(0).getUser().getId());
        assertEquals(sprint.getId(), progresses.get(0).getTask().getSprint().getId());
    }
}
