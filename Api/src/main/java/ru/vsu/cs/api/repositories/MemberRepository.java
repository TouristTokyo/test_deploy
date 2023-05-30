package ru.vsu.cs.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.api.models.Channel;
import ru.vsu.cs.api.models.Member;
import ru.vsu.cs.api.models.User;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, BigInteger> {
    List<Member> findByUser(User user);
    List<Member> findByChannel(Channel channel);
    Optional<Member> findByUserAndChannel(User user, Channel channel);
}
