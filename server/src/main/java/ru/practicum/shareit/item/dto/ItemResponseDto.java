package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private String photoUrl;
    private BigDecimal price;
    private Boolean available;
    private Long requestId;
}
