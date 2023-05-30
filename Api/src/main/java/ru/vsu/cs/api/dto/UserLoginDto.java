package ru.vsu.cs.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Информация, которая необходима для входа в аккаунт")
public class UserLoginDto {
    @NotNull
    @Email
    private String email;
    @NotNull
    private String password;
}
