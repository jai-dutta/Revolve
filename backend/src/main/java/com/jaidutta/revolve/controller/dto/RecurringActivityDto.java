package com.jaidutta.revolve.controller.dto;

import com.jaidutta.revolve.definitions.ActivityType;
import com.jaidutta.revolve.entity.RecurringActivity;
import jakarta.validation.constraints.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class RecurringActivityDto {

    private Long id;

    @NotBlank
    @Size(min = 1, max = 64, message = "Course name must be between 1 and 64 characters")
    private String courseName;

    @NotBlank
    @Size(min = 1, max = 64, message = "Activity name must be between 1 and 64 characters")
    private String activityName;

    @NotNull
    private ActivityType activityType;

    @NotNull
    private DayOfWeek dayOfWeek;

    @NotNull
    private LocalTime startTime;

    @NotNull
    @Positive
    private Integer durationMinutes;

    public RecurringActivityDto() {}

    public RecurringActivityDto(String courseName, String activityName, ActivityType activityType, DayOfWeek dayOfWeek, LocalTime startTime, Integer durationMinutes) {
        this.courseName = courseName;
        this.activityName = activityName;
        this.activityType = activityType;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
    }

    public static RecurringActivityDto mapToRecurringActivity(RecurringActivity recurringActivity) {
        RecurringActivityDto recurringActivityDto = new RecurringActivityDto();
        recurringActivityDto.setId(recurringActivity.getId());
        recurringActivityDto.setCourseName(recurringActivity.getCourseName());
        recurringActivityDto.setActivityName(recurringActivity.getActivityName());
        recurringActivityDto.setActivityType(recurringActivity.getActivityType());
        recurringActivityDto.setDayOfWeek(recurringActivity.getDayOfWeek());
        recurringActivityDto.setStartTime(recurringActivity.getStartTime());
        recurringActivityDto.setDurationMinutes(recurringActivity.getDurationMinutes());
        return recurringActivityDto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {this.id = id;}

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
