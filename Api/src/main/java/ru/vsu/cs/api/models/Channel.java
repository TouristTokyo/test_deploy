package ru.vsu.cs.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Entity
@Table(name = "channels")
@Data
@NoArgsConstructor
@Schema(description = "Информация о канале")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "creator", referencedColumnName = "id")
    private User creator;

    @Column(name = "name")
    private String name;

    public Channel(User creator, String name) {
        this.creator = creator;
        this.name = name;
    }
}
