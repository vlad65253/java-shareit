package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.interfaces.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JacksonTester<UserDto> json;

    private UserDto userDto;
    private Long userId;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        userDto = userService.createUser(userDto);
        userId = userDto.getId();
    }

    @Test
    void testGetUserById() {
        UserDto user = userService.getUser(userId);
        assertNotNull(user);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void testCreateUser() {
        UserDto newUserInputDto = UserDto.builder()
                .name("New User")
                .email("newuser@example.com")
                .build();

        UserDto newUserOutputDto = userService.createUser(newUserInputDto);
        assertNotNull(newUserOutputDto);
        assertEquals(newUserInputDto.getName(), newUserOutputDto.getName());
        assertEquals(newUserInputDto.getEmail(), newUserOutputDto.getEmail());
    }

    @Test
    void testUpdateUser() {
        UserDto updatedUserInputDto = UserDto.builder()
                .name("Updated User")
                .email("updateduser@example.com")
                .build();

        UserDto updatedUserOutputDto = userService.patchUser(userId, updatedUserInputDto);
        assertNotNull(updatedUserOutputDto);
        assertEquals(updatedUserInputDto.getName(), updatedUserOutputDto.getName());
        assertEquals(updatedUserInputDto.getEmail(), updatedUserOutputDto.getEmail());
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser(userId);
        assertThrows(NotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    void testCreateUserWithDuplicateEmail() {
        UserDto duplicateUserInputDto = UserDto.builder()
                .name("Duplicate User")
                .email("test@example.com")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(duplicateUserInputDto));
    }

    @Test
    void testUpdateUserWithDuplicateEmail() {
        UserDto duplicateUserInputDto = UserDto.builder()
                .name("Duplicate User")
                .email("newuser@example.com")
                .build();

        UserDto newUserInputDto = UserDto.builder()
                .name("New User")
                .email("newuser@example.com")
                .build();
        UserDto newUserOutputDto = userService.createUser(newUserInputDto);

        assertThrows(DataIntegrityViolationException.class, () -> userService.patchUser(newUserOutputDto.getId(), duplicateUserInputDto));
    }


    @Test
    void testSerialize() throws Exception {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .build();

        // сериализация
        JsonContent<UserDto> result = json.write(user);
        assertThat(result).hasJsonPathNumberValue("@.id");
        assertThat(result).hasJsonPathStringValue("@.name");
        assertThat(result).hasJsonPathStringValue("@.email");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Alice");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("alice@example.com");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = """
                {
                  "id": 1,
                  "name": "Bob",
                  "email": "bob@example.com"
                }
                """;

        // десериализация
        UserDto user = json.parseObject(content);
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Bob");
        assertThat(user.getEmail()).isEqualTo("bob@example.com");
    }
}