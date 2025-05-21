package com.myblog.service;

import com.myblog.model.Comment;
import com.myblog.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    private Post testPost;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        testPost = new Post();
        testPost.setTitle("Тестовый пост");
        testPost.setContent("Это контент тестового поста.");

        postService.savePost(testPost);
        testComment = new Comment();
        testComment.setContent("Это тестовый комментарий.");
        testComment.setPostId(testPost.getId());
        commentService.saveComment(testComment);
    }

    @Test
    void testSaveComment() {
        Comment saved = commentService.getCommentByPostId(testPost.getId()).getFirst();

        assertNotNull(saved, "Сохраненный комментарий не должен быть null");
        assertEquals(saved.getContent(), testComment.getContent(), "Содержимое комментария должно совпадать");
        assertEquals(testPost.getId(), saved.getPostId(), "Комментарий должен быть связан с правильным постом");
    }

    @Test
    void testDeleteComment() {
        commentService.saveComment(testComment);

        List<Comment> list = commentService.getCommentByPostId(testPost.getId());
        Long comment_id = list.getFirst().getId();
        commentService.deleteComment(comment_id);

        Comment deletedComment = commentService.getCommentById(comment_id);
        assertNull(deletedComment, "Удаленный комментарий должен быть null");
    }

    @Test
    void testGetCommentsByPost() {
        Post post = new Post();
        post.setTitle("Тест пост для возврата по id");
        post.setContent("Какой то текст!");
        postService.savePost(post);

        Comment comment1 = new Comment();
        comment1.setContent("Комментарий 1");
        comment1.setPostId(post.getId());
        commentService.saveComment(comment1);

        Comment comment2 = new Comment();
        comment2.setContent("Комментарий 2");
        comment2.setPostId(post.getId());
        commentService.saveComment(comment2);

        List<Comment> comments = commentService.getCommentByPostId(post.getId());

        assertEquals(2, comments.size(), "Должно быть два комментария");
        assertTrue(comments.stream().anyMatch(c -> c.getContent().equals("Комментарий 1")), "Комментарий 1 должен быть в списке");
        assertTrue(comments.stream().anyMatch(c -> c.getContent().equals("Комментарий 2")), "Комментарий 2 должен быть в списке");
    }

    @Test
    void testUpdateComment() {
        Long postId = testPost.getId();
        List<Comment> comments = commentService.getCommentByPostId(postId);
        Comment commentUpdate = comments.getFirst();
        commentUpdate.setContent("Изменный комментарий!");
        Long commentId = commentUpdate.getId();
        commentService.updateComment(commentUpdate);

        assertEquals("Изменный комментарий!", commentService.getCommentById(commentId).getContent(), "Здесь должен быть одинаковый контекст 'Изменный комментарий!'");
    }
}