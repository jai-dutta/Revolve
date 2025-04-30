package com.jaidutta.revolve.controller;

import com.jaidutta.revolve.controller.dto.ApiResponseDto;
import com.jaidutta.revolve.controller.dto.RecurringActivityDto;
import com.jaidutta.revolve.entity.RecurringActivity;
import com.jaidutta.revolve.entity.User;
import com.jaidutta.revolve.exception.TooManyRecurringActivitiesRegisteredByUserException;
import com.jaidutta.revolve.service.RecurringActivityService;
import com.jaidutta.revolve.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/recurring-activities")
public class RecurringActivityController {
    private final RecurringActivityService recurringActivityService;
    private final UserService userService;


    @Autowired
    public RecurringActivityController(RecurringActivityService recurringActivityService, UserService userService) {
        this.recurringActivityService = recurringActivityService;
        this.userService = userService;
    }

    @PostMapping("/create")
   public ResponseEntity<?> createRecurringActivity(@Valid @RequestBody RecurringActivityDto recurringActivityDto)
            throws TooManyRecurringActivitiesRegisteredByUserException {
        User user = getCurrentUserEntity();
        RecurringActivity savedActivity =
                this.recurringActivityService.createRecurringActivity(user, recurringActivityDto);
        RecurringActivityDto responseDto =
                RecurringActivityDto.mapToRecurringActivity(savedActivity);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(responseDto));
    }

    @GetMapping
    public ResponseEntity<?> readAllRecurringActivities() {
        User user = getCurrentUserEntity();
        List<RecurringActivity> activities = this.recurringActivityService.getRecurringActivitiesByUser(user);
        List<RecurringActivityDto> activityResponseDtos = new ArrayList<>();

        for (RecurringActivity recurringActivity : activities) {
            activityResponseDtos.add(RecurringActivityDto.mapToRecurringActivity(recurringActivity));
        }
        return  ResponseEntity.ok().body(ApiResponseDto.success(activityResponseDtos));
    }


    private User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();
        return this.userService.findUserByUsername(authentication.getName());
    }
}
