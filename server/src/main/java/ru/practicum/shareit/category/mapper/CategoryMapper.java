package ru.practicum.shareit.category.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.category.dto.CategoryDto;
import ru.practicum.shareit.category.model.Category;

@UtilityClass
public class CategoryMapper {

    public static Category categoryDtoToCategory(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .title(categoryDto.getTitle())
                .build();
    }

    public static CategoryDto categoryToCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .title(category.getTitle())
                .build();
    }
}
