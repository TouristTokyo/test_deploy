package ru.vsu.cs.api.utils.mapper;

import ru.vsu.cs.api.dto.*;
import ru.vsu.cs.api.dto.message.ChannelMessageDto;
import ru.vsu.cs.api.dto.message.ChatMessageDto;
import ru.vsu.cs.api.dto.search.ChannelSearchDto;
import ru.vsu.cs.api.dto.search.ChatSearchDto;
import ru.vsu.cs.api.dto.supporting.*;
import ru.vsu.cs.api.models.*;

import java.util.List;

public class Mapper {
    public static UserResponseDto convertToUserResponseDto(User user, List<Chat> chats, List<Message> savedMessages,
                                                           List<Channel> channels) {
        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setId(user.getId());
        userResponseDto.setName(user.getName());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setPassword(user.getPassword());
        userResponseDto.setImage(user.getImage());
        userResponseDto.setChats(chats.stream().map(Mapper::convertToChatSupportingDto).toList());
        userResponseDto.setSavedMessages(savedMessages.stream().map(Mapper::convertToSavedMessageSupportingDto).toList());
        userResponseDto.setChannels(channels.stream().map(Mapper::convertToChannelSupportingDto).toList());

        return userResponseDto;
    }

    public static User convertToUser(UserCreationDto userCreationDto) {
        User user = new User();

        user.setName(userCreationDto.getName());
        user.setEmail(userCreationDto.getEmail());
        user.setPassword(userCreationDto.getPassword());

        return user;
    }

    public static Chat convertToChat(User firstUser, User secondUser) {
        Chat chat = new Chat();

        chat.setUserFirst(firstUser);
        chat.setUserSecond(secondUser);

        return chat;
    }

    public static ChatMessageDto convertToChatMessageDto(Message message) {
        ChatMessageDto chatMessageDto = new ChatMessageDto();

        chatMessageDto.setId(message.getId());
        chatMessageDto.setSender(convertToUserSupportingDto(message.getSender()));
        chatMessageDto.setChat(convertToChatSupportingDto(message.getChat()));
        chatMessageDto.setData(message.getData());
        chatMessageDto.setDate(message.getDate());

        return chatMessageDto;
    }

    public static ChannelResponseDto convertToChannelResponseDto(Channel channel, List<Member> members,
                                                                 List<Message> messages) {
        ChannelResponseDto channelResponseDto = new ChannelResponseDto();

        channelResponseDto.setChannel(convertToChannelSupportingDto(channel));
        channelResponseDto.setCreator(convertToUserSupportingDto(channel.getCreator()));
        channelResponseDto.setMembers(members.stream().map(Mapper::convertToMemberSupportingDto).toList());
        channelResponseDto.setMessages(messages.stream().map(Mapper::convertToChannelMessageDto).toList());

        return channelResponseDto;
    }

    public static ChannelMessageDto convertToChannelMessageDto(Message message) {
        ChannelMessageDto channelMessageDto = new ChannelMessageDto();

        channelMessageDto.setId(message.getId());
        channelMessageDto.setChannelName(message.getChannel().getName());
        channelMessageDto.setData(message.getData());
        channelMessageDto.setSender(convertToUserSupportingDto(message.getSender()));
        channelMessageDto.setDate(message.getDate());

        return channelMessageDto;
    }

    public static ChannelSearchDto convertToChannelDto(Channel channel) {
        ChannelSearchDto channelSearchDto = new ChannelSearchDto();

        channelSearchDto.setId(channel.getId());
        channelSearchDto.setName(channel.getName());

        return channelSearchDto;
    }

    public static ChatSearchDto convertToChatSearchDto(User user) {
        ChatSearchDto chatSearchDto = new ChatSearchDto();

        chatSearchDto.setName(user.getName());
        chatSearchDto.setImage(user.getImage());

        return chatSearchDto;
    }


    private static ChatSupportingDto convertToChatSupportingDto(Chat chat) {
        ChatSupportingDto chatSupportingDto = new ChatSupportingDto();

        chatSupportingDto.setId(chat.getId());
        chatSupportingDto.setSender(convertToUserSupportingDto(chat.getUserFirst()));
        chatSupportingDto.setRecipient(convertToUserSupportingDto(chat.getUserSecond()));

        return chatSupportingDto;
    }

    private static UserSupportingDto convertToUserSupportingDto(User user) {
        UserSupportingDto userSupportingDto = new UserSupportingDto();

        userSupportingDto.setId(user.getId());
        userSupportingDto.setName(user.getName());
        userSupportingDto.setImage(user.getImage());

        return userSupportingDto;
    }

    private static ChannelSupportingDto convertToChannelSupportingDto(Channel channel) {
        ChannelSupportingDto channelSupportingDto = new ChannelSupportingDto();

        channelSupportingDto.setId(channel.getId());
        channelSupportingDto.setName(channel.getName());

        return channelSupportingDto;
    }

    private static RoleSupportingDto convertToRoleSupportingDto(Role role) {
        RoleSupportingDto roleSupportingDto = new RoleSupportingDto();

        roleSupportingDto.setName(role.getName());
        roleSupportingDto.setAdmin(role.getIsAdmin());
        roleSupportingDto.setCreator(role.getIsCreator());

        return roleSupportingDto;
    }

    private static MemberSupportingDto convertToMemberSupportingDto(Member member) {
        MemberSupportingDto memberSupportingDto = new MemberSupportingDto();

        memberSupportingDto.setUser(convertToUserSupportingDto(member.getUser()));
        memberSupportingDto.setRole(convertToRoleSupportingDto(member.getRole()));

        return memberSupportingDto;
    }

    private static SavedMessageSupportingDto convertToSavedMessageSupportingDto(Message message) {
        SavedMessageSupportingDto savedMessageSupportingDto = new SavedMessageSupportingDto();

        savedMessageSupportingDto.setId(message.getId());
        savedMessageSupportingDto.setSender(convertToUserSupportingDto(message.getSender()));
        if (message.getChannel() == null) {
            savedMessageSupportingDto.setChat(convertToChatSupportingDto(message.getChat()));
        } else {
            savedMessageSupportingDto.setChannel(convertToChannelSupportingDto(message.getChannel()));
        }
        savedMessageSupportingDto.setData(message.getData());

        return savedMessageSupportingDto;
    }
}
