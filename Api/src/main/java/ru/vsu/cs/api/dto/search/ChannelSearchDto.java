package ru.vsu.cs.api.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigInteger;

@Data
@Schema(description = "Информация о найденном канале")
public class ChannelSearchDto {
    private BigInteger id;
    private String name;

}
