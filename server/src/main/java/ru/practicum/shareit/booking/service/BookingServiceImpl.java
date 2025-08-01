package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.enums.StatusBooking;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;


    @Override
    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        log.info("Получение бронирования по ID: {}", bookingId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено."));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new AccessDeniedException("Данные о бронировании может получать только владелец вещи или автор бронирования!");
        }
        return bookingMapper.toBookingResponseDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto addNewBooking(BookingRequestDto requestDto, Long userId) {
        log.info("Добавление нового бронирования для пользователя с ID " + userId);
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + requestDto.getItemId() + " не найдена."));
        if (booker.getId().equals(item.getOwner().getId())) {
            throw new BadRequestException("Владелец вещи не может бронировать свою же вещь!");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь не доступна для бронирования!");
        }
        if (requestDto.getEnd().isBefore(requestDto.getStart()) || requestDto.getEnd().isEqual(requestDto.getStart())) {
            throw new BadRequestException("Дата окончания бронирования не может быть раньше или равна дате начала.");
        }
        List<Booking> existingBookings = repository.findByItem_IdAndEndAfterAndStartBefore(
                item.getId(), requestDto.getStart(), requestDto.getEnd()
        );
        if (!existingBookings.isEmpty()) {
            throw new ConflictException("Вещь уже забронирована на эти даты.");
        }
        Booking booking = bookingMapper.toBooking(requestDto, item, booker);
        Booking savedBooking = repository.save(booking);
        log.info("Бронирование произведено: {}", savedBooking);
        return bookingMapper.toBookingResponseDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto updateBooking(Long bookingId, Long userId, Boolean approved) {
        Booking booking = repository.findByIdAndItem_Owner_Id(bookingId, userId)
                .orElseThrow(() -> new BadRequestException("Бронирование не найдено или доступ запрещен"));
        if (!booking.getStatus().equals(StatusBooking.WAITING)) {
            throw new BadRequestException("Бронирование можно подтвердить только в статусе ожидания.");
        }
        if (approved) {
            booking.setStatus(StatusBooking.APPROVED);
        } else {
            booking.setStatus(StatusBooking.REJECTED);
        }
        Booking savedBooking = repository.save(booking);
        return bookingMapper.toBookingResponseDto(savedBooking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long bookerId, String stringState) {
        if (!userRepository.existsById(bookerId)) {
            throw new NotFoundException("Пользователь с id " + bookerId + " не найден");
        }
        State state;
        try {
            state = State.valueOf(stringState.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Неверный параметр state: " + stringState);
        }

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                bookings = repository.findByBooker_IdOrderByStartDesc(bookerId);
                break;
            case CURRENT:
                bookings = repository.findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, now, now);
                break;
            case PAST:
                bookings = repository.findByBooker_IdAndEndBeforeOrderByStartDesc(bookerId, now);
                break;
            case FUTURE:
                bookings = repository.findByBooker_IdAndStartAfterOrderByStartDesc(bookerId, now);
                break;
            case WAITING:
                bookings = repository.findByBooker_IdAndStatusEqualsOrderByStartDesc(bookerId, StatusBooking.WAITING);
                break;
            case REJECTED:
                bookings = repository.findByBooker_IdAndStatusEqualsOrderByStartDesc(bookerId, StatusBooking.REJECTED);
                break;
            default:
                throw new BadRequestException("Неверный параметр state: " + stringState);
        }

        return bookings.stream()
                .map(bookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long userId, String stringState) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        State state;
        try {
            state = State.valueOf(stringState.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Неверный параметр state: " + stringState);
        }

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                bookings = repository.findByItem_OwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = repository.findByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case PAST:
                bookings = repository.findByItem_OwnerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                bookings = repository.findByItem_OwnerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case WAITING:
                bookings = repository.findByItem_OwnerIdAndStatusEqualsOrderByStartDesc(userId, StatusBooking.WAITING);
                break;
            case REJECTED:
                bookings = repository.findByItem_OwnerIdAndStatusEqualsOrderByStartDesc(userId, StatusBooking.REJECTED);
                break;
            default:
                throw new BadRequestException("Неверный параметр state: " + stringState);
        }

        return bookings.stream()
                .map(bookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }
}
