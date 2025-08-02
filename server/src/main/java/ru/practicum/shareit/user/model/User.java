package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Некорректная электронная почта")
    @NotBlank(message = "Email не может быть пустым")
    private String email;
}
