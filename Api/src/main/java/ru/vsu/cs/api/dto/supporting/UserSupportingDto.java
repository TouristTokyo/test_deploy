package ru.vsu.cs.api.dto.supporting;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigInteger;

@Data
@Schema(description = "Дополнительная информация о пользователе")
public class UserSupportingDto {
    private BigInteger id;
    private String name;
    private byte[] image;
}
