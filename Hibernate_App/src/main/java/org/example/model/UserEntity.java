package org.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, name = "user_name")
    @NonNull
    private String name;

    @Column(nullable = false, unique = true, name = "user_email")
    @NonNull
    private String email;

    @Column(name = "user_age")
    private Integer age;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public UserEntity(@NonNull String name, @NonNull String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.LocalDateTime.now();
    }

}
