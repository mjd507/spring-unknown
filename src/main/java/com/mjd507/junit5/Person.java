package com.mjd507.junit5;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Person {
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate dateOfBirth;

    public enum Gender {
        F,M
    }
}
