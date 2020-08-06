package com.softserve.marathon.service;

import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.model.Marathon;
import java.util.List;

public interface MarathonService {

    Marathon createOrUpdateMarathon(Marathon marathon);

    List<Marathon> getAll();

    Marathon getMarathonById(Long id) throws EntityNotFoundException;

    void deleteMarathonById(Long id);
}
