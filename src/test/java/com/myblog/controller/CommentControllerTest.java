package com.myblog.controller;


import com.myblog.config.ThymeleafTestConfig;
import com.myblog.model.Comment;
import com.myblog.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = ThymeleafTestConfig.class)
public class CommentControllerTest {

    private MockMvc mockMvc;
    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private Comment testComment;

    private static final Long POST_ID = 1L;
    private static final Long COMMENT_ID = 1L;
    private static final String VALID_COMMENT_TEXT = "Тестовый комментарий";
    private static final String EMPTY_COMMENT_TEXT = "   ";

    @BeforeEach
    void setUp() {
        mockMvc = ThymeleafTestConfig.buildMockMvc(commentController);
        testComment = new Comment();
        testComment.setId(COMMENT_ID);
        testComment.setPostId(POST_ID);
        testComment.setContent(VALID_COMMENT_TEXT);
    }

    @Nested
    class AddCommentTests {
        @Test
        void shouldRedirectToPostPageWhenCommentIsAddedSuccessfully() throws Exception {
            doNothing().when(commentService).saveComment(any(Comment.class));

            mockMvc.perform(post("/comments")
                            .param("postId", POST_ID.toString())
                            .param("text", VALID_COMMENT_TEXT))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/posts/" + POST_ID));

            verify(commentService, times(1)).saveComment(any(Comment.class));
            verifyNoMoreInteractions(commentService);
        }

        @Test
        void shouldRedirectWithErrorWhenCommentTextIsEmpty() throws Exception {
            mockMvc.perform(post("/comments")
                            .param("postId", POST_ID.toString())
                            .param("text", EMPTY_COMMENT_TEXT))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/posts/" + POST_ID + "?error=Comment content cannot be empty"));

            verifyNoInteractions(commentService);
        }

        @Test
        void shouldRedirectWithErrorWhenCommentTextIsNull() throws Exception {
            mockMvc.perform(post("/comments")
                            .param("postId", POST_ID.toString()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/posts/" + POST_ID + "?error=Comment content cannot be empty"));

            verifyNoInteractions(commentService);
        }
    }

    @Nested
    class UpdateCommentTests {
        @Test
        void shouldRedirectToPostPageWhenCommentIsUpdatedSuccessfully() throws Exception {
            when(commentService.getCommentById(COMMENT_ID)).thenReturn(testComment);
            doNothing().when(commentService).updateComment(any(Comment.class));

            mockMvc.perform(post("/comments/" + COMMENT_ID + "/edit")
                            .param("text", VALID_COMMENT_TEXT))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/posts/" + POST_ID));

            verify(commentService, times(1)).getCommentById(COMMENT_ID);
            verify(commentService, times(1)).updateComment(testComment);
            verifyNoMoreInteractions(commentService);
        }

        @Test
        void shouldRedirectWithErrorWhenCommentTextIsEmpty() throws Exception {
            when(commentService.getCommentById(COMMENT_ID)).thenReturn(testComment);

            mockMvc.perform(post("/comments/" + COMMENT_ID + "/edit")
                            .param("text", EMPTY_COMMENT_TEXT))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/posts/" + POST_ID + "?error=Comment content cannot be empty"));

            verify(commentService, times(1)).getCommentById(COMMENT_ID);
            verifyNoMoreInteractions(commentService);
        }

        @Test
        void shouldRedirectWithErrorWhenCommentNotFound() throws Exception {
            when(commentService.getCommentById(COMMENT_ID)).thenReturn(null);

            mockMvc.perform(post("/comments/" + COMMENT_ID + "/edit")
                            .param("text", VALID_COMMENT_TEXT))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/posts?error=Comment not found"));

            verify(commentService, times(1)).getCommentById(COMMENT_ID);
            verifyNoMoreInteractions(commentService);
        }
    }

    @Nested
    class DeleteCommentTests {
        @Test
        void shouldRedirectToPostPageWhenCommentIsDeletedSuccessfully() throws Exception {
            doNothing().when(commentService).deleteComment(COMMENT_ID);

            mockMvc.perform(post("/comments/" + COMMENT_ID + "/delete")
                            .param("postId", POST_ID.toString()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/posts/" + POST_ID));

            verify(commentService, times(1)).deleteComment(COMMENT_ID);
            verifyNoMoreInteractions(commentService);
        }
    }


}
