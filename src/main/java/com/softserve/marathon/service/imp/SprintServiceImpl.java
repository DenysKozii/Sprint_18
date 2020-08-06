package com.softserve.marathon.service.imp;

import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.repository.SprintRepository;
import com.softserve.marathon.service.SprintService;
import com.softserve.marathon.service.TaskService;
import com.softserve.marathon.model.Marathon;
import com.softserve.marathon.model.Sprint;
import com.softserve.marathon.model.Task;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SprintServiceImpl implements SprintService {
    private final SprintRepository sprintRepository;
    private final TaskService taskService;

    @Autowired
    public SprintServiceImpl(SprintRepository sprintRepository, TaskService taskService) {
        this.sprintRepository = sprintRepository;
        this.taskService = taskService;
    }

    @Override
    public void delete(Long id) throws EntityNotFoundException {
        Optional<Sprint> sprint = sprintRepository.findById(id);
        if (sprint.isPresent()) {
            Sprint sprintFromDb = sprint.get();
            for (Task task : sprintFromDb.getTasks()) {
                taskService.delete(task.getId());
            }
            sprintRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Sprint doesn't exist");
        }
    }

    @Override
    public List<Sprint> getSprintsByMarathon(Long id) {
        return sprintRepository.getSprintsByMarathonId(id);
    }

    @Override
    public boolean addSprintToMarathon(Sprint sprint, Marathon marathon) {
        sprint.setMarathon(marathon);
        Sprint sprintFromDb = sprintRepository.getOne(sprint.getId());
        if (!sprintFromDb.equals(sprint)) {
            sprintRepository.save(sprint);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSprint(Sprint sprint) {
        boolean updated = false;
        if (sprint.getId() != null) {
            Sprint sprintFromDb = sprintRepository.getOne(sprint.getId());
            if (!sprintFromDb.equals(sprint)) {
                sprintRepository.save(sprint);
                updated = true;
            }
        }
        return updated;
    }

    @Override
    public void createSpring(Sprint sprint) {
        sprint.setStartDay(LocalDate.now());
        sprintRepository.save(sprint);
    }

    @Override
    public void finishSprint(Sprint sprint) {
        sprint.setFinish(LocalDate.now());
        sprintRepository.save(sprint);
    }

    @Override
    public Sprint getSprintById(Long id) throws EntityNotFoundException {
        Optional<Sprint> sprint = sprintRepository.findById(id);
        if (sprint.isPresent()) {
            return sprint.get();
        } else {
            throw new EntityNotFoundException("Sprint doesn't exist");
        }
    }
}
