package ru.practicum.shareit.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
