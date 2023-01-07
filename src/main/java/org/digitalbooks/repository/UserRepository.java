package org.digitalbooks.repository;

import org.digitalbooks.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    @Query("select u from User u where upper(u.name) like upper(concat('%', ?1, '%'))")
    List<User> findByNameContainsIgnoreCaseAllIgnoreCase(String name);
    Optional<User> findByEmailIdAndPassword(String emailId, String password);
    Optional<User> findByEmailId(String emailId);
}
