package com.jaidutta.revolve.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    protected User() {}

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
}
