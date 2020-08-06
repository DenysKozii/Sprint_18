package com.softserve.marathon.service;

import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.model.Marathon;
import com.softserve.marathon.model.Sprint;
import java.util.List;

public interface SprintService {
    void delete(Long id) throws EntityNotFoundException;

    List<Sprint> getSprintsByMarathon(Long id);

    boolean addSprintToMarathon(Sprint sprint, Marathon marathon);

    boolean updateSprint(Sprint sprint);

    void createSpring(Sprint sprint);

    void finishSprint(Sprint sprint);

    Sprint getSprintById(Long id) throws EntityNotFoundException;
}
