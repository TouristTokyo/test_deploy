package ru.vsu.cs.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.api.dto.message.ChatMessageCreationDto;
import ru.vsu.cs.api.dto.message.ChatMessageDto;
import ru.vsu.cs.api.dto.search.ChatSearchDto;
import ru.vsu.cs.api.models.Chat;
import ru.vsu.cs.api.models.Message;
import ru.vsu.cs.api.models.User;
import ru.vsu.cs.api.services.ChatService;
import ru.vsu.cs.api.services.MessageService;
import ru.vsu.cs.api.services.UserService;
import ru.vsu.cs.api.utils.ErrorResponse;
import ru.vsu.cs.api.utils.exceptions.ChatException;
import ru.vsu.cs.api.utils.exceptions.MessageException;
import ru.vsu.cs.api.utils.exceptions.UserException;
import ru.vsu.cs.api.utils.mapper.Mapper;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chats")
@CrossOrigin
@Tag(name = "Чаты", description = "Методы для работы с чатами")
public class ChatController {
    private final UserService userService;
    private final ChatService chatService;
    private final MessageService messageService;

    @Autowired
    public ChatController(UserService userService, ChatService chatService, MessageService messageService) {
        this.userService = userService;
        this.chatService = chatService;
        this.messageService = messageService;
    }

    @GetMapping()
    public List<ChatSearchDto> getChats() {
        return userService.getUsers().stream().map(Mapper::convertToChatSearchDto).toList();
    }

    @PostMapping("/add_message")
    @Operation(summary = "Отправка сообщения в чат")
    public ResponseEntity<ChatMessageDto> addMessage(@Valid @RequestBody ChatMessageCreationDto chatMessageCreationDto,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        User currentUser = userService.getUserByName(chatMessageCreationDto.getCurrentUsername());
        User otherUser = userService.getUserByName(chatMessageCreationDto.getOtherUsername());


        Chat chat = chatService.create(Mapper.convertToChat(currentUser, otherUser));
        Message message = messageService.save(new Message(currentUser, chat, chatMessageCreationDto.getMessage()));

        return new ResponseEntity<>(Mapper.convertToChatMessageDto(message), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удаление чата")
    public ResponseEntity<HttpStatus> deleteChat(@PathVariable("id") BigInteger id) {
        chatService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение чата по id")
    public List<ChatMessageDto> getChatById(@PathVariable("id") BigInteger id) {
        List<Message> messages = messageService.getMessagesByChat(chatService.getById(id));

        return messages.stream().map(Mapper::convertToChatMessageDto).toList();
    }

    @GetMapping("/usernames")
    @Operation(summary = "Получение чата по именам (никнеймам) пользователей")
    public ResponseEntity<List<ChatMessageDto>> getChatByUsernames(@RequestParam("first_user") String firstUser,
                                                                   @RequestParam("second_user") String secondUser) {
        User currentUser = userService.getUserByName(firstUser);
        User otherUser = userService.getUserByName(secondUser);

        Chat chat = chatService.getByUsernames(currentUser, otherUser);
        List<Message> messages = messageService.getMessagesByChat(chat);

        return new ResponseEntity<>(messages.stream().map(Mapper::convertToChatMessageDto).toList(), HttpStatus.OK);
    }


    @ExceptionHandler
    private ResponseEntity<ErrorResponse> userException(UserException ex) {
        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> messageException(MessageException ex) {
        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler
    private ResponseEntity<ErrorResponse> chatException(ChatException ex) {
        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
