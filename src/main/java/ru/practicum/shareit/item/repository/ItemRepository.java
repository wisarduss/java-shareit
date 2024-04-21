package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemsByOwnerId(Long userId);

    @Query("select i.available from Item i where i.id = ?1")
    boolean isItemAvailable(Long id);

    @Query("select i from Item i where i.available = true "
            + "and upper(i.description) like upper(concat('%', ?1, '%')) "
            + "or upper(i.name) like upper(concat('%', ?1, '%')) "
            + "group by i.id")
    List<Item> search(String text);

    @Modifying(clearAutomatically = true)
    @Query("update Item i set i.available = :available where i.id = :id")
    void updateItemAvailableById(@Param("id") Long id, @Param("available") boolean available);
}
