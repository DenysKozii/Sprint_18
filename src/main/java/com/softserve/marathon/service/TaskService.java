package com.softserve.marathon.service;

import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.model.Sprint;
import com.softserve.marathon.model.Task;

public interface TaskService {
    void delete(Long id) throws EntityNotFoundException;

    Task getTaskById(Long id) throws EntityNotFoundException;

    Task addTaskToSprint(Task task, Sprint sprint);

    Task updateTask(Task task);
}
