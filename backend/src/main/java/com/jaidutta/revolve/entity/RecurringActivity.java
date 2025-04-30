package com.jaidutta.revolve.entity;

import com.jaidutta.revolve.definitions.ActivityType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "recurring_activities")
public class RecurringActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Size(min = 1, max = 64, message = "Course name must be between 1 and 64 characters")
    @Column(nullable = false)
    private String courseName;

    @NotBlank
    @Size(min = 1, max = 64, message = "Activity name must be between 1 and 64 characters")
    @Column(nullable = false)
    private String activityName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType activityType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private Integer durationMinutes;

    // Getters
    public Long getId() {
        return this.id;
    }

    public User getUser() {
        return this.user;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public String getActivityName() {
        return this.activityName;
    }

    public ActivityType getActivityType() {
        return this.activityType;
    }

    public DayOfWeek getDayOfWeek() {
        return this.dayOfWeek;
    }

    public LocalTime getStartTime() {
        return this.startTime;
    }

    public Integer getDurationMinutes() {
        return this.durationMinutes;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

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
