package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemOwnerViewDto;
import ru.practicum.shareit.item.dto.ItemSimpleDto;
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
public class ItemServiceImpTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;

    private UserDto owner;
    private UserDto otherUser;
    private ItemOwnerViewDto itemDto;

    @BeforeEach
    void setUp() {
        owner = userService.addNewUser(new UserDto(null, "Owner", "owner@mail.ru"));
        otherUser = userService.addNewUser(new UserDto(null, "Other User", "other@mail.ru"));

        ItemOwnerViewDto newItem = new ItemOwnerViewDto();
        newItem.setName("Дрель");
        newItem.setDescription("Простая дрель");
        newItem.setAvailable(true);
        itemDto = itemService.addItem(owner.getId(), newItem);
    }

    @Test
    void addItem_shouldSaveAndReturnItem() {
        ItemOwnerViewDto newItemDto = new ItemOwnerViewDto();
        newItemDto.setName("Молоток");
        newItemDto.setDescription("Простой молоток");
        newItemDto.setAvailable(true);

        ItemOwnerViewDto savedItem = itemService.addItem(owner.getId(), newItemDto);

        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getName()).isEqualTo("Молоток");
    }

    @Test
    void addItem_whenOwnerNotFound_shouldThrowNotFoundException() {
        ItemOwnerViewDto newItemDto = new ItemOwnerViewDto();
        newItemDto.setName("Молоток");
        newItemDto.setDescription("Простой молоток");
        newItemDto.setAvailable(true);

        assertThatThrownBy(() -> itemService.addItem(999L, newItemDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id 999 не найден");
    }

    @Test
    void updateItem_whenUserIsOwner_shouldUpdateItem() {
        ItemOwnerViewDto updateDto = new ItemOwnerViewDto();
        updateDto.setName("Обновленная дрель");

        ItemOwnerViewDto updatedItem = itemService.updateItem(owner.getId(), itemDto.getId(), updateDto);

        assertThat(updatedItem.getName()).isEqualTo("Обновленная дрель");
        assertThat(updatedItem.getDescription()).isEqualTo(itemDto.getDescription());
    }

    @Test
    void updateItem_whenUserIsNotOwner_shouldThrowAccessDeniedException() {
        ItemOwnerViewDto updateDto = new ItemOwnerViewDto();
        updateDto.setName("Обновленная дрель");

        assertThatThrownBy(() -> itemService.updateItem(otherUser.getId(), itemDto.getId(), updateDto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Редактировать может только владелец вещи.");
    }

    @Test
    void getItemById_shouldReturnItemDetailsForOwner() {
        BookingRequestDto bookingRequest = new BookingRequestDto(itemDto.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        BookingResponseDto booking = bookingService.addNewBooking(otherUser.getId(), bookingRequest);
        bookingService.updateBooking(booking.getId(), owner.getId(), true);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Комментарий к вещи");
        itemService.addComment(itemDto.getId(), otherUser.getId(), commentDto);

        ItemDetailsDto itemDetails = itemService.getItemById(itemDto.getId(), owner.getId());

        assertThat(itemDetails).isNotNull();
        assertThat(itemDetails.getLastBooking()).isNotNull();
        assertThat(itemDetails.getComments()).hasSize(1);
    }

    @Test
    void getItemById_shouldReturnItemDetailsForOtherUserWithoutBookings() {
        ItemDetailsDto itemDetails = itemService.getItemById(itemDto.getId(), otherUser.getId());

        assertThat(itemDetails).isNotNull();
        assertThat(itemDetails.getLastBooking()).isNull();
        assertThat(itemDetails.getComments()).isEmpty();
    }

    @Test
    void getAllItemsByOwner_shouldReturnAllItems() {
        List<ItemOwnerViewDto> items = itemService.getAllItemsByOwner(owner.getId());
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void searchItems_whenTextMatches_shouldReturnItem() {
        List<ItemSimpleDto> foundItems = itemService.searchItems("дрель");
        assertThat(foundItems).hasSize(1);
        assertThat(foundItems.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void searchItems_whenTextNotMatches_shouldReturnEmptyList() {
        List<ItemSimpleDto> foundItems = itemService.searchItems("молоток");
        assertThat(foundItems).isEmpty();
    }

    @Test
    void addComment_whenUserBookedItem_shouldAddComment() {
        BookingRequestDto bookingRequest = new BookingRequestDto(itemDto.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        BookingResponseDto booking = bookingService.addNewBooking(otherUser.getId(), bookingRequest);
        bookingService.updateBooking(booking.getId(), owner.getId(), true);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Комментарий к вещи");
        CommentDto savedComment = itemService.addComment(itemDto.getId(), otherUser.getId(), commentDto);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getText()).isEqualTo("Комментарий к вещи");
        assertThat(savedComment.getAuthorName()).isEqualTo(otherUser.getName());
    }

    @Test
    void addComment_whenUserDidNotBookItem_shouldThrowBadRequestException() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Комментарий к вещи");

        assertThatThrownBy(() -> itemService.addComment(itemDto.getId(), otherUser.getId(), commentDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("не брал вещь с ID");
    }

    @Test
    void addItem_whenItemDtoHasNullFields_shouldThrowConstraintViolationException() {
        ItemOwnerViewDto newItemDto = new ItemOwnerViewDto();
        newItemDto.setName(null);
        newItemDto.setDescription("Описание");
        newItemDto.setAvailable(true);

        assertThatThrownBy(() -> itemService.addItem(owner.getId(), newItemDto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Название предмета не должно быть пустым");
    }

    @Test
    void updateItem_whenUserDoesNotExist_shouldThrowNotFoundException() {
        ItemOwnerViewDto updateDto = new ItemOwnerViewDto();
        updateDto.setName("Обновленная дрель");

        assertThatThrownBy(() -> itemService.updateItem(999L, itemDto.getId(), updateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id 999 не найден");
    }

    @Test
    void updateItem_whenItemDoesNotExist_shouldThrowNotFoundException() {
        ItemOwnerViewDto updateDto = new ItemOwnerViewDto();
        updateDto.setName("Обновленная дрель");

        assertThatThrownBy(() -> itemService.updateItem(owner.getId(), 999L, updateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь с id 999 не найдена.");
    }

    @Test
    void getItemById_whenUserDoesNotExist_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> itemService.getItemById(itemDto.getId(), 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id 999 не найден");
    }

    @Test
    void getItemById_whenItemDoesNotExist_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> itemService.getItemById(999L, owner.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь с id 999 не найдена.");
    }

    @Test
    void getAllItemsByOwner_whenUserDoesNotExist_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> itemService.getAllItemsByOwner(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id 999 не найден");
    }

    @Test
    void searchItems_whenTextIsBlank_shouldReturnEmptyList() {
        List<ItemSimpleDto> foundItems = itemService.searchItems("   ");
        assertThat(foundItems).isEmpty();
    }

    @Test
    void addComment_whenUserDoesNotExist_shouldThrowNotFoundException() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Тестовый комментарий");

        assertThatThrownBy(() -> itemService.addComment(itemDto.getId(), 999L, commentDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id 999 не найден");
    }

    @Test
    void addComment_whenItemDoesNotExist_shouldThrowNotFoundException() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Тестовый комментарий");

        assertThatThrownBy(() -> itemService.addComment(999L, otherUser.getId(), commentDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь с id 999 не найдена");
    }
}