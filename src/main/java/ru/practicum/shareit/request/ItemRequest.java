package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRequest that = (ItemRequest) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(description, that.description) &&
               Objects.equals(requestor, that.requestor) &&
               Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, requestor, created);
    }
}