package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemRequestResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImp implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;


    @Override
    @Transactional
    public ItemRequestDtoOut addNewRequest(Long userId, ItemRequestDtoIn itemRequestDtoIn) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id " + userId + " не существует"));
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDtoIn, user);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest savedItemRequest = repository.save(itemRequest);
        return itemRequestMapper.toItemRequestDtoOut(savedItemRequest, new ArrayList<>());
    }

    @Override
    public List<ItemRequestDtoOut> getUserRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id " + userId + " не существует"));
        List<ItemRequest> requests = repository.findByRequestor_IdOrderByCreatedDesc(userId);
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toSet());
        List<Item> allItemsForRequests = itemRepository.findByRequest_IdIn(requestIds);
        Map<Long, List<ItemRequestResponseDto>> itemsByRequestId = allItemsForRequests.stream()
                .map(itemMapper::toItemRequestResponseDto)
                .collect(Collectors.groupingBy(ItemRequestResponseDto::getRequestId));
        List<ItemRequestDtoOut> result = requests.stream()
                .map(request -> {
                    List<ItemRequestResponseDto> itemsForCurrentRequest = itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList());
                    return itemRequestMapper.toItemRequestDtoOut(request, itemsForCurrentRequest);
                })
                .collect(Collectors.toList());

        log.info("Получены запросы пользователя id={}. Найдено {} запросов.", userId, result.size());
        return result;
    }

    @Override
    public List<ItemRequestDtoOut> getAllRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id " + userId + " не существует"));
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        Pageable pageable = PageRequest.of(from / size, size, sort);

        List<ItemRequest> requests = repository.findByRequestor_IdNotOrderByCreatedDesc(userId, pageable);

        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toSet());
        List<Item> allItemsForRequests = itemRepository.findByRequest_IdIn(requestIds);
        Map<Long, List<ItemRequestResponseDto>> itemsByRequestId = allItemsForRequests.stream()
                .map(itemMapper::toItemRequestResponseDto)
                .collect(Collectors.groupingBy(ItemRequestResponseDto::getRequestId));
        List<ItemRequestDtoOut> result = requests.stream()
                .map(request -> {
                    List<ItemRequestResponseDto> itemsForCurrentRequest = itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList());
                    return itemRequestMapper.toItemRequestDtoOut(request, itemsForCurrentRequest);
                })
                .collect(Collectors.toList());
        log.info("Получены все запросы (кроме пользователя id={}). Найдено {} запросов.", userId, result.size());
        return result;
    }

    @Override
    public ItemRequestDtoOut getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id " + userId + " не существует"));
        ItemRequest itemRequest = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден."));
        List<ItemRequestResponseDto> items = itemRepository.findByRequest_Id(requestId).stream()
                .map(itemMapper::toItemRequestResponseDto)
                .collect(Collectors.toList());
        log.info("Получен запрос id={} пользователем id={}", requestId, userId);
        return itemRequestMapper.toItemRequestDtoOut(itemRequest, items);
    }
}
