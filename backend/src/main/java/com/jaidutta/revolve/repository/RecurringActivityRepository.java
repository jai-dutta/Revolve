package com.jaidutta.revolve.repository;

import com.jaidutta.revolve.entity.RecurringActivity;
import com.jaidutta.revolve.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecurringActivityRepository extends JpaRepository<RecurringActivity, Long> {
    List<RecurringActivity> findAllByUser(User user);

    Optional<RecurringActivity> findByIdAndUser(Long id, User user);

}
