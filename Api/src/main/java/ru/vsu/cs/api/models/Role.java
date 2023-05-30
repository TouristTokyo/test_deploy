package ru.vsu.cs.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@Schema(description = "Информация о роли")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;
    @Column(name = "name")
    private String name;
    @Column(name = "is_admin")
    private Boolean isAdmin;

    @Column(name = "is_creator")
    private Boolean isCreator;

    public Role(String name, Boolean isAdmin, Boolean isCreator) {
        this.name = name;
        this.isAdmin = isAdmin;
        this.isCreator = isCreator;
    }
}
