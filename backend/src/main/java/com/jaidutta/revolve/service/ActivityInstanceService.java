package com.jaidutta.revolve.service;

import com.jaidutta.revolve.entity.ActivityInstance;
import com.jaidutta.revolve.entity.RecurringActivity;
import com.jaidutta.revolve.entity.User;
import com.jaidutta.revolve.exception.ActivityInstanceNotFoundException;
import com.jaidutta.revolve.repository.ActivityInstanceRepository;
import com.jaidutta.revolve.repository.RecurringActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class ActivityInstanceService {

    private final ActivityInstanceRepository activityInstanceRepository;
    private final RecurringActivityRepository recurringActivityRepository;

    @Autowired
    public ActivityInstanceService(
            ActivityInstanceRepository activityInstanceRepository,
            RecurringActivityRepository recurringActivityRepository) {
        this.activityInstanceRepository = activityInstanceRepository;
        this.recurringActivityRepository = recurringActivityRepository;
    }

    @Transactional
    public List<ActivityInstance> getOrGenerateInstancesForWeek(User user, LocalDate weekStartDate) {
        List<RecurringActivity> activities = recurringActivityRepository.findAllByUser(user);

        for (RecurringActivity activity : activities) {
            boolean exists = activityInstanceRepository
                    .findByRecurringActivityAndWeekStartDate(activity, weekStartDate)
                    .isPresent();
            if (!exists) {
                int daysFromMonday = activity.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
                LocalDate dueDate = weekStartDate.plusDays(daysFromMonday);
                ActivityInstance instance = new ActivityInstance(activity, user, weekStartDate, dueDate);
                activityInstanceRepository.save(instance);
            }
        }

        return activityInstanceRepository.findAllByUserAndWeekStartDate(user, weekStartDate);
    }

    @Transactional
    public ActivityInstance toggleComplete(Long id, User user) throws ActivityInstanceNotFoundException {
        ActivityInstance instance = activityInstanceRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ActivityInstanceNotFoundException("Activity instance " + id + " not found"));
        instance.setCompleted(!instance.isCompleted());
        return activityInstanceRepository.save(instance);
    }

    public List<ActivityInstance> getBacklog(User user) {
        return activityInstanceRepository.findAllByUserAndBackloggedTrueAndCompletedFalse(user);
    }

    // Runs at 1am every Monday to flag past weeks' incomplete instances as backlogged
    @Scheduled(cron = "0 0 1 * * MON")
    @Transactional
    public void markPastWeeksAsBacklogged() {
        LocalDate currentWeekMonday = LocalDate.now().with(DayOfWeek.MONDAY);
        List<ActivityInstance> pastDue = activityInstanceRepository
                .findAllByWeekStartDateBeforeAndCompletedFalseAndBackloggedFalse(currentWeekMonday);
        for (ActivityInstance instance : pastDue) {
            instance.setBacklogged(true);
        }
        activityInstanceRepository.saveAll(pastDue);
    }
}
