package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, componentModel = "spring")
public interface ItemMapper {

    @Mapping(source = "item.id", target = "id")
    ItemOwnerViewDto toItemDto(Item item, BookingItemDto lastBooking, BookingItemDto nextBooking);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "itemOwnerViewDto.name", target = "name")
    @Mapping(source = "itemOwnerViewDto.description", target = "description")
    @Mapping(source = "itemRequest", target = "request")
    Item toItem(ItemOwnerViewDto itemOwnerViewDto, User owner, ItemRequest itemRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    void updateFromDto(@MappingTarget Item item, ItemOwnerViewDto itemOwnerViewDto);


    ItemShortDto toItemShortDto(Item item);

    @Mapping(source = "item.id", target = "id")
    ItemDetailsDto toItemBookingDto(Item item,
                                    BookingItemDto lastBooking,
                                    BookingItemDto nextBooking,
                                    List<CommentDto> comments);

    ItemSimpleDto toItemResponseDto(Item item);

    @Mapping(source = "item.owner.id",target = "ownerId")
    @Mapping(source = "item.request.id", target = "requestId")
    ItemRequestResponseDto toItemRequestResponseDto(Item item);
}
