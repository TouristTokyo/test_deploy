package ru.vsu.cs.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.api.models.Channel;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, BigInteger> {
    Optional<Channel> findByName(String name);
}
