package ru.vsu.cs.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.api.models.User;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, BigInteger> {
   Optional<User> findByEmail(String email);
   Optional<User> findByName(String name);
}
