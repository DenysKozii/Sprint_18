package com.softserve.marathon.repository;

import com.softserve.marathon.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {
    List<Sprint> getSprintsByMarathonId(Long id);
    @Query(value = "SELECT DISTINCT S.* FROM SPRINT S " +
            "JOIN TASK T ON T.SPRINT_ID  = S.ID " +
            "JOIN PROGRESS P ON P.TASK_ID = T.ID " +
            "WHERE P.USER_ID = ?1 AND S.MARATHON_ID = ?2", nativeQuery = true)
    List<Sprint> getSprintByUserIdAndMarathon(Long userId, Long marathonId);
}
