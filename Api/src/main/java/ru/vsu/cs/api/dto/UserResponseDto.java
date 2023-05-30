package ru.vsu.cs.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.vsu.cs.api.dto.supporting.ChannelSupportingDto;
import ru.vsu.cs.api.dto.supporting.ChatSupportingDto;
import ru.vsu.cs.api.dto.supporting.SavedMessageSupportingDto;

import java.math.BigInteger;
import java.util.List;

@Data
@Schema(description = "Полная информация о пользователе")
public class UserResponseDto {
    private BigInteger id;
    private String name;
    private String email;
    private String password;
    private byte[] image;
    private List<ChatSupportingDto> chats;
    private List<ChannelSupportingDto> channels;
    private List<SavedMessageSupportingDto> savedMessages;

}
