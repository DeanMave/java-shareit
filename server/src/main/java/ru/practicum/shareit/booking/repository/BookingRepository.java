package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.StatusBooking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdAndItem_IdAndEndBeforeAndStatusEquals(Long bookerId, Long itemId, LocalDateTime now, StatusBooking status);

    List<Booking> findByItem_IdInAndStatusEqualsOrderByStartAsc(List<Long> itemIds, StatusBooking status);

    Optional<Booking> findTopByItem_IdAndEndBeforeAndStatusEqualsOrderByEndDesc(Long itemId, LocalDateTime now, StatusBooking status);

    Optional<Booking> findTopByItem_IdAndStartAfterAndStatusEqualsOrderByStartAsc(Long itemId, LocalDateTime now, StatusBooking status);

    Optional<Booking> findByIdAndItem_Owner_Id(Long id, Long ownerId);

    List<Booking> findByItem_IdAndEndAfterAndStartBefore(Long bookerId, LocalDateTime end, LocalDateTime start);

    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBooker_IdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBooker_IdAndStatusEqualsOrderByStartDesc(Long bookerId, StatusBooking status);

    List<Booking> findByItem_OwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItem_OwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItem_OwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItem_OwnerIdAndStatusEqualsOrderByStartDesc(Long ownerId, StatusBooking status);

}
