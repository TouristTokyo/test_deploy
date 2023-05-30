package ru.vsu.cs.api.dto.supporting;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Дополнительная информация об участнике")
public class MemberSupportingDto {
    private UserSupportingDto user;
    private RoleSupportingDto role;
}
