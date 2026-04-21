package com.jaidutta.revolve.repository;

import com.jaidutta.revolve.entity.ActivityInstance;
import com.jaidutta.revolve.entity.RecurringActivity;
import com.jaidutta.revolve.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityInstanceRepository extends JpaRepository<ActivityInstance, Long> {

    List<ActivityInstance> findAllByUserAndWeekStartDate(User user, LocalDate weekStartDate);

    Optional<ActivityInstance> findByRecurringActivityAndWeekStartDate(
        RecurringActivity recurringActivity, LocalDate weekStartDate);

    Optional<ActivityInstance> findByIdAndUser(Long id, User user);

    List<ActivityInstance> findAllByUserAndBackloggedTrueAndCompletedFalse(User user);

    List<ActivityInstance> findAllByWeekStartDateBeforeAndCompletedFalseAndBackloggedFalse(
        LocalDate weekStartDate);

    void deleteAllByRecurringActivity(RecurringActivity recurringActivity);
}
