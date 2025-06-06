package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;
    @NotBlank()
    private String name;
    @NotBlank()
    private String description;
    @NotBlank
    private String photoUrl;
    @NotNull
    private BigDecimal price;
    @NotNull()
    private Boolean available;
    private Long requestId;

    @NotEmpty
    private Set<Long> catIds;
}
