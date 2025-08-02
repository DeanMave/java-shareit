package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId);

    List<Item> findByRequest_IdIn(Set<Long> requestIds);

    List<Item> findByRequest_Id(Long requestId);

    @Query(" select i from Item i " +
           "where i.available = true and (upper(i.name) like concat('%', ?1, '%') " +
           " or upper(i.description) like concat('%', ?1, '%'))")
    List<Item> search(String text);
}
