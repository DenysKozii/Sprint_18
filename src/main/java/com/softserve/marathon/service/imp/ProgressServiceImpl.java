package com.softserve.marathon.service.imp;

import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.repository.ProgressRepository;
import com.softserve.marathon.service.ProgressService;
import com.softserve.marathon.model.Progress;
import com.softserve.marathon.model.Task;
import com.softserve.marathon.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProgressServiceImpl implements ProgressService {
    private final ProgressRepository progressRepository;

    @Autowired
    public ProgressServiceImpl(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    @Override
    public void delete(Long id) throws EntityNotFoundException {
        Optional<Progress> progress = progressRepository.findById(id);
        if (progress.isPresent()){
            progressRepository.deleteById(id);
        }else {
            throw new EntityNotFoundException("Progress doesn't exist");
        }
    }

    @Override
    public Progress getProgressById(Long id) throws EntityNotFoundException {
        Optional<Progress> progress = progressRepository.findById(id);
        if (progress.isPresent()){
            return progress.get();
        }else {
            throw new EntityNotFoundException("Progress doesn't exist");
        }
    }

    @Override
    public Progress addTaskForStudent(Task task, User user) {
        Progress progress = new Progress();
        progress.setStarted(LocalDate.now());
        progress.setStatus("start");
        progress.setTask(task);
        progress.setUser(user);
        return progressRepository.save(progress);
    }

    @Override
    public boolean setStatus(String taskStatus, Progress progress) {
        Progress progressFromDb = progressRepository.getOne(progress.getId());
        if (!progressFromDb.getStatus().equals(taskStatus)) {
            progress.setStatus(taskStatus);
            progress.setUpdated(LocalDate.now());
            progressRepository.save(progress);
            return true;
        }
        return false;
    }

    @Override
    public List<Progress> allProgressByUserIdAndMarathonId(Long userId, Long marathonId) {
        return progressRepository.allProgressByUserIdAndMarathonId(userId, marathonId);
    }

    @Override
    public List<Progress> allProgressByUserIdAndSprintId(Long userId, Long sprintId) {
        return progressRepository.allProgressByUserIdAndSprintId(userId, sprintId);
    }
}
