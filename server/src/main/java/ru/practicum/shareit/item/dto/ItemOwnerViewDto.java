package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingItemDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemOwnerViewDto {
    private Long id;
    @NotBlank(message = "Название предмета не должно быть пустым")
    private String name;
    @NotBlank(message = "Описание предмета не должно быть пустым")
    private String description;
    @NotNull(message = "Статус доступности предмета должен быть указан")
    private Boolean available;
    private Long requestId;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
}
