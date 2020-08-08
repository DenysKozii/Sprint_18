package com.softserve.marathon.service;

import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.model.Marathon;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface MarathonService {

    Marathon createOrUpdateMarathon(Marathon marathon);

    List<Marathon> getAll();

    //For student role
    @PreAuthorize("hasRole('STUDENT')")
    List<Marathon> getAllByStudentId(Long studentId);

    Marathon getMarathonById(Long id) throws EntityNotFoundException;

    void deleteMarathonById(Long id);
}
