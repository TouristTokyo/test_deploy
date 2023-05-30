package ru.vsu.cs.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.api.models.Role;

import java.math.BigInteger;

@Repository
public interface RoleRepository extends JpaRepository<Role, BigInteger> {
}
