package ru.vsu.cs.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.api.models.Channel;
import ru.vsu.cs.api.models.Chat;
import ru.vsu.cs.api.models.Message;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, BigInteger> {
    List<Message> findByChat(Chat chat);
    List<Message> findByChannel(Channel channel);
}
