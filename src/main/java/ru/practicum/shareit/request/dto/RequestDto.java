package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Data
@Builder(toBuilder = true)
@Jacksonized
public class RequestDto {

    @NotBlank
    private String description;
}
