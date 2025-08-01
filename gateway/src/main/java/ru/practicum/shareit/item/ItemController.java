package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemOwnerViewDto;

import java.util.Collections;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody ItemOwnerViewDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(itemId, userId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Positive @PathVariable Long itemId,
                                             @RequestBody ItemOwnerViewDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Positive @PathVariable Long itemId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getAllItemsByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        if (text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return itemClient.searchItems(text);
    }
}
