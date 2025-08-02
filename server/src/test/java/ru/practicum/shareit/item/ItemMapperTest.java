package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ItemMapperTest {

    @Autowired
    private ItemMapper mapper;

    @Test
    void testToItemDto_withBookings_shouldMapAllFields() {
        User owner = new User(1L, "Owner", "owner@mail.ru");
        Item item = new Item(2L, "Drill", "Powerful drill", owner, true, null);
        BookingItemDto lastBooking = new BookingItemDto(3L, 4L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(1));
        BookingItemDto nextBooking = new BookingItemDto(5L, 6L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1));
        ItemOwnerViewDto dto = mapper.toItemDto(item, lastBooking, nextBooking);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
        assertThat(dto.getDescription()).isEqualTo(item.getDescription());
        assertThat(dto.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(dto.getLastBooking()).isEqualTo(lastBooking);
        assertThat(dto.getNextBooking()).isEqualTo(nextBooking);
    }

    @Test
    void testToItemDto_withoutBookings_shouldMapAllFieldsAndNullBookings() {
        User owner = new User(1L, "Owner", "owner@mail.ru");
        Item item = new Item(2L, "Drill", "Powerful drill", owner, true, null);
        ItemOwnerViewDto dto = mapper.toItemDto(item, null, null);
        assertThat(dto).isNotNull();
        assertThat(dto.getLastBooking()).isNull();
        assertThat(dto.getNextBooking()).isNull();
    }

    @Test
    void testToItem_shouldMapAllFieldsAndIgnoreId() {
        User owner = new User(1L, "Owner", "owner@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "Need a hammer", owner, LocalDateTime.now());
        ItemOwnerViewDto dto = new ItemOwnerViewDto(null, "New Item", "New description", true, null, null, null);
        Item item = mapper.toItem(dto, owner, itemRequest);
        assertThat(item).isNotNull();
        assertThat(item.getId()).isNull();
        assertThat(item.getName()).isEqualTo(dto.getName());
        assertThat(item.getDescription()).isEqualTo(dto.getDescription());
        assertThat(item.getAvailable()).isEqualTo(dto.getAvailable());
        assertThat(item.getOwner()).isEqualTo(owner);
        assertThat(item.getRequest()).isEqualTo(itemRequest);
    }

    @Test
    void testUpdateFromDto_shouldUpdateNonNullFields() {
        User owner = new User(1L, "Owner", "owner@mail.ru");
        Item existingItem = new Item(2L, "Old Name", "Old description", owner, false, null);
        ItemOwnerViewDto updateDto = new ItemOwnerViewDto(null, "Updated Name", null, true, null, null, null);
        mapper.updateFromDto(existingItem, updateDto);
        assertThat(existingItem.getName()).isEqualTo("Updated Name");
        assertThat(existingItem.getDescription()).isEqualTo("Old description");
        assertThat(existingItem.getAvailable()).isEqualTo(true);
    }

    @Test
    void testUpdateFromDto_withNullDto_shouldNotChangeItem() {
        User owner = new User(1L, "Owner", "owner@mail.ru");
        Item existingItem = new Item(2L, "Old Name", "Old description", owner, false, null);
        ItemOwnerViewDto updateDto = new ItemOwnerViewDto();
        mapper.updateFromDto(existingItem, updateDto);
        assertThat(existingItem.getName()).isEqualTo("Old Name");
        assertThat(existingItem.getDescription()).isEqualTo("Old description");
        assertThat(existingItem.getAvailable()).isEqualTo(false);
    }

    @Test
    void testToItemShortDto_shouldMapAllFields() {
        User owner = new User(1L, "Owner", "owner@mail.ru");
        Item item = new Item(2L, "Drill", "Powerful drill", owner, true, null);
        ItemShortDto dto = mapper.toItemShortDto(item);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
    }

    @Test
    void testToItemBookingDto_withBookingsAndComments_shouldMapAllFields() {
        User owner = new User(1L, "Owner", "owner@mail.ru");
        Item item = new Item(2L, "Drill", "Powerful drill", owner, true, null);
        BookingItemDto lastBooking = new BookingItemDto(3L, 4L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(1));
        BookingItemDto nextBooking = new BookingItemDto(5L, 6L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1));
        List<CommentDto> comments = Collections.singletonList(new CommentDto());
        ItemDetailsDto dto = mapper.toItemBookingDto(item, lastBooking, nextBooking, comments);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
        assertThat(dto.getDescription()).isEqualTo(item.getDescription());
        assertThat(dto.getLastBooking()).isEqualTo(lastBooking);
        assertThat(dto.getNextBooking()).isEqualTo(nextBooking);
        assertThat(dto.getComments()).isEqualTo(comments);
    }

    @Test
    void testToItemRequestResponseDto_shouldMapOwnerAndRequestId() {
        User owner = new User(1L, "Owner", "owner@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "Need a hammer", owner, LocalDateTime.now());
        Item item = new Item(2L, "Hammer", "A tool", owner, true, itemRequest);
        ItemRequestResponseDto dto = mapper.toItemRequestResponseDto(item);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
        assertThat(dto.getOwnerId()).isEqualTo(owner.getId());
        assertThat(dto.getRequestId()).isEqualTo(itemRequest.getId());
    }
}