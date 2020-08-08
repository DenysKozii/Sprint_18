package com.softserve.marathon.repository;

import com.softserve.marathon.model.Marathon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarathonRepository extends JpaRepository<Marathon, Long> {
    @Query(value = "SELECT M.* FROM MARATHON M\n" +
            "JOIN MARATHON_USER MU ON MU.ID_MARATHON  = M.ID \n" +
            "WHERE MU.ID_USER = ?1", nativeQuery = true)
    List<Marathon> findAllByUserId(Long id);
}
