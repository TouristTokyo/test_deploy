package ru.vsu.cs.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Информация, которая необходима для создания роли")
public class RoleCreationDto {
    @NotNull
    private String name;
    @NotNull
    private Boolean isAdmin;
    @NotNull
    private String username;
    @NotNull
    private String channelName;
}
