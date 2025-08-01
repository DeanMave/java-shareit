package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.enums.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemOwnerViewDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingServiceImpTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    private UserDto owner;
    private UserDto booker;
    private ItemOwnerViewDto itemDto;

    @BeforeEach
    void setUp() {
        owner = userService.addNewUser(new UserDto(null, "Owner", "owner@mail.ru"));
        booker = userService.addNewUser(new UserDto(null, "Booker", "booker@mail.ru"));

        itemDto = new ItemOwnerViewDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Простая дрель");
        itemDto.setAvailable(true);
        itemDto = itemService.addItem(owner.getId(), itemDto);
    }

    @Test
    void addNewBooking_shouldSaveAndReturnBooking() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(itemDto.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto savedBooking = bookingService.addNewBooking(requestDto, booker.getId());

        assertThat(savedBooking).isNotNull();
        assertThat(savedBooking.getId()).isNotNull();
        assertThat(savedBooking.getItem().getId()).isEqualTo(itemDto.getId());
        assertThat(savedBooking.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(savedBooking.getStatus()).isEqualTo(StatusBooking.WAITING);
    }

    @Test
    void addNewBooking_whenItemNotAvailable_shouldThrowBadRequestException() {
        ItemOwnerViewDto unavailableItem = new ItemOwnerViewDto();
        unavailableItem.setName("Недоступная вещь");
        unavailableItem.setDescription("Описание");
        unavailableItem.setAvailable(false);
        unavailableItem = itemService.addItem(owner.getId(), unavailableItem);

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(unavailableItem.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThatThrownBy(() -> bookingService.addNewBooking(requestDto, booker.getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Вещь не доступна для бронирования!");
    }

    @Test
    void addNewBooking_whenOwnerTriesToBookHisOwnItem_shouldThrowBadRequestException() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(itemDto.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThatThrownBy(() -> bookingService.addNewBooking(requestDto, owner.getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Владелец вещи не может бронировать свою же вещь!");
    }

    @Test
    void getBookingById_shouldReturnBooking_forOwnerOrBooker() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(itemDto.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingResponseDto createdBooking = bookingService.addNewBooking(requestDto, booker.getId());

        BookingResponseDto foundByOwner = bookingService.getBookingById(createdBooking.getId(), owner.getId());
        assertThat(foundByOwner).isNotNull();

        BookingResponseDto foundByBooker = bookingService.getBookingById(createdBooking.getId(), booker.getId());
        assertThat(foundByBooker).isNotNull();
    }

    @Test
    void getBookingById_whenUserIsNotOwnerOrBooker_shouldThrowNotFoundException() {
        UserDto anotherUser = userService.addNewUser(new UserDto(null, "Another", "another@mail.ru"));

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(itemDto.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingResponseDto createdBooking = bookingService.addNewBooking(requestDto, booker.getId());

        assertThatThrownBy(() -> bookingService.getBookingById(createdBooking.getId(), anotherUser.getId()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Данные о бронировании может получать только владелец вещи или автор бронирования!");
    }

    @Test
    void updateBooking_whenApproved_shouldUpdateStatusToApproved() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(itemDto.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingResponseDto createdBooking = bookingService.addNewBooking(requestDto, booker.getId());

        BookingResponseDto updatedBooking = bookingService.updateBooking(createdBooking.getId(), owner.getId(), true);
        assertThat(updatedBooking.getStatus()).isEqualTo(StatusBooking.APPROVED);
    }

    @Test
    void updateBooking_whenRejected_shouldUpdateStatusToRejected() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(itemDto.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingResponseDto createdBooking = bookingService.addNewBooking(requestDto, booker.getId());

        BookingResponseDto updatedBooking = bookingService.updateBooking(createdBooking.getId(), owner.getId(), false);
        assertThat(updatedBooking.getStatus()).isEqualTo(StatusBooking.REJECTED);
    }

    @Test
    void getUserBookings_whenStateIsAll_shouldReturnAllBookings() {
        BookingRequestDto requestDto1 = new BookingRequestDto();
        requestDto1.setItemId(itemDto.getId());
        requestDto1.setStart(LocalDateTime.now().plusDays(1));
        requestDto1.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.addNewBooking(requestDto1, booker.getId());

        BookingRequestDto requestDto2 = new BookingRequestDto();
        requestDto2.setItemId(itemDto.getId());
        requestDto2.setStart(LocalDateTime.now().plusDays(3));
        requestDto2.setEnd(LocalDateTime.now().plusDays(4));
        bookingService.addNewBooking(requestDto2, booker.getId());

        List<BookingResponseDto> bookings = bookingService.getUserBookings(booker.getId(), "ALL");
        assertThat(bookings).hasSize(2);
    }
}
