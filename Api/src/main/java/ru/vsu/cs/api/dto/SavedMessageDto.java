package ru.vsu.cs.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigInteger;

@Data
@Schema(description = "Информация, которая необходима для сохранения сообщения")
public class SavedMessageDto {
    @NotNull
    private String username;
    @NotNull
    private BigInteger messageId;
}
