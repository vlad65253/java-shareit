package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class User {
    Long id;
    String name;
    @NotBlank
    @Email
    String email;
}
