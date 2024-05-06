package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotEmpty;

@Data
@Builder(toBuilder = true)
@Jacksonized
public class RequestDto {

    @NotEmpty
    private String description;
}
