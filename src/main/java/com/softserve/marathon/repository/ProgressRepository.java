package com.softserve.marathon.repository;

import com.softserve.marathon.model.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {

    @Query("select case when count(p)> 0 then true else false end " +
            "from Progress p where p.user.id =:userId and p.task.id =:taskId")
    boolean checkProgressExist(@Param("userId") Long userId, @Param("taskId") Long taskId);

    @Query(value = "select * from progress where user_id =?1 and task_id in " +
            "(select id from task where sprint_id in " +
            "(select id from sprint where marathon_id =?2))", nativeQuery = true)
    List<Progress> allProgressByUserIdAndMarathonId(Long userId, Long marathonId);

    @Query(value = "select * from progress where user_id =?1 and task_id in " +
            "(select id from task where sprint_id =?2)", nativeQuery = true)
    List<Progress> allProgressByUserIdAndSprintId(Long userId, Long sprintId);

    Progress getProgressByUserIdAndTaskId(Long userId, Long taskId);

}
