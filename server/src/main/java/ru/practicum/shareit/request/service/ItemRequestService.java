package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoOut addNewRequest(Long userId, ItemRequestDtoIn itemRequestDtoIn);

    List<ItemRequestDtoOut> getUserRequests(Long userId);

    List<ItemRequestDtoOut> getAllRequests(Long requestorId,Integer from, Integer size);

    ItemRequestDtoOut getRequestById(Long userId, Long requestId);
}
