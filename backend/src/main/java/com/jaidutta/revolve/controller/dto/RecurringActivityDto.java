package com.jaidutta.revolve.controller.dto;

import com.jaidutta.revolve.definitions.ActivityType;
import com.jaidutta.revolve.entity.RecurringActivity;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

    private LocalTime startTime;

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
        return id; }
    public String getCourseName() {
        return courseName;
    }
    public String getActivityName() {
        return activityName;
    }
    public ActivityType getActivityType() {
        return activityType;
    }
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
    public LocalTime getStartTime() {
        return startTime;
    }
    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setId(Long id) { this.id = id; }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
