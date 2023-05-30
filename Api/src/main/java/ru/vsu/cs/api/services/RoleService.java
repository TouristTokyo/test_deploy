package ru.vsu.cs.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.cs.api.models.Role;
import ru.vsu.cs.api.repositories.RoleRepository;

import java.math.BigInteger;

@Service
@Transactional(readOnly = true)
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    public Role save(Role role) {
        return roleRepository.saveAndFlush(role);
    }

    @Transactional
    public void delete(BigInteger id) {
        roleRepository.deleteById(id);
    }
}
