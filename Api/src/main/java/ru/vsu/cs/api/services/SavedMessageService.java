package ru.vsu.cs.api.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.cs.api.models.Message;
import ru.vsu.cs.api.models.SavedMessage;
import ru.vsu.cs.api.models.User;
import ru.vsu.cs.api.repositories.SavedMessageRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class SavedMessageService {
    private final SavedMessageRepository savedMessageRepository;

    @Autowired
    public SavedMessageService(SavedMessageRepository savedMessageRepository) {
        this.savedMessageRepository = savedMessageRepository;
    }

    public List<SavedMessage> getSavedMessageByUser(User user) {
        return savedMessageRepository.findByUser(user);
    }

    @Transactional
    public void save(SavedMessage savedMessage) {
        log.info(savedMessage.getUser().getName() + " saved the message");
        savedMessageRepository.save(savedMessage);
    }

    @Transactional
    public void deleteAllByUser(User user) {
        log.info(user.getName() + " deleted all saved messages");
        savedMessageRepository.deleteAllByUser(user);
    }

    @Transactional
    public void delete(Message message, User user) {
        log.info(user.getName() + " deleted saved message");
        savedMessageRepository.deleteByMessageAndUser(message, user);
    }
}
