package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void verificationRequest() {
        final User user = User.builder()
                .id(1L)
                .name("max")
                .email("max@mail.ru")
                .build();

        userRepository.save(user);

        final Item item = Item.builder()
                .id(1L)
                .name("дрель")
                .description("description")
                .available(Boolean.TRUE)
                .owner(user)
                .build();

        itemRepository.save(item);

        final Comment comment = Comment.builder()
                .text("text")
                .item(item)
                .user(user)
                .build();

        List<Comment> result = commentRepository.findAllByItemId(commentRepository.save(comment).getId());

        assertThat(result)
                .isNotNull()
                .hasSize(1)
                .usingRecursiveComparison()
                .isEqualTo(List.of(comment));
    }
}
