package com.softserve.marathon.service.imp;

import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.model.Progress;
import com.softserve.marathon.model.Sprint;
import com.softserve.marathon.model.Task;
import com.softserve.marathon.repository.ProgressRepository;
import com.softserve.marathon.repository.TaskRepository;
import com.softserve.marathon.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ProgressRepository progressRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, ProgressRepository progressRepository) {
        this.taskRepository = taskRepository;
        this.progressRepository = progressRepository;
    }

    @Override
    public void delete(Long id) throws EntityNotFoundException {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()){
            Task taskFromDb = task.get();
            for (Progress progress : taskFromDb.getProgresses()) {
                progressRepository.deleteById(progress.getId());
            }
            taskRepository.deleteById(id);
        }else {
            throw new EntityNotFoundException("Task doesn't exist");
        }
    }

    @Override
    public Task getTaskById(Long id) throws EntityNotFoundException {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()){
            return task.get();
        }else {
            throw new EntityNotFoundException("Task doesn't exist");
        }
    }

    @Override
    public Task addTaskToSprint(Task task, Sprint sprint) {
        task.setCreated(LocalDate.now());
        task.setSprint(sprint);
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Task task) {
        task.setUpdated(LocalDate.now());
        return taskRepository.save(task);
    }
}
