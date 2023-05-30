package ru.vsu.cs.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.api.dto.RoleCreationDto;
import ru.vsu.cs.api.models.Channel;
import ru.vsu.cs.api.models.Role;
import ru.vsu.cs.api.models.User;
import ru.vsu.cs.api.services.ChannelService;
import ru.vsu.cs.api.services.MemberService;
import ru.vsu.cs.api.services.RoleService;
import ru.vsu.cs.api.services.UserService;
import ru.vsu.cs.api.utils.ErrorResponse;
import ru.vsu.cs.api.utils.exceptions.MemberException;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin
@Tag(name = "Роли", description = "Методы для работы с ролями")
public class RoleController {
    private final MemberService memberService;
    private final RoleService roleService;
    private final UserService userService;
    private final ChannelService channelService;

    @Autowired
    public RoleController(MemberService memberService, RoleService roleService, UserService userService,
                          ChannelService channelService) {
        this.memberService = memberService;
        this.roleService = roleService;
        this.userService = userService;
        this.channelService = channelService;
    }

    @PostMapping("/create")
    @Operation(summary = "Создание/Обновление роли")
    public ResponseEntity<HttpStatus> create(@Valid @RequestBody RoleCreationDto roleCreationDto,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        User user = userService.getUserByName(roleCreationDto.getUsername());
        Channel channel = channelService.getChannelByName(roleCreationDto.getChannelName());

        Role role = roleService.save(new Role(roleCreationDto.getName(), roleCreationDto.getIsAdmin(), false));

        memberService.updateRole(user, channel, role);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler()
    private ResponseEntity<ErrorResponse> memberException(MemberException ex) {
        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.PAYLOAD_TOO_LARGE);
    }


}
