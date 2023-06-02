package ru.vsu.cs.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.vsu.cs.api.dto.*;
import ru.vsu.cs.api.dto.message.ChannelMessageCreationDto;
import ru.vsu.cs.api.dto.message.ChatMessageCreationDto;
import ru.vsu.cs.api.models.*;
import ru.vsu.cs.api.services.*;
import ru.vsu.cs.api.utils.exceptions.ChannelException;
import ru.vsu.cs.api.utils.exceptions.UserException;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MessengerApiApplicationTests {
    @Value("${security_username}")
    private String SECURITY_USERNAME;
    @Value("${security_password}")
    private String SECURITY_PASSWORD;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    @MockBean
    private final UserService userService;
    @MockBean
    private final ChatService chatService;
    @MockBean
    private final ChannelService channelService;
    @MockBean
    private final MemberService memberService;
    @MockBean
    private final MessageService messageService;
    @MockBean
    private final RoleService roleService;
    @MockBean
    private final SavedMessageService savedMessageService;

    @Autowired
    public MessengerApiApplicationTests(MockMvc mockMvc, ObjectMapper objectMapper, UserService userService, ChatService chatService, ChannelService channelService, MemberService memberService, MessageService messageService, RoleService roleService, SavedMessageService savedMessageService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.chatService = chatService;
        this.channelService = channelService;
        this.memberService = memberService;
        this.messageService = messageService;
        this.roleService = roleService;
        this.savedMessageService = savedMessageService;
    }

    @Test
    void testSuccessfulRegistrationUser() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        UserCreationDto userCreationDto = new UserCreationDto();
        userCreationDto.setName("Name");
        userCreationDto.setEmail("test@example.com");
        userCreationDto.setPassword("secret");


        doNothing().when(userService).save(any(User.class));


        ResultActions response = mockMvc.perform(post("/api/register")
                .header("Authorization", "Basic " + base64Credentials)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreationDto)));

        response.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testIncorrectRegistrationUser() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        UserCreationDto userCreationDto = new UserCreationDto();
        userCreationDto.setName("Name");
        userCreationDto.setEmail("invalid@");
        userCreationDto.setPassword("secret");


        doNothing().when(userService).save(any(User.class));


        ResultActions response = mockMvc.perform(post("/api/register")
                .header("Authorization", "Basic " + base64Credentials)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreationDto)));

        response.andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void testSuccessfulLoginUser() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("test@example.com");
        userLoginDto.setPassword("secret");

        User user = new User();
        user.setId(BigInteger.ONE);
        user.setImage(null);
        user.setName("Name");
        user.setEmail("test@example.com");
        user.setPassword("secret");

        when(userService.login(userLoginDto.getEmail(), userLoginDto.getPassword())).thenReturn(user);


        ResultActions response = mockMvc.perform(post("/api/login")
                .header("Authorization", "Basic " + base64Credentials)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDto)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.password", is(user.getPassword())))
                .andExpect(jsonPath("$.image", is(nullValue())))
                .andExpect(jsonPath("$.channels", is(Collections.emptyList())))
                .andExpect(jsonPath("$.chats", is(Collections.emptyList())))
                .andExpect(jsonPath("$.savedMessages", is(Collections.emptyList())));
    }

    @Test
    void testIncorrectLoginUser() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("test@example.com");
        userLoginDto.setPassword(null);

        User user = new User();
        user.setId(BigInteger.ONE);
        user.setImage(null);
        user.setName("Name");
        user.setEmail("test@example.com");
        user.setPassword("secret");

        when(userService.login(userLoginDto.getEmail(), userLoginDto.getPassword())).thenReturn(user);


        ResultActions response = mockMvc.perform(post("/api/login")
                .header("Authorization", "Basic " + base64Credentials)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDto)));

        response.andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void testGetUserById() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        User user = new User();
        user.setId(BigInteger.ONE);
        user.setImage(null);
        user.setName("Name");
        user.setEmail("test@example.com");
        user.setPassword("secret");

        when(userService.getById(BigInteger.ONE)).thenReturn(user);

        ResultActions response = mockMvc.perform(get("/api/users/1")
                .header("Authorization", "Basic " + base64Credentials)
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.password", is(user.getPassword())))
                .andExpect(jsonPath("$.image", is(nullValue())))
                .andExpect(jsonPath("$.channels", is(Collections.emptyList())))
                .andExpect(jsonPath("$.chats", is(Collections.emptyList())))
                .andExpect(jsonPath("$.savedMessages", is(Collections.emptyList())));
    }

    @Test
    void testGetUserByEmail() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        User user = new User();
        user.setId(BigInteger.ONE);
        user.setImage(null);
        user.setName("Name");
        user.setEmail("test@example.com");
        user.setPassword("secret");

        when(userService.getUserByEmail("test@example.com")).thenReturn(user);

        ResultActions response = mockMvc.perform(get("/api/users/email")
                .header("Authorization", "Basic " + base64Credentials)
                .param("email", "test@example.com")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(user.getId().toString()));
    }

    @Test
    void testGetUserByEmailThrowError() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));


        when(userService.getUserByEmail("not_found@example.com"))
                .thenThrow(new UserException("Не существует пользователя с такой почтой: not_found@example.com"));

        ResultActions response = mockMvc.perform(get("/api/users/email")
                .header("Authorization", "Basic " + base64Credentials)
                .param("email", "not_found@example.com")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUsername() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        doNothing().when(userService).updateName(BigInteger.ONE, "new_name");

        ResultActions response = mockMvc.perform(put("/api/users/1/update/name")
                .header("Authorization", "Basic " + base64Credentials)
                .param("name", "new_name")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testUpdatePasswordWithLastPassword() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        doNothing().when(userService).updatePassword(BigInteger.ONE, "password", "new_password");

        ResultActions response = mockMvc.perform(put("/api/users/1/update/password")
                .header("Authorization", "Basic " + base64Credentials)
                .param("last_password", "password")
                .param("new_password", "new_password")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testUpdatePasswordNoLastPassword() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        doNothing().when(userService).updatePassword(BigInteger.ONE, null, "new_password");

        ResultActions response = mockMvc.perform(put("/api/users/1/update/password")
                .header("Authorization", "Basic " + base64Credentials)
                .param("new_password", "new_password")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateEmail() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        doNothing().when(userService).updateEmail(BigInteger.ONE, "new_email");

        ResultActions response = mockMvc.perform(put("/api/users/1/update/email")
                .header("Authorization", "Basic " + base64Credentials)
                .param("email", "new_email")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteImage() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        doNothing().when(userService).updateImage(BigInteger.ONE, null);

        ResultActions response = mockMvc.perform(delete("/api/users/1/delete_image")
                .header("Authorization", "Basic " + base64Credentials)
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testIsCorrectSendEmailRequest() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        ResultActions response = mockMvc.perform(get("/api/send_email?email=ff.mail.ru")
                .header("Authorization", "Basic " + base64Credentials)
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetChats() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        mockMvc.perform(get("/api/chats")
                        .header("Authorization", "Basic " + base64Credentials)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testAddMessageToChat() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        ChatMessageCreationDto chatMessageCreationDto = new ChatMessageCreationDto();
        chatMessageCreationDto.setCurrentUsername("curr_user");
        chatMessageCreationDto.setOtherUsername("other_user");
        chatMessageCreationDto.setMessage("Test");

        User currentUser = new User();
        currentUser.setId(BigInteger.ONE);
        currentUser.setName("curr_user");
        currentUser.setEmail("curr_user@example.com");
        currentUser.setPassword("secret");
        currentUser.setImage(null);

        User otherUser = new User();
        otherUser.setId(BigInteger.TWO);
        otherUser.setName("other_user");
        otherUser.setEmail("other_user@example.com");
        otherUser.setPassword("secret");
        otherUser.setImage(null);

        Chat chat = new Chat();
        chat.setId(BigInteger.ONE);
        chat.setUserFirst(currentUser);
        chat.setUserSecond(otherUser);

        Message message = new Message();
        message.setId(BigInteger.ONE);
        message.setChat(chat);
        message.setSender(currentUser);
        message.setData(chatMessageCreationDto.getMessage());
        message.setDate(LocalDateTime.now());

        when(userService.getUserByName(chatMessageCreationDto.getCurrentUsername())).thenReturn(currentUser);
        when(userService.getUserByName(chatMessageCreationDto.getOtherUsername())).thenReturn(otherUser);
        when(chatService.create(any())).thenReturn(chat);
        when(messageService.save(any())).thenReturn(message);

        ResultActions response = mockMvc.perform(post("/api/chats/add_message")
                .header("Authorization", "Basic " + base64Credentials)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatMessageCreationDto)));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(message.getId().intValue())))
                .andExpect(jsonPath("$.chat.id", is(chat.getId().intValue())))
                .andExpect(jsonPath("$.chat.sender.id", is(chat.getUserFirst().getId().intValue())))
                .andExpect(jsonPath("$.chat.sender.name", is(chat.getUserFirst().getName())))
                .andExpect(jsonPath("$.chat.sender.image", is(chat.getUserFirst().getImage())))
                .andExpect(jsonPath("$.chat.recipient.id", is(chat.getUserSecond().getId().intValue())))
                .andExpect(jsonPath("$.chat.recipient.name", is(chat.getUserSecond().getName())))
                .andExpect(jsonPath("$.chat.recipient.image", is(chat.getUserSecond().getImage())))
                .andExpect(jsonPath("$.data", is(message.getData())))
                .andExpect(jsonPath("$.sender.id", is(currentUser.getId().intValue())))
                .andExpect(jsonPath("$.sender.name", is(currentUser.getName())))
                .andExpect(jsonPath("$.sender.image", is(currentUser.getImage())));
    }

    @Test
    void testDeleteChat() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        doNothing().when(chatService).delete(any());

        mockMvc.perform(delete("/api/chats/delete/1")
                        .header("Authorization", "Basic " + base64Credentials)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testGetChatByUsernames() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        mockMvc.perform(get("/api/chats/usernames")
                        .header("Authorization", "Basic " + base64Credentials)
                        .param("first_user", "first_user")
                        .param("second_user", "second_user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testGetChatByUsernamesBadRequest() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        mockMvc.perform(get("/api/chats/usernames")
                        .header("Authorization", "Basic " + base64Credentials)
                        .param("second_user", "second_user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetChannels() throws Exception {
        mockMvc.perform(get("/api/channels")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateChannelName() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        doNothing().when(channelService).updateName(BigInteger.ONE, "new_name");

        mockMvc.perform(put("/api/channels/1/update")
                        .header("Authorization", "Basic " + base64Credentials)
                        .param("name", "new_name")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteChannel() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        doNothing().when(channelService).delete(any());

        mockMvc.perform(delete("/api/channels/delete/1")
                        .header("Authorization", "Basic " + base64Credentials)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testAddMessageToChannel() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        ChannelMessageCreationDto channelMessageCreationDto = new ChannelMessageCreationDto();
        channelMessageCreationDto.setCurrentUsername("curr_user");
        channelMessageCreationDto.setChannelName("name");
        channelMessageCreationDto.setMessage("Test");

        User currentUser = new User();
        currentUser.setId(BigInteger.ONE);
        currentUser.setName("curr_user");
        currentUser.setEmail("curr_user@example.com");
        currentUser.setPassword("secret");
        currentUser.setImage(null);


        Channel channel = new Channel();
        channel.setId(BigInteger.ONE);
        channel.setName("name");
        channel.setCreator(currentUser);

        Message message = new Message();
        message.setId(BigInteger.ONE);
        message.setChannel(channel);
        message.setSender(currentUser);
        message.setData(channelMessageCreationDto.getMessage());
        message.setDate(LocalDateTime.now());

        when(channelService.getChannelByName("name")).thenReturn(channel);
        when(userService.getUserByName(channelMessageCreationDto.getCurrentUsername())).thenReturn(currentUser);
        when(messageService.save(any())).thenReturn(message);

        ResultActions response = mockMvc.perform(post("/api/channels/add_message")
                .header("Authorization", "Basic " + base64Credentials)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(channelMessageCreationDto)));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(message.getId().intValue())))
                .andExpect(jsonPath("$.channelName", is(channel.getName())))
                .andExpect(jsonPath("$.data", is(message.getData())))
                .andExpect(jsonPath("$.sender.id", is(currentUser.getId().intValue())))
                .andExpect(jsonPath("$.sender.name", is(currentUser.getName())))
                .andExpect(jsonPath("$.sender.image", is(currentUser.getImage())));
    }

    @Test
    void testCreateChannel() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        ChannelCreationDto channelCreationDto = new ChannelCreationDto();
        channelCreationDto.setUsername("curr_user");
        channelCreationDto.setChannelName("test");

        User currentUser = new User();
        currentUser.setId(BigInteger.ONE);
        currentUser.setName("curr_user");
        currentUser.setEmail("curr_user@example.com");
        currentUser.setPassword("secret");
        currentUser.setImage(null);

        Channel channel = new Channel();
        channel.setId(BigInteger.ONE);
        channel.setName("test");
        channel.setCreator(currentUser);

        Role role = new Role();
        role.setId(BigInteger.ONE);
        role.setName("owner");
        role.setIsAdmin(true);
        role.setIsCreator(true);

        Member member = new Member();
        member.setId(BigInteger.ONE);
        member.setUser(currentUser);
        member.setChannel(channel);
        member.setRole(role);

        when(userService.getUserByName(channelCreationDto.getUsername())).thenReturn(currentUser);
        when(channelService.create(any())).thenReturn(channel);
        when(roleService.save(any())).thenReturn(role);
        doNothing().when(memberService).save(member);
        when(memberService.getMembersByChannel(channel)).thenReturn(Collections.singletonList(member));
        when(messageService.getMessagesByChannel(channel)).thenReturn(Collections.emptyList());

        ResultActions response = mockMvc.perform(post("/api/channels/create")
                .header("Authorization", "Basic " + base64Credentials)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(channelCreationDto)));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.channel.id", is(channel.getId().intValue())))
                .andExpect(jsonPath("$.channel.name", is(channel.getName())))
                .andExpect(jsonPath("$.creator.id", is(channel.getCreator().getId().intValue())))
                .andExpect(jsonPath("$.creator.name", is(channel.getCreator().getName())))
                .andExpect(jsonPath("$.creator.image", is(channel.getCreator().getImage())))
                .andExpect(jsonPath("$.members[0].user.id", is(member.getUser().getId().intValue())))
                .andExpect(jsonPath("$.members[0].user.name", is(member.getUser().getName())))
                .andExpect(jsonPath("$.members[0].user.image", is(member.getUser().getImage())))
                .andExpect(jsonPath("$.members[0].role.name", is(member.getRole().getName())))
                .andExpect(jsonPath("$.members[0].role.admin", is(member.getRole().getIsAdmin())))
                .andExpect(jsonPath("$.members[0].role.creator", is(member.getRole().getIsCreator())))
                .andExpect(jsonPath("$.messages", is(Collections.emptyList())));
    }

    @Test
    void testJoinTheChannel() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        User currentUser = new User();
        currentUser.setId(BigInteger.ONE);
        currentUser.setName("curr_user");
        currentUser.setEmail("curr_user@example.com");
        currentUser.setPassword("secret");
        currentUser.setImage(null);

        Channel channel = new Channel();
        channel.setId(BigInteger.ONE);
        channel.setName("test");
        channel.setCreator(currentUser);

        Role role = new Role();
        role.setId(BigInteger.ONE);
        role.setName("member");
        role.setIsAdmin(false);
        role.setIsCreator(false);

        Member member = new Member();
        member.setId(BigInteger.ONE);
        member.setUser(currentUser);
        member.setChannel(channel);
        member.setRole(role);

        when(userService.getUserByName("curr_user")).thenReturn(currentUser);
        when(channelService.getChannelByName("test")).thenReturn(channel);
        when(roleService.save(any())).thenReturn(role);
        doNothing().when(memberService).save(member);

        mockMvc.perform(post("/api/channels/join")
                        .header("Authorization", "Basic " + base64Credentials)
                        .param("username", "curr_user")
                        .param("channel_name", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testJoinTheChannelIncorrectRequest() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        User currentUser = new User();
        currentUser.setId(BigInteger.ONE);
        currentUser.setName("curr_user");
        currentUser.setEmail("curr_user@example.com");
        currentUser.setPassword("secret");
        currentUser.setImage(null);

        Channel channel = new Channel();
        channel.setId(BigInteger.ONE);
        channel.setName("test");
        channel.setCreator(currentUser);

        Role role = new Role();
        role.setId(BigInteger.ONE);
        role.setName("member");
        role.setIsAdmin(false);
        role.setIsCreator(false);

        Member member = new Member();
        member.setId(BigInteger.ONE);
        member.setUser(currentUser);
        member.setChannel(channel);
        member.setRole(role);

        when(userService.getUserByName("curr_user")).thenReturn(currentUser);
        when(channelService.getChannelByName("not_found"))
                .thenThrow(new ChannelException("Не существует канала с таким названием: not_found"));

        mockMvc.perform(post("/api/channels/join")
                        .header("Authorization", "Basic " + base64Credentials)
                        .param("username", "curr_user")
                        .param("channel_name", "not_found")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testLeaveTheChannel() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        User currentUser = new User();
        currentUser.setId(BigInteger.ONE);
        currentUser.setName("curr_user");
        currentUser.setEmail("curr_user@example.com");
        currentUser.setPassword("secret");
        currentUser.setImage(null);

        Channel channel = new Channel();
        channel.setId(BigInteger.ONE);
        channel.setName("test");
        channel.setCreator(currentUser);

        Role role = new Role();
        role.setId(BigInteger.ONE);
        role.setName("member");
        role.setIsAdmin(false);
        role.setIsCreator(false);

        Member member = new Member();
        member.setId(BigInteger.ONE);
        member.setUser(currentUser);
        member.setChannel(channel);
        member.setRole(role);

        when(userService.getUserByName("curr_user")).thenReturn(currentUser);
        when(channelService.getChannelById(BigInteger.ONE)).thenReturn(channel);
        when(memberService.getMemberByUserAndChannel(currentUser, channel)).thenReturn(member);
        doNothing().when(roleService).delete(member.getRole().getId());
        doNothing().when(memberService).delete(member.getId());

        mockMvc.perform(delete("/api/channels/1/leave")
                        .header("Authorization", "Basic " + base64Credentials)
                        .param("username", "curr_user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testLeaveTheChannelIncorrectRequest() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        User currentUser = new User();
        currentUser.setId(BigInteger.ONE);
        currentUser.setName("curr_user");
        currentUser.setEmail("curr_user@example.com");
        currentUser.setPassword("secret");
        currentUser.setImage(null);

        Channel channel = new Channel();
        channel.setId(BigInteger.ONE);
        channel.setName("test");
        channel.setCreator(currentUser);

        Role role = new Role();
        role.setId(BigInteger.ONE);
        role.setName("member");
        role.setIsAdmin(false);
        role.setIsCreator(false);

        Member member = new Member();
        member.setId(BigInteger.ONE);
        member.setUser(currentUser);
        member.setChannel(channel);
        member.setRole(role);

        when(userService.getUserByName("not_found"))
                .thenThrow(new UserException("Не существует пользовтаеля с таким именем: not_found"));

        mockMvc.perform(delete("/api/channels/1/leave")
                        .header("Authorization", "Basic " + base64Credentials)
                        .param("username", "not_found")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testSaveMessage() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        SavedMessageDto savedMessageDto = new SavedMessageDto();
        savedMessageDto.setUsername("curr_user");
        savedMessageDto.setMessageId(BigInteger.ONE);

        User currentUser = new User();
        currentUser.setId(BigInteger.ONE);
        currentUser.setName("curr_user");
        currentUser.setEmail("curr_user@example.com");
        currentUser.setPassword("secret");
        currentUser.setImage(null);

        Channel channel = new Channel();
        channel.setId(BigInteger.ONE);
        channel.setName("test");
        channel.setCreator(currentUser);

        Message message = new Message();
        message.setId(BigInteger.ONE);
        message.setChannel(channel);
        message.setSender(currentUser);
        message.setData("Test save message!");
        message.setDate(LocalDateTime.now());

        when(userService.getUserByName(savedMessageDto.getUsername())).thenReturn(currentUser);
        when(messageService.getMessage(BigInteger.ONE)).thenReturn(message);
        doNothing().when(savedMessageService).save(any());

        mockMvc.perform(post("/api/saved_message/save")
                        .header("Authorization", "Basic " + base64Credentials)
                        .param("username", "curr_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedMessageDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteSavedMessagesForUser() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        User currentUser = new User();
        currentUser.setId(BigInteger.ONE);
        currentUser.setName("curr_user");
        currentUser.setEmail("curr_user@example.com");
        currentUser.setPassword("secret");
        currentUser.setImage(null);

        when(userService.getById(BigInteger.ONE)).thenReturn(currentUser);
        doNothing().when(savedMessageService).deleteAllByUser(currentUser);

        mockMvc.perform(delete("/api/saved_message/delete_all")
                        .header("Authorization", "Basic " + base64Credentials)
                        .param("user_id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteSavedMessage() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        User currentUser = new User();
        currentUser.setId(BigInteger.ONE);
        currentUser.setName("curr_user");
        currentUser.setEmail("curr_user@example.com");
        currentUser.setPassword("secret");
        currentUser.setImage(null);

        Channel channel = new Channel();
        channel.setId(BigInteger.ONE);
        channel.setName("test");
        channel.setCreator(currentUser);

        Message message = new Message();
        message.setId(BigInteger.ONE);
        message.setChannel(channel);
        message.setSender(currentUser);
        message.setData("Test save message!");
        message.setDate(LocalDateTime.now());

        when(userService.getById(BigInteger.ONE)).thenReturn(currentUser);
        when(messageService.getMessage(BigInteger.ONE)).thenReturn(message);
        doNothing().when(savedMessageService).deleteAllByUser(currentUser);

        mockMvc.perform(delete("/api/saved_message/delete")
                        .header("Authorization", "Basic " + base64Credentials)
                        .param("user_id", "1")
                        .param("message_id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testRoleCreation() throws Exception {
        String credentials = SECURITY_USERNAME + ":" + SECURITY_PASSWORD;
        String base64Credentials = new String(Base64.encode(credentials.getBytes()));

        RoleCreationDto roleCreationDto = new RoleCreationDto();
        roleCreationDto.setName("test_role");
        roleCreationDto.setIsAdmin(true);
        roleCreationDto.setUsername("curr_user");
        roleCreationDto.setChannelName("test");

        User currentUser = new User();
        currentUser.setId(BigInteger.ONE);
        currentUser.setName("curr_user");
        currentUser.setEmail("curr_user@example.com");
        currentUser.setPassword("secret");
        currentUser.setImage(null);

        Channel channel = new Channel();
        channel.setId(BigInteger.ONE);
        channel.setName("test");
        channel.setCreator(currentUser);

        Role role = new Role();
        role.setId(BigInteger.ONE);
        role.setName("member");
        role.setIsAdmin(false);
        role.setIsCreator(false);

        Role newRole = new Role();
        newRole.setId(role.getId());
        newRole.setName(roleCreationDto.getName());
        newRole.setIsAdmin(roleCreationDto.getIsAdmin());
        newRole.setIsCreator(false);

        Member member = new Member();
        member.setId(BigInteger.ONE);
        member.setUser(currentUser);
        member.setChannel(channel);
        member.setRole(role);

        when(userService.getUserByName("curr_user")).thenReturn(currentUser);
        when(channelService.getChannelByName("test")).thenReturn(channel);
        when(memberService.getMemberByUserAndChannel(currentUser, channel)).thenReturn(member);
        when(roleService.update(member.getRole().getId(), newRole)).thenReturn(newRole);
        doNothing().when(memberService).updateRole(member, newRole);

        mockMvc.perform(put("/api/roles/update")
                        .header("Authorization", "Basic " + base64Credentials)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleCreationDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
