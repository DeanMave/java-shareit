package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addNewRequest(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody ItemRequestDtoIn itemRequestDtoIn) {
        return itemRequestClient.addNewRequest(userId, itemRequestDtoIn);
    }

    @GetMapping()
    public ResponseEntity<Object> getUserRequests(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Positive @PathVariable Long requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
