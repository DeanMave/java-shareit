package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemOwnerViewDto;
import ru.practicum.shareit.item.dto.ItemSimpleDto;

import java.util.List;

public interface ItemService {
    ItemOwnerViewDto addItem(Long ownerId, ItemOwnerViewDto itemDto);

    ItemOwnerViewDto updateItem(Long ownerId, Long itemId, ItemOwnerViewDto itemDto);

    ItemDetailsDto getItemById(Long itemId, Long requesterId);

    List<ItemOwnerViewDto> getAllItemsByOwner(Long ownerId);

    List<ItemSimpleDto> searchItems(String text);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);
}
