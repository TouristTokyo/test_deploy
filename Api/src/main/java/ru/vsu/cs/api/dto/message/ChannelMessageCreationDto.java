package ru.vsu.cs.api.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Информация, необходимая для создания сообщения в канале")
public class ChannelMessageCreationDto {
    @NotNull
    private String currentUsername;
    @NotNull
    private String message;
    @NotNull
    private String channelName;
}
