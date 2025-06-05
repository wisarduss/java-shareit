package ru.practicum.shareit.category.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.category.dto.CategoryDto;
import ru.practicum.shareit.category.service.CategoryService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/catalog")
@Tag(name = "Category Controller", description = "Управление категориями")
public class CategoryController {

    private final ItemService itemService;
    private final CategoryService categoryService;

    @GetMapping("/{catId}")
    public List<ItemResponseDto> getItemForCatId(@PathVariable Long catId) {
        return itemService.getItemForCatId(catId);
    }

    @PostMapping
    public CategoryDto addCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.addCategory(categoryDto);
    }

    @GetMapping()
    public Map<String, List<ItemResponseDto>> getAll() {
        return itemService.getAll();
    }
}
