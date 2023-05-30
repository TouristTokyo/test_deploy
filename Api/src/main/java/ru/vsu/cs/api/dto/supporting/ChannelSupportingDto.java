package ru.vsu.cs.api.dto.supporting;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigInteger;

@Data
@Schema(description = "Дополнительная информация о канале")
public class ChannelSupportingDto {
    private BigInteger id;
    private String name;
}
