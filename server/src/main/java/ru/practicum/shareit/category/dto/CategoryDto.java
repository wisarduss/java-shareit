package ru.practicum.shareit.category.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class CategoryDto {

    private Long id;
    private String title;
}
