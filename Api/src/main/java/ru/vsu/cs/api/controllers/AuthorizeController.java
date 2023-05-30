package ru.vsu.cs.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.api.dto.UserCreationDto;
import ru.vsu.cs.api.dto.UserLoginDto;
import ru.vsu.cs.api.dto.UserResponseDto;
import ru.vsu.cs.api.models.*;
import ru.vsu.cs.api.services.ChatService;
import ru.vsu.cs.api.services.MemberService;
import ru.vsu.cs.api.services.SavedMessageService;
import ru.vsu.cs.api.services.UserService;
import ru.vsu.cs.api.utils.ErrorResponse;
import ru.vsu.cs.api.utils.exceptions.UserException;
import ru.vsu.cs.api.utils.mapper.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
@Tag(name = "Аутентификация", description = "Методы для входа и регистрации")
public class AuthorizeController {
    private final UserService userService;
    private final SavedMessageService savedMessageService;
    private final MemberService memberService;
    private final ChatService chatService;

    @Autowired
    public AuthorizeController(UserService userService, SavedMessageService savedMessageService, MemberService memberService, ChatService chatService) {
        this.userService = userService;
        this.savedMessageService = savedMessageService;
        this.memberService = memberService;
        this.chatService = chatService;
    }

    @PostMapping("/login")
    @Operation(summary = "Вход в аккаунт")
    public ResponseEntity<UserResponseDto> login(@Valid @RequestBody UserLoginDto userLoginDto,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        User user = userService.login(userLoginDto.getEmail(), userLoginDto.getPassword());

        List<Message> savedMessages = savedMessageService.getSavedMessageByUser(user).stream()
                .map(SavedMessage::getMessage)
                .toList();

        List<Channel> channels = memberService.getMembersByUser(user).stream()
                .map(Member::getChannel)
                .toList();

        List<Chat> chats = chatService.getChatsByUser(user);

        return new ResponseEntity<>(Mapper.convertToUserResponseDto(user, chats, savedMessages, channels), HttpStatus.OK);
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация")
    public ResponseEntity<HttpStatus> register(@Valid @RequestBody UserCreationDto userCreationDto,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        userService.save(Mapper.convertToUser(userCreationDto));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(UserException ex) {
        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
