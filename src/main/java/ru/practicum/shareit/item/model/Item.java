package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

@Entity
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    @NotBlank(message = "Название предмета не должно быть пустым")
    private String name;
    @Column(name = "description", nullable = false)
    @NotBlank(message = "Описание предмета не должно быть пустым")
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @Column(name = "is_available", nullable = false)
    @NotNull(message = "Статус доступности предмета должен быть указан")
    private Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = true)
    private ItemRequest request;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) && Objects.equals(name, item.name) && Objects.equals(description, item.description) && Objects.equals(owner, item.owner) && Objects.equals(available, item.available) && Objects.equals(request, item.request);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, owner, available, request);
    }
}
