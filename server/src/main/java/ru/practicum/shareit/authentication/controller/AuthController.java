package ru.practicum.shareit.authentication.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.authentication.dto.AuthenticationDto;
import ru.practicum.shareit.authentication.service.AuthenticationService;
import ru.practicum.shareit.exception.BadRegistrationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.userDto.UserDto;
import ru.practicum.shareit.utils.JWTUtil;
import ru.practicum.shareit.utils.UserValidator;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/auth")
@Slf4j
public class AuthController {

    private final UserValidator userValidator;
    private final AuthenticationService authenticationService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/registration")
    public String performRegistration(@RequestBody @Valid UserDto userCreateDto,
                                      BindingResult bindingResult) {

        userValidator.validate(userCreateDto, bindingResult);

        User user = UserMapper.userDtoToUser(userCreateDto);

        if (bindingResult.hasErrors()) {
            throw new BadRegistrationException("Пользователь не прошел проверку регистрации!");
        }

        authenticationService.register(user);

        String token = jwtUtil.generateToken(user.getEmail());
        log.debug("Пользователь зарегистрирован {} JWTToken выдан", userCreateDto);
        return token;
    }

    @PostMapping("/login")
    public String performLogin(@RequestBody @Valid AuthenticationDto authenticationDto) {
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authenticationDto.getEmail(), authenticationDto.getPassword());

        try {
            authenticationManager.authenticate(authInputToken);
        } catch (BadCredentialsException e) {
            throw new BadRegistrationException("Incorrect credentials");
        }

        String token = jwtUtil.generateToken(authenticationDto.getEmail());
        log.debug("Пользователь прошел аутентификацию, JWTToken получен");
        return token;
    }

}
