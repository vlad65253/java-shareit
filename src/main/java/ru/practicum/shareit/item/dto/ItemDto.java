package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ItemDto {
    Long id;
    @NotNull
    @NotBlank
    String name;
    @NotNull
    String description;
    @NotNull
    Boolean available;
    Long requestId;
}
