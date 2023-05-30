package ru.vsu.cs.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.api.dto.SavedMessageDto;
import ru.vsu.cs.api.models.SavedMessage;
import ru.vsu.cs.api.models.User;
import ru.vsu.cs.api.services.MessageService;
import ru.vsu.cs.api.services.SavedMessageService;
import ru.vsu.cs.api.services.UserService;
import ru.vsu.cs.api.utils.ErrorResponse;
import ru.vsu.cs.api.utils.exceptions.MessageException;
import ru.vsu.cs.api.utils.exceptions.UserException;

import java.math.BigInteger;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/saved_message")
@CrossOrigin
@Tag(name = "Сохранённые сообщения", description = "Методы для работы с сообщениями")
public class SaveMessageController {
    private final UserService userService;
    private final SavedMessageService savedMessageService;
    private final MessageService messageService;

    @Autowired
    public SaveMessageController(UserService userService, SavedMessageService savedMessageService,
                                 MessageService messageService) {
        this.userService = userService;
        this.savedMessageService = savedMessageService;
        this.messageService = messageService;
    }

    @PostMapping("/save")
    @Operation(summary = "Сохранение сообщения")
    public ResponseEntity<HttpStatus> saveMessage(@NotNull @RequestBody SavedMessageDto savedMessageDto,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        User user = userService.getUserByName(savedMessageDto.getUsername());

        savedMessageService.save(new SavedMessage(messageService.getMessage(savedMessageDto.getMessageId()), user));
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @DeleteMapping("/delete_all")
    @Operation(summary = "Удаление всех сохраннёных сообщений у пользователя")
    public ResponseEntity<HttpStatus> deleteAllMessage(@RequestParam("user_id") BigInteger userId) {
        savedMessageService.deleteAllByUser(userService.getById(userId));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Удаление конкретного сохранённого сообщения")
    public ResponseEntity<HttpStatus> deleteMessage(@RequestParam("user_id") BigInteger userId,
                                                    @RequestParam("message_id") BigInteger messageId) {
        savedMessageService.delete(messageService.getMessage(messageId), userService.getById(userId));
        return ResponseEntity.ok(HttpStatus.OK);
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
}
