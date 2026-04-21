package com.jaidutta.revolve.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
    name = "activity_instances",
    uniqueConstraints = @UniqueConstraint(columnNames = {"recurring_activity_id", "week_start_date"})
)
public class ActivityInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recurring_activity_id", nullable = false)
    private RecurringActivity recurringActivity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate weekStartDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(nullable = false)
    private boolean backlogged = false;

    protected ActivityInstance() {}

    public ActivityInstance(RecurringActivity recurringActivity, User user, LocalDate weekStartDate, LocalDate dueDate) {
        this.recurringActivity = recurringActivity;
        this.user = user;
        this.weekStartDate = weekStartDate;
        this.dueDate = dueDate;
    }

    public Long getId() { return id; }

    public RecurringActivity getRecurringActivity() { return recurringActivity; }

    public User getUser() { return user; }

    public LocalDate getWeekStartDate() { return weekStartDate; }

    public LocalDate getDueDate() { return dueDate; }

    public boolean isCompleted() { return completed; }

    public void setCompleted(boolean completed) { this.completed = completed; }

    public boolean isBacklogged() { return backlogged; }

    public void setBacklogged(boolean backlogged) { this.backlogged = backlogged; }
}
