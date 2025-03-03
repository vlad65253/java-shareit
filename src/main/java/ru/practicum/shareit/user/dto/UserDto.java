package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserDto {
    Long id;
    String name;
    @NotBlank
    @Email
    String email;
}
