package ru.vsu.cs.api.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.vsu.cs.api.dto.supporting.ChatSupportingDto;
import ru.vsu.cs.api.dto.supporting.UserSupportingDto;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@Schema(description = "Информация о сообщении в чате, которое отправил пользователь ")
public class ChatMessageDto {
    private BigInteger id;
    private ChatSupportingDto chat;
    private String data;
    private LocalDateTime date;
    private UserSupportingDto sender;
}
