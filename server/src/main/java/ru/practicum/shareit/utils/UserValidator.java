package ru.practicum.shareit.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.userDto.UserDto;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDto.class.equals(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
        UserDto userCreateDto = (UserDto) o;

        if (userRepository.findByEmail(userCreateDto.getEmail()).isPresent()) {
            throw new AlreadyExistException("Такой пользователь уже зарегистрирован");
        }
    }
}
