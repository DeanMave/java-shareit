package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemOwnerViewDto toItemDto(Item item, BookingItemDto last, BookingItemDto next) {
        if (item == null) {
            return null;
        }
        return new ItemOwnerViewDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                last,
                next
        );
    }

    public static Item toItem(ItemOwnerViewDto itemDto, User owner) {
        return new Item(
                null,
                itemDto.getName(),
                itemDto.getDescription(),
                owner,
                itemDto.getAvailable(),
                null
        );
    }

    public static void updateFromDto(Item item, ItemOwnerViewDto itemDto) {
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
    }

    public static ItemShortDto toItemShortDto(Item item) {
        if (item == null) {
            return null;
        }
        return new ItemShortDto(item.getId(), item.getName());
    }

    public static ItemDetailsDto toItemBookingDto(Item item,
                                                  BookingItemDto lastBooking,
                                                  BookingItemDto nextBooking,
                                                  List<CommentDto> comments) {
        return new ItemDetailsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments
        );
    }

    public static ItemSimpleDto toItemResponseDto(Item item) {
        if (item == null) {
            return null;
        }
        return new ItemSimpleDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }
}
