package com.jaidutta.revolve.service;

import com.jaidutta.revolve.controller.dto.RecurringActivityDto;
import com.jaidutta.revolve.entity.RecurringActivity;
import com.jaidutta.revolve.entity.User;
import com.jaidutta.revolve.repository.RecurringActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecurringActivityService {

    private final RecurringActivityRepository recurringActivityRepository;

    @Autowired
    public RecurringActivityService(RecurringActivityRepository recurringActivityRepository) {
        this.recurringActivityRepository = recurringActivityRepository;
    }

    @Transactional
    public void registerRecurringActivity(User user, RecurringActivityDto recurringDto) {
        RecurringActivity recurringActivity = new RecurringActivity(
                user,
                recurringDto.getCourseName(),
                recurringDto.getActivityName(),
                recurringDto.getActivityType(),
                recurringDto.getDayOfWeek(),
                recurringDto.getStartTime(),
                recurringDto.getDurationMinutes()
                );
        this.recurringActivityRepository.save(recurringActivity);
    }

}
