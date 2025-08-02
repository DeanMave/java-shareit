package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemOwnerViewDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRequestServiceImpTest {

    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    private UserDto user1;
    private UserDto user2;
    private UserDto user3;

    @BeforeEach
    void setUp() {
        user1 = userService.addNewUser(new UserDto(null, "User1", "user1@mail.ru"));
        user2 = userService.addNewUser(new UserDto(null, "User2", "user2@mail.ru"));
        user3 = userService.addNewUser(new UserDto(null, "User3", "user3@mail.ru"));
    }

    @Test
    void addNewRequest_shouldSaveAndReturnRequest() {
        ItemRequestDtoIn requestDtoIn = new ItemRequestDtoIn("Нужна дрель");
        ItemRequestDtoOut savedRequest = itemRequestService.addNewRequest(user1.getId(), requestDtoIn);

        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getDescription()).isEqualTo("Нужна дрель");
        assertThat(savedRequest.getRequestor().getId()).isEqualTo(user1.getId());
    }

    @Test
    void addNewRequest_whenUserNotFound_shouldThrowNotFoundException() {
        ItemRequestDtoIn requestDtoIn = new ItemRequestDtoIn("Нужна дрель");

        assertThatThrownBy(() -> itemRequestService.addNewRequest(999L, requestDtoIn))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователя с id 999 не существует");
    }

    @Test
    void getUserRequests_shouldReturnRequestsWithItems() {
        ItemRequestDtoIn requestDtoIn = new ItemRequestDtoIn("Нужна дрель");
        ItemRequestDtoOut request = itemRequestService.addNewRequest(user1.getId(), requestDtoIn);

        ItemOwnerViewDto itemDto = new ItemOwnerViewDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Простая дрель");
        itemDto.setAvailable(true);
        itemDto.setRequestId(request.getId());
        itemService.addItem(user2.getId(), itemDto);

        List<ItemRequestDtoOut> userRequests = itemRequestService.getUserRequests(user1.getId());

        assertThat(userRequests).hasSize(1);
        assertThat(userRequests.get(0).getDescription()).isEqualTo("Нужна дрель");
        assertThat(userRequests.get(0).getItems()).hasSize(1);
        assertThat(userRequests.get(0).getItems().get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void getUserRequests_whenNoRequests_shouldReturnEmptyList() {
        List<ItemRequestDtoOut> userRequests = itemRequestService.getUserRequests(user1.getId());
        assertThat(userRequests).isEmpty();
    }

    @Test
    void getAllRequests_shouldReturnRequestsOfOtherUsers() {
        itemRequestService.addNewRequest(user1.getId(), new ItemRequestDtoIn("Нужна дрель"));
        itemRequestService.addNewRequest(user2.getId(), new ItemRequestDtoIn("Нужен молоток"));

        List<ItemRequestDtoOut> allRequests = itemRequestService.getAllRequests(user1.getId(), 0, 10);
        assertThat(allRequests).hasSize(1);
        assertThat(allRequests.get(0).getDescription()).isEqualTo("Нужен молоток");
        assertThat(allRequests.get(0).getRequestor().getId()).isEqualTo(user2.getId());
    }

    @Test
    void getAllRequests_withPagination_shouldReturnCorrectSize() {
        itemRequestService.addNewRequest(user2.getId(), new ItemRequestDtoIn("Нужен молоток"));
        itemRequestService.addNewRequest(user2.getId(), new ItemRequestDtoIn("Нужен винт"));

        List<ItemRequestDtoOut> allRequests = itemRequestService.getAllRequests(user1.getId(), 0, 1);
        assertThat(allRequests).hasSize(1);
    }

    @Test
    void getRequestById_shouldReturnRequestWithItems() {
        ItemRequestDtoIn requestDtoIn = new ItemRequestDtoIn("Нужна дрель");
        ItemRequestDtoOut request = itemRequestService.addNewRequest(user1.getId(), requestDtoIn);

        ItemOwnerViewDto itemDto = new ItemOwnerViewDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Простая дрель");
        itemDto.setAvailable(true);
        itemDto.setRequestId(request.getId());
        itemService.addItem(user2.getId(), itemDto);

        ItemRequestDtoOut foundRequest = itemRequestService.getRequestById(user1.getId(), request.getId());
        assertThat(foundRequest).isNotNull();
        assertThat(foundRequest.getDescription()).isEqualTo("Нужна дрель");
        assertThat(foundRequest.getItems()).hasSize(1);
    }

    @Test
    void getRequestById_whenRequestNotFound_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> itemRequestService.getRequestById(user1.getId(), 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос с id 999 не найден.");
    }


    @Test
    void getUserRequests_whenNoItemsForRequests_shouldReturnRequestsWithEmptyItemList() {
        itemRequestService.addNewRequest(user1.getId(), new ItemRequestDtoIn("Нужна дрель"));
        itemRequestService.addNewRequest(user1.getId(), new ItemRequestDtoIn("Нужен молоток"));

        List<ItemRequestDtoOut> userRequests = itemRequestService.getUserRequests(user1.getId());

        assertThat(userRequests).hasSize(2);
        assertThat(userRequests.get(0).getItems()).isEmpty();
        assertThat(userRequests.get(1).getItems()).isEmpty();
    }

    @Test
    void getAllRequests_whenUserHasNoRequests_shouldReturnEmptyList() {
        List<ItemRequestDtoOut> allRequests = itemRequestService.getAllRequests(user3.getId(), 0, 10);
        assertThat(allRequests).isEmpty();
    }

    @Test
    void getAllRequests_withFromExceedingTotalRequests_shouldReturnEmptyList() {
        itemRequestService.addNewRequest(user2.getId(), new ItemRequestDtoIn("Нужен молоток"));
        itemRequestService.addNewRequest(user2.getId(), new ItemRequestDtoIn("Нужен винт"));

        List<ItemRequestDtoOut> allRequests = itemRequestService.getAllRequests(user1.getId(), 5, 10);
        assertThat(allRequests).isEmpty();
    }

    @Test
    void getRequestById_whenUserNotFound_shouldThrowNotFoundException() {
        itemRequestService.addNewRequest(user1.getId(), new ItemRequestDtoIn("Нужна дрель"));

        assertThatThrownBy(() -> itemRequestService.getRequestById(999L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователя с id 999 не существует");
    }

    @Test
    void getAllRequests_whenUserNotFound_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> itemRequestService.getAllRequests(999L, 0, 10))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователя с id 999 не существует");
    }
}