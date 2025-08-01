package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
											  @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
											  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
											  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") @Positive long ownerId,
												   @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
												   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
												   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get owner bookings with state {}, ownerId={}, from={}, size={}", stateParam, ownerId, from, size);
		return bookingClient.getOwnerBookings(ownerId, state, from, size);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
											 @PathVariable @Positive Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@PostMapping
	public ResponseEntity<Object> addNewBooking(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
												@RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
												@PathVariable @Positive Long bookingId,
												@RequestParam Boolean approved) {
		log.info("Updating booking {}, userId={}, approved={}", bookingId, userId, approved);
		return bookingClient.updateBooking(userId, bookingId, approved);
	}
}