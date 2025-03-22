package com.example.tdd.domain.student;

import com.example.tdd.global.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Student extends BaseTime {
    @Column(length = 10)
    private String name;

    private String password;

    private String nickname;

    @Column(unique = true, length = 50)
    private String apiKey;

    public boolean isAdmin() {
        return "admin".equals(name);
    }

    public boolean matchPassword(String password) {
        return this.password.equals(password);
    }
}