package ru.vsu.cs.api.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.cs.api.models.User;
import ru.vsu.cs.api.repositories.UserRepository;
import ru.vsu.cs.api.utils.exceptions.UserException;

import java.math.BigInteger;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.warn("Not found user with email: " + email);
            throw new UserException("Не существует пользователя с такой почтой: " + email);
        }
        return user;
    }

    public User login(String email, String password) {
        User user = getUserByEmail(email);
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else {
            log.warn("Incorrect password: " + password);
            throw new UserException("Неверный пароль");
        }
    }

    public User getUserByName(String name) {
        User user = userRepository.findByName(name).orElse(null);
        if (user == null) {
            log.warn("Не существует пользователя с таким именем: " + name);
            throw new UserException("Not found user with name: " + name);
        }
        return user;
    }

    public User getById(BigInteger id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            log.warn("Not found user with id: " + id);
            throw new UserException("Не существует пользователя с таким id: " + id);
        }
        return user;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void save(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Exist user with email: " + user.getEmail());
            throw new UserException("Пользователь с такой почтой уже существует: " + user.getEmail());
        }
        if (userRepository.findByName(user.getName()).isPresent()) {
            log.warn("Exist user with nickname: " + user.getName());
            throw new UserException("Пользователь с таким именем уже существует: " + user.getName());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        log.info("User (" + user.getName() + ", " + user.getEmail() + ") registered successfully");
    }

    @Transactional
    public void updateImage(BigInteger id, byte[] array) {
        User user = getById(id);
        user.setImage(array);
        userRepository.save(user);
    }


    @Transactional
    public void updateEmail(BigInteger id, String email) {
        User foundUserByEmail = userRepository.findByEmail(email).orElse(null);
        User user = getById(id);

        if (foundUserByEmail != null && !foundUserByEmail.getId().equals(id)) {
            log.warn("Exist user with email: " + email);
            throw new UserException("Пользователь с такой почтой уже существует: " + email);
        }

        user.setEmail(email);

        userRepository.save(user);
    }

    @Transactional
    public void updateName(BigInteger id, String name) {
        User foundUserByName = userRepository.findByName(name).orElse(null);
        User user = getById(id);

        if (foundUserByName != null && !foundUserByName.getId().equals(id)) {
            log.warn("Exist user with nickname: " + name);
            throw new UserException("Пользователь с таким именем уже существует:: " + name);
        }

        user.setName(name);

        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(BigInteger id, String lastPassword, String newPassword) {
        User user = getById(id);
        if (lastPassword !=null && !passwordEncoder.matches(lastPassword, user.getPassword())) {
            log.warn("Incorrect current password: " + lastPassword);
            throw new UserException("Неверный текущий пароль: " + lastPassword);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void delete(BigInteger id) {
        userRepository.deleteById(id);
    }
}
