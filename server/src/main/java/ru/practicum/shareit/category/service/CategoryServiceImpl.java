package ru.practicum.shareit.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.category.dto.CategoryDto;
import ru.practicum.shareit.category.mapper.CategoryMapper;
import ru.practicum.shareit.category.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        return CategoryMapper.categoryToCategoryDto(categoryRepository
                .save(CategoryMapper.categoryDtoToCategory(categoryDto)));
    }
}
