package ru.vsu.cs.api.dto.supporting;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigInteger;

@Data
@Schema(description = "Дополнительная информация о сохранённом сообщении")
public class SavedMessageSupportingDto {
    private BigInteger id;
    private UserSupportingDto sender;
    private ChatSupportingDto chat;
    private ChannelSupportingDto channel;
    private String data;
}
