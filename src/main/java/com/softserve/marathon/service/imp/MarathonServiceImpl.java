package com.softserve.marathon.service.imp;

import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.model.Marathon;
import com.softserve.marathon.model.Sprint;
import com.softserve.marathon.repository.MarathonRepository;
import com.softserve.marathon.service.MarathonService;
import com.softserve.marathon.service.SprintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MarathonServiceImpl implements MarathonService {
    Logger logger = LoggerFactory.getLogger(MarathonService.class);

    private final MarathonRepository marathonRepository;
    private final SprintService sprintService;

    @Autowired
    public MarathonServiceImpl(MarathonRepository marathonRepository, SprintService sprintService) {
        this.marathonRepository = marathonRepository;
        this.sprintService = sprintService;
    }

    //For admin, mentor roles
    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MENTOR')")
    public List<Marathon> getAll() {
        logger.info("show getAll() method");
        List<Marathon> marathons = marathonRepository.findAll();
        return marathons.stream()
                .filter(marathon -> !marathon.isClosed())
                .collect(Collectors.toList());
    }

    //For student role
    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public List<Marathon> getAllByStudentId(Long studentId) {
        logger.info("show getAllByStudentId() method");
        List<Marathon> marathons = marathonRepository.findAllByUserId(studentId);
        return marathons.stream()
                .filter(marathon -> !marathon.isClosed())
                .collect(Collectors.toList());
    }

    @Override
    public Marathon getMarathonById(Long id) {
        Optional<Marathon> marathon = marathonRepository.findById(id);
        if (marathon.isPresent()) {
            logger.info("get marathon from BD by ID");
            return marathon.get();
        } else {
            throw new EntityNotFoundException("Marathon with id " + id + " doesn't exist");
        }
    }

    @Override
    public Marathon createOrUpdateMarathon(Marathon marathon) {
        return marathonRepository.save(marathon);
    }

    @Override
    public void deleteMarathonById(Long id) {
        try {
            Optional<Marathon> marathon = marathonRepository.findById(id);
            if (marathon.isPresent()) {
                Marathon marathonFromDb = marathon.get();
                for (Sprint sprint : marathonFromDb.getSprints()) {
                    try {
                        logger.info("delete marathon");
                        sprintService.delete(sprint.getId());
                    } catch (EntityNotFoundException e) {
                        logger.error("Exception:" + e);
                        e.printStackTrace();
                    }
                }
                marathonRepository.deleteById(id);
            } else {
                throw new EntityNotFoundException("Marathon with id " + id + " doesn't exist");
            }
        } catch (EntityNotFoundException e) {
            logger.error("Exception:" + e);
            e.printStackTrace();
        }
    }
}
