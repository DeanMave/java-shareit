package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CommentMapperTest {

    @Autowired
    private CommentMapper mapper;

    @Test
    void testToCommentDto_shouldMapAllFields() {
        User author = new User(1L, "Author Name", "author@mail.ru");
        Item item = new Item(2L, "Item Name", "Item description", author, true, null);
        LocalDateTime created = LocalDateTime.now();
        Comment comment = new Comment(3L, "Test comment", item, author, created);
        CommentDto dto = mapper.toCommentDto(comment);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(comment.getId());
        assertThat(dto.getText()).isEqualTo(comment.getText());
        assertThat(dto.getAuthorName()).isEqualTo(author.getName());
        assertThat(dto.getCreated()).isEqualTo(created);
    }

    @Test
    void testToComment_shouldMapAllFieldsAndIgnoreId() {
        CommentDto commentDto = new CommentDto(null, "New comment", "Author Name", null);
        Item item = new Item(2L, "Item Name", "Item description", new User(), true, null);
        User author = new User(1L, "Author Name", "author@mail.ru");
        Comment comment = mapper.toComment(commentDto, item, author);
        assertThat(comment).isNotNull();
        assertThat(comment.getId()).isNull();
        assertThat(comment.getText()).isEqualTo(commentDto.getText());
        assertThat(comment.getItem()).isEqualTo(item);
        assertThat(comment.getAuthor()).isEqualTo(author);
        assertThat(comment.getCreated()).isNotNull();
    }
}