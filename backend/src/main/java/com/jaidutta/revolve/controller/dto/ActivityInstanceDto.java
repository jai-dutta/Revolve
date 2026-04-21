package com.jaidutta.revolve.controller.dto;

import com.jaidutta.revolve.definitions.ActivityType;
import com.jaidutta.revolve.entity.ActivityInstance;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class ActivityInstanceDto {

    private Long id;
    private Long recurringActivityId;
    private String courseName;
    private String activityName;
    private ActivityType activityType;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private Integer durationMinutes;
    private LocalDate weekStartDate;
    private LocalDate dueDate;
    private boolean completed;
    private boolean backlogged;

    public ActivityInstanceDto() {}

    public static ActivityInstanceDto from(ActivityInstance instance) {
        ActivityInstanceDto dto = new ActivityInstanceDto();
        dto.id = instance.getId();
        dto.recurringActivityId = instance.getRecurringActivity().getId();
        dto.courseName = instance.getRecurringActivity().getCourseName();
        dto.activityName = instance.getRecurringActivity().getActivityName();
        dto.activityType = instance.getRecurringActivity().getActivityType();
        dto.dayOfWeek = instance.getRecurringActivity().getDayOfWeek();
        dto.startTime = instance.getRecurringActivity().getStartTime();
        dto.durationMinutes = instance.getRecurringActivity().getDurationMinutes();
        dto.weekStartDate = instance.getWeekStartDate();
        dto.dueDate = instance.getDueDate();
        dto.completed = instance.isCompleted();
        dto.backlogged = instance.isBacklogged();
        return dto;
    }

    public Long getId() { return id; }
    public Long getRecurringActivityId() { return recurringActivityId; }
    public String getCourseName() { return courseName; }
    public String getActivityName() { return activityName; }
    public ActivityType getActivityType() { return activityType; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public LocalDate getWeekStartDate() { return weekStartDate; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isCompleted() { return completed; }
    public boolean isBacklogged() { return backlogged; }
}
