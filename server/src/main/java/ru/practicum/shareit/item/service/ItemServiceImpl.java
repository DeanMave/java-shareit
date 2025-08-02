package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.enums.StatusBooking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import ru.practicum.shareit.exception.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemOwnerViewDto addItem(Long ownerId, ItemOwnerViewDto itemDto) {
        log.info("Добавление новой вещи для пользователя ID {}. DTO: {}", ownerId, itemDto); // Логируем весь DTO
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            log.info("Поиск запроса с ID: {}", itemDto.getRequestId());
            request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElse(null);
            if (request == null) {
                log.warn("Запрос с ID {} не найден при добавлении вещи.", itemDto.getRequestId());
            } else {
                log.info("Запрос с ID {} найден: {}", itemDto.getRequestId(), request);
            }
        }

        Item item = itemMapper.toItem(itemDto, owner, request);
        log.info("Созданная сущность Item перед сохранением: {}", item); // Посмотрите, есть ли тут request
        Item savedItem = itemRepository.save(item);
        log.info("Вещь добавлена: {}", savedItem); // Посмотрите, сохранился ли request в savedItem
        return itemMapper.toItemDto(savedItem, null, null);
    }

    @Override
    @Transactional
    public ItemOwnerViewDto updateItem(Long ownerId, Long itemId, ItemOwnerViewDto itemDto) {
        log.info("Обновление вещи ID {} для пользователя ID {}: {}", itemId, ownerId, itemDto.getName());
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не найден");
        }
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена."));
        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Редактировать может только владелец вещи.");
        }
        itemMapper.updateFromDto(existingItem, itemDto);
        Item updatedItem = itemRepository.save(existingItem);
        log.info("Вещь обновлена: {}", updatedItem);
        return itemMapper.toItemDto(updatedItem, null, null);
    }

    @Override
    public ItemDetailsDto getItemById(Long itemId, Long requesterId) {
        log.info("Получение вещи по ID: {}", itemId);
        if (!userRepository.existsById(requesterId)) {
            throw new NotFoundException("Пользователь с id " + requesterId + " не найден");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена."));

        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
        List<CommentDto> commentDto = comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());

        BookingItemDto lastBookingDto = null;
        BookingItemDto nextBookingDto = null;

        if (item.getOwner().getId().equals(requesterId)) {
            LocalDateTime now = LocalDateTime.now();

            Optional<Booking> lastBooking = bookingRepository.findTopByItem_IdAndEndBeforeAndStatusEqualsOrderByEndDesc(
                    itemId, now, StatusBooking.APPROVED);
            if (lastBooking.isPresent()) {
                lastBookingDto = bookingMapper.toBookingItemDto(lastBooking.get());
            }

            Optional<Booking> nextBooking = bookingRepository.findTopByItem_IdAndStartAfterAndStatusEqualsOrderByStartAsc(
                    itemId, now, StatusBooking.APPROVED);
            if (nextBooking.isPresent()) {
                nextBookingDto = bookingMapper.toBookingItemDto(nextBooking.get());
            }
        }
        return itemMapper.toItemBookingDto(item, lastBookingDto, nextBookingDto, commentDto);
    }

    @Override
    public List<ItemOwnerViewDto> getAllItemsByOwner(Long ownerId) {
        log.info("Получение всех вещей для владельца ID: {}", ownerId);
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не найден");
        }
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        LocalDateTime now = LocalDateTime.now();
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        List<Booking> allApprovedBookingsForOwnersItems = bookingRepository
                .findByItem_IdInAndStatusEqualsOrderByStartAsc(itemIds, StatusBooking.APPROVED);

        return items.stream()
                .map(item -> mapItemToItemDtoWithBookings(item, allApprovedBookingsForOwnersItems, now))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemSimpleDto> searchItems(String text) {
        log.info("Поиск вещей по тексту: {}", text);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> foundItems = itemRepository.search(text.toUpperCase());
        return foundItems.stream()
                .map(itemMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
        List<Booking> bookings = bookingRepository.findByBooker_IdAndItem_IdAndEndBeforeAndStatusEquals(
                userId, itemId, LocalDateTime.now(), StatusBooking.APPROVED);
        if (bookings.isEmpty()) {
            throw new BadRequestException("Пользователь с ID " + userId +
                                          " не брал вещь с ID " + itemId + " в аренду или срок аренды еще не закончен.");
        }
        Comment comment = commentMapper.toComment(commentDto, item, user);
        Comment savedComment = commentRepository.save(comment);
        savedComment.getAuthor().getName();
        return commentMapper.toCommentDto(savedComment);
    }


    private ItemOwnerViewDto mapItemToItemDtoWithBookings(Item item, List<Booking> allApprovedBookings, LocalDateTime now) {
        BookingItemDto lastBookingDto = null;
        BookingItemDto nextBookingDto = null;

        List<Booking> itemBookings = allApprovedBookings.stream()
                .filter(b -> b.getItem().getId().equals(item.getId()))
                .collect(Collectors.toList());

        Optional<Booking> lastBooking = itemBookings.stream()
                .filter(b -> b.getEnd().isBefore(now))
                .max(Comparator.comparing(Booking::getEnd));

        Optional<Booking> nextBooking = itemBookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart));

        if (lastBooking.isPresent()) {
            lastBookingDto = bookingMapper.toBookingItemDto(lastBooking.get());
        }
        if (nextBooking.isPresent()) {
            nextBookingDto = bookingMapper.toBookingItemDto(nextBooking.get());
        }

        return itemMapper.toItemDto(item, lastBookingDto, nextBookingDto);
    }
}