package com.jaidutta.revolve.controller;

import com.jaidutta.revolve.controller.dto.ActivityInstanceDto;
import com.jaidutta.revolve.controller.dto.ApiResponseDto;
import com.jaidutta.revolve.entity.ActivityInstance;
import com.jaidutta.revolve.entity.User;
import com.jaidutta.revolve.exception.ActivityInstanceNotFoundException;
import com.jaidutta.revolve.service.ActivityInstanceService;
import com.jaidutta.revolve.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/activity-instances")
public class ActivityInstanceController {

    private final ActivityInstanceService activityInstanceService;
    private final UserService userService;

    @Autowired
    public ActivityInstanceController(ActivityInstanceService activityInstanceService, UserService userService) {
        this.activityInstanceService = activityInstanceService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getInstancesForWeek(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        User user = getCurrentUserEntity();

        if (weekStart == null) {
            weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        }

        List<ActivityInstance> instances = activityInstanceService.getOrGenerateInstancesForWeek(user, weekStart);
        List<ActivityInstanceDto> dtos = instances.stream().map(ActivityInstanceDto::from).toList();
        return ResponseEntity.ok(ApiResponseDto.success(dtos));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> toggleComplete(@PathVariable Long id)
            throws ActivityInstanceNotFoundException {
        User user = getCurrentUserEntity();
        ActivityInstance updated = activityInstanceService.toggleComplete(id, user);
        return ResponseEntity.ok(ApiResponseDto.success(ActivityInstanceDto.from(updated)));
    }

    @GetMapping("/backlog")
    public ResponseEntity<?> getBacklog() {
        User user = getCurrentUserEntity();
        List<ActivityInstance> backlog = activityInstanceService.getBacklog(user);
        List<ActivityInstanceDto> dtos = backlog.stream().map(ActivityInstanceDto::from).toList();
        return ResponseEntity.ok(ApiResponseDto.success(dtos));
    }

    private User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findUserByUsername(authentication.getName());
    }
}
