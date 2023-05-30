package ru.vsu.cs.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Entity
@Table(name = "chats")
@Data
@NoArgsConstructor
@Schema(description = "Информация о чате")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "first_user", referencedColumnName = "id")
    private User userFirst;

    @ManyToOne
    @JoinColumn(name = "second_user", referencedColumnName = "id")
    private User userSecond;

}
