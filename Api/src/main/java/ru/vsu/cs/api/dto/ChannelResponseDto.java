package ru.vsu.cs.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.vsu.cs.api.dto.message.ChannelMessageDto;
import ru.vsu.cs.api.dto.supporting.ChannelSupportingDto;
import ru.vsu.cs.api.dto.supporting.MemberSupportingDto;
import ru.vsu.cs.api.dto.supporting.UserSupportingDto;

import java.util.List;

@Data
@Schema(description = "Полная информация о канале")
public class ChannelResponseDto {
    private ChannelSupportingDto channel;
    private UserSupportingDto creator;
    private List<MemberSupportingDto> members;
    private List<ChannelMessageDto> messages;
}
