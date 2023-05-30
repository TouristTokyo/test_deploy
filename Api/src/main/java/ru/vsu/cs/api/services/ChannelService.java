package ru.vsu.cs.api.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.cs.api.models.Channel;
import ru.vsu.cs.api.repositories.ChannelRepository;
import ru.vsu.cs.api.utils.exceptions.ChannelException;
import ru.vsu.cs.api.utils.exceptions.UserException;

import java.math.BigInteger;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ChannelService {
    private final ChannelRepository channelRepository;

    @Autowired
    public ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public List<Channel> getAll() {
        return channelRepository.findAll();
    }

    @Transactional
    public Channel create(Channel channel) {
        if (channelRepository.findByName(channel.getName()).isPresent()) {
            log.warn("Exist channel with name: " + channel.getName());
            throw new ChannelException("Канал с таким именем уже существует: " + channel.getName());
        }
        log.info("Channel (" + channel.getName() + ") create successfully");
        return channelRepository.saveAndFlush(channel);
    }

    @Transactional
    public void delete(BigInteger id) {
        channelRepository.deleteById(id);
    }

    public Channel getChannelById(BigInteger id) {
        Channel channel = channelRepository.findById(id).orElse(null);
        if (channel == null) {
            log.warn("Not found channel with id: " + id);
            throw new ChannelException("Канал с таким id не был найден: " + id);
        }
        return channel;
    }

    public Channel getChannelByName(String name) {
        Channel channel = channelRepository.findByName(name).orElse(null);
        if (channel == null) {
            log.warn("Not found channel with name: " + name);
            throw new ChannelException("Канал с таким именем не был найден: " + name);
        }
        return channel;
    }

    @Transactional
    public void updateName(BigInteger id, String name) {
        Channel foundChannel = channelRepository.findByName(name).orElse(null);
        Channel channel = getChannelById(id);

        if (foundChannel != null && !foundChannel.getId().equals(id)) {
            log.warn("Exist channel with name: " + name);
            throw new UserException("Канал с таким именем уже существует: " + name);
        }

        log.info("Updated channel name: " + channel.getName() + " --> " + name);

        channel.setName(name);
        channelRepository.save(channel);
    }

}
