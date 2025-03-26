package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testSerialize() throws Exception {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .build();

        var result = json.write(user);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).hasJsonPathStringValue("$.email");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Alice");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("alice@example.com");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = """
                   {
                   "id": 2,
                   "name": "Bob",
                   "email": "bob@example.com"
                   }
                   """;

        UserDto user = json.parseObject(content);

        assertThat(user.getId()).isEqualTo(2L);
        assertThat(user.getName()).isEqualTo("Bob");
        assertThat(user.getEmail()).isEqualTo("bob@example.com");
    }
}
