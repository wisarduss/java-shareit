package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotEmpty;

@Data
@Builder(toBuilder = true)
@Jacksonized
public class CommentUpdateDto {

    @NotEmpty
    private String text;
}
