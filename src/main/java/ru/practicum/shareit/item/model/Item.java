package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private Long id;
    @NotBlank(message = "Название предмета не должно быть пустым")
    private String name;
    @NotBlank(message = "Описание предмета не должно быть пустым")
    private String description;
    private User owner;
    @NotNull(message = "Статус доступности предмета должен быть указан")
    private Boolean available;
    private ItemRequest request;
}
