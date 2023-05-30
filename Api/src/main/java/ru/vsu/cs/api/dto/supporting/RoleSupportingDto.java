package ru.vsu.cs.api.dto.supporting;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Дополнительная информация о роли")
public class RoleSupportingDto {
    private String name;
    private boolean isAdmin;
    private boolean isCreator;
}
