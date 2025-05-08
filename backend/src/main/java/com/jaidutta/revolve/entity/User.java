package com.jaidutta.revolve.entity;

import jakarta.persistence.*;

import java.util.TimeZone;

@Entity @Table(name = "users") public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private TimeZone timeZone = TimeZone.getDefault();

    @Column(nullable = false)
    private int eventsCurrentlyRegistered = 0;

    protected User() {
    }

    public User(String username, String hashedPassword) {
        this.username = username;
        this.password = hashedPassword;
    }

    public Long getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String hashedPassword) {
        this.password = hashedPassword;
    }

    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public int getEventsCurrentlyRegistered() {return this.eventsCurrentlyRegistered;}

    public void setEventsCurrentlyRegistered(int eventsCurrentlyRegistered) {
        this.eventsCurrentlyRegistered = eventsCurrentlyRegistered;
    }
}
