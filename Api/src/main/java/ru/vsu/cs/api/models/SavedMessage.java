package ru.vsu.cs.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Entity
@Table(name = "saved_messages")
@Data
@NoArgsConstructor
@Schema(description = "Информация о сохранённом сообщении")
public class SavedMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @OneToOne
    @JoinColumn(name = "message_id", referencedColumnName = "id")
    private Message message;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public SavedMessage(Message message, User user) {
        this.message = message;
        this.user = user;
    }
}
