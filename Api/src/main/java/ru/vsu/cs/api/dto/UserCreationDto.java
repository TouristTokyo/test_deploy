package ru.vsu.cs.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Информация, которая необходима для регистрации пользователя")
public class UserCreationDto {
    @NotNull
    private String name;
    @NotNull
    @Email(message = "Некоректный email")
    private String email;
    @NotNull
    private String password;
}
