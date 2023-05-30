package ru.vsu.cs.api.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Информация о найденном чате")
public class ChatSearchDto {
    private String name;
    private byte[] image;
}
