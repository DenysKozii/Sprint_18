package com.softserve.marathon.repository;

import com.softserve.marathon.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User getUserByEmail(String email);

    List<User> getAllByRole(User.Role role);

    @Query(value = "SELECT U.* FROM USERS U\n" +
            "JOIN MARATHON_USER MU ON MU.ID_USER  = U.ID \n" +
            "WHERE MU.ID_MARATHON = ?1", nativeQuery = true)
    List<User> getAllByMarathon(Long id);
}
