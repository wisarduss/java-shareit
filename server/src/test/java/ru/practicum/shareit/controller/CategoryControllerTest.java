package ru.practicum.shareit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.category.controller.CategoryController;
import ru.practicum.shareit.category.dto.CategoryDto;
import ru.practicum.shareit.category.service.CategoryService;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private ItemResponseDto itemResponseDto;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .photoUrl("http://photo.com/1")
                .price(new BigDecimal("100.50"))
                .available(true)
                .requestId(10L)
                .build();

        categoryDto = CategoryDto.builder()
                .id(1L)
                .title("Category 1")
                .build();
    }

    @Test
    void getItemForCatId_ShouldReturnItemsList() {
        Long categoryId = 1L;
        when(itemService.getItemForCatId(categoryId)).thenReturn(List.of(itemResponseDto));

        List<ItemResponseDto> result = categoryController.getItemForCatId(categoryId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemResponseDto.getId(), result.get(0).getId());
        assertEquals(itemResponseDto.getName(), result.get(0).getName());
        verify(itemService).getItemForCatId(categoryId);
    }

    @Test
    void addCategory_ShouldReturnCreatedCategory() {
        when(categoryService.addCategory(any(CategoryDto.class))).thenReturn(categoryDto);

        CategoryDto result = categoryController.addCategory(categoryDto);

        assertNotNull(result);
        assertEquals(categoryDto.getId(), result.getId());
        assertEquals(categoryDto.getTitle(), result.getTitle());
        verify(categoryService).addCategory(categoryDto);
    }

    @Test
    void getAll_ShouldReturnItemsMap() {
        Map<String, List<ItemResponseDto>> expectedMap = Map.of(
                "items", List.of(itemResponseDto)
        );
        when(itemService.getAll()).thenReturn(expectedMap);

        Map<String, List<ItemResponseDto>> result = categoryController.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("items"));
        assertEquals(1, result.get("items").size());
        assertEquals(itemResponseDto.getId(), result.get("items").get(0).getId());
        verify(itemService).getAll();
    }

    @Test
    void getItemForCatId_ShouldReturnEmptyListWhenNoItems() {
        Long categoryId = 99L;
        when(itemService.getItemForCatId(categoryId)).thenReturn(List.of());

        List<ItemResponseDto> result = categoryController.getItemForCatId(categoryId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemService).getItemForCatId(categoryId);
    }

    @Test
    void addCategory_ShouldVerifyServiceCall() {
        CategoryDto newCategory = CategoryDto.builder()
                .title("New Category")
                .build();

        when(categoryService.addCategory(any())).thenReturn(newCategory);

        CategoryDto result = categoryController.addCategory(newCategory);

        assertNotNull(result);
        assertEquals("New Category", result.getTitle());
        verify(categoryService).addCategory(newCategory);
    }
}