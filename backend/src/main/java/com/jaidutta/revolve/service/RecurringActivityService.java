package com.jaidutta.revolve.service;

import com.jaidutta.revolve.controller.dto.RecurringActivityDto;
import com.jaidutta.revolve.entity.RecurringActivity;
import com.jaidutta.revolve.entity.User;
import com.jaidutta.revolve.exception.RecurringActivityNotFoundException;
import com.jaidutta.revolve.exception.TooManyRecurringActivitiesRegisteredByUserException;
import com.jaidutta.revolve.repository.RecurringActivityRepository;
import com.jaidutta.revolve.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service public class RecurringActivityService {

    private final RecurringActivityRepository recurringActivityRepository;
    private final UserRepository userRepository;

    @Autowired
    public RecurringActivityService(RecurringActivityRepository recurringActivityRepository, UserRepository userRepository) {
        this.recurringActivityRepository = recurringActivityRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RecurringActivity createRecurringActivity(User user, RecurringActivityDto recurringDto)
            throws TooManyRecurringActivitiesRegisteredByUserException {
        // Todo: add config instead of magic number 20
        if (user.getEventsCurrentlyRegistered() == 20) {
            throw new TooManyRecurringActivitiesRegisteredByUserException(
                    "Too many recurring " + "activities registered for user: " +
                    user.getUsername());
        }

        RecurringActivity recurringActivity = new RecurringActivity(user,
                                                                    recurringDto.getCourseName(),
                                                                    recurringDto.getActivityName(),
                                                                    recurringDto.getActivityType(),
                                                                    recurringDto.getDayOfWeek(),
                                                                    recurringDto.getStartTime(),
                                                                    recurringDto.getDurationMinutes());


        user.setEventsCurrentlyRegistered(user.getEventsCurrentlyRegistered() + 1);
        this.userRepository.save(user);
        this.recurringActivityRepository.save(recurringActivity);
        return recurringActivity;
    }

    public Optional<RecurringActivity> getRecurringActivityByIdAndUser(Long id, User user) {
        return this.recurringActivityRepository.findByIdAndUser(id, user);
    }

    public List<RecurringActivity> getRecurringActivitiesByUser(User user) {
        return this.recurringActivityRepository.findAllByUser(user);
    }

    @Transactional public void deleteRecurringActivityByIdAndUser(Long id, User user)
            throws RecurringActivityNotFoundException {
        Optional<RecurringActivity> activityToDelete = getRecurringActivityByIdAndUser(id, user);
        if (activityToDelete.isPresent()) {
            recurringActivityRepository.deleteById(id);
            user.setEventsCurrentlyRegistered(user.getEventsCurrentlyRegistered() - 1);
            this.userRepository.save(user);
        } else {
            throw new RecurringActivityNotFoundException("Activity " + id + " not found");
        }
    }

    @Transactional
    public RecurringActivity updateRecurringActivityByIdAndUser(Long id, User user, RecurringActivityDto recurringDto)
            throws RecurringActivityNotFoundException {
        RecurringActivity existingActivity = getRecurringActivityByIdAndUser(id, user).orElseThrow(
                () -> new RecurringActivityNotFoundException("Activity " + id + " not found"));

        existingActivity.setCourseName(recurringDto.getCourseName());
        existingActivity.setActivityName(recurringDto.getActivityName());
        existingActivity.setActivityType(recurringDto.getActivityType());
        existingActivity.setDayOfWeek(recurringDto.getDayOfWeek());
        existingActivity.setStartTime(recurringDto.getStartTime());
        existingActivity.setDurationMinutes(recurringDto.getDurationMinutes());

        this.recurringActivityRepository.save(existingActivity);
        return existingActivity;
    }
}
