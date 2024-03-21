package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder(toBuilder = true)
public class ItemDto {
    // идентификатор предмета:
    private Long id;

    // название предмета:
    @NotBlank()
    private String name;

    // описание предмета:
    @NotBlank()
    private String description;

    // доступно для аренды или нет:
    @NotNull()
    private Boolean available;
}
