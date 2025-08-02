package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.enums.StatusBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, componentModel = "spring",
        uses = {ItemMapper.class, UserMapper.class},
        imports = {StatusBooking.class})
public interface BookingMapper {

    BookingResponseDto toBookingResponseDto(Booking booking);

    @Mapping(target = "status", expression = "java(StatusBooking.WAITING)")
    @Mapping(target = "id", ignore = true)
    Booking toBooking(BookingRequestDto bookingRequestDto, Item item, User booker);

    @Mapping(source = "booking.booker.id", target = "bookerId")
    BookingItemDto toBookingItemDto(Booking booking);


}
