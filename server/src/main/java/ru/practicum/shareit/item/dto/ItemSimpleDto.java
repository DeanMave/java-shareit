package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemSimpleDto {
    private Long id;
    @NotBlank(message = "Название предмета не должно быть пустым")
    private String name;
    @NotBlank(message = "Описание предмета не должно быть пустым")
    private String description;
    @NotNull(message = "Статус доступности предмета должен быть указан")
    private Boolean available;
}
