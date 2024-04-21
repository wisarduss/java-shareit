package ru.practicum.shareit.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class MainDto {
    private Long id;
    private String name;
}
