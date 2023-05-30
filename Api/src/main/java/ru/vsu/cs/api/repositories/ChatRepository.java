package ru.vsu.cs.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.api.models.Chat;
import ru.vsu.cs.api.models.User;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, BigInteger> {

    @Query(value = "select * from chats where (first_user = :firstUser and second_user = :secondUser) or " +
                "(first_user = :secondUser and second_user = :firstUser)",
            nativeQuery = true)
    Optional<Chat> findByUsernames(@Param("firstUser") BigInteger currentUsername,
                                   @Param("secondUser") BigInteger otherUsername);

    List<Chat> findByUserFirstOrUserSecond(User sender, User recipient);
}
