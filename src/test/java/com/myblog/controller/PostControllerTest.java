package com.myblog.controller;

import com.myblog.model.Post;
import com.myblog.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
public class PostControllerTest {
    private MockMvc mockMvc;
    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;
    private Post testPost;

    @BeforeEach
    void setUp() {
        // Настраиваем Thymeleaf
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setCharacterEncoding("UTF-8");

        mockMvc = MockMvcBuilders
                .standaloneSetup(postController)
                .setViewResolvers(viewResolver)
                .build();


        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Тестовый пост");
        testPost.setContent("Контент тестового поста");
        testPost.setImageUrl("/static/uploads/test.jpg");
        testPost.setLikes(0);
        testPost.setComments(new ArrayList<>());
    }

    @Test
    void testGetPosts() throws Exception {
        when(postService.getPosts(1, 10, null)).thenReturn(Collections.singletonList(testPost));

        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("pageSize", 10))
                .andExpect(model().attribute("tag", (String) null));

        verify(postService).getPosts(1, 10, null);

    }

    @Test
    void testGetPostsWithTag() throws Exception {
        when(postService.getPosts(1, 10, "test")).thenReturn(Collections.singletonList(testPost));

        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .param("page", "1")
                        .param("size", "10")
                        .param("tag", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts"))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("pageSize", 10))
                .andExpect(model().attribute("tag", "test"));

        verify(postService).getPosts(1, 10, "test");
    }

    @Test
    void testGetPost() throws Exception {
        when(postService.getPostById(1L)).thenReturn(testPost);

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("post"))
                .andExpect(model().attribute("post", testPost));

        verify(postService).getPostById(1L);
    }

    @Test
    void testGetPostNotFound() throws Exception {
        when(postService.getPostById(1L)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        verify(postService).getPostById(1L);
    }

    @Test
    void testShowAddPostForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attribute("post", instanceOf(Post.class)));
    }

    @Test
    void testSavePostSuccess() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpg",
                "test image".getBytes()
        );

        when(postService.processPost(
                any(Post.class),
                eq("Title"),
                eq("Content"),
                eq("tag1"),
                eq(image),
                any()
        )).thenReturn(null);

        mockMvc.perform(multipart("/posts")
                        .file(image)
                        .param("title", "Title")
                        .param("text", "Content")
                        .param("tags", "tag1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        verify(postService).savePost(any(Post.class));

    }

    @Test
    void testSavePostValidationError() throws Exception {

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image".getBytes());
        when(postService.processPost(
                any(Post.class),
                eq(""),
                eq("Content"),
                eq("tag1"),
                eq(image), any()))
                .thenReturn("add-post");

        mockMvc.perform(multipart("/posts")
                        .file(image)
                        .param("title", "")
                        .param("text", "Content")
                        .param("tags", "tag1"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-post"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeExists("post"));

        verify(postService, never()).savePost(any(Post.class));
    }

    @Test
    void testUpdatePostSuccess() throws Exception {

        when(postService.getPostById(1L)).thenReturn(testPost);
        when(postService.processPost(any(Post.class), eq("Updated Title"), eq("Updated Content"), eq("tag1"), any(), any()))
                .thenReturn(null);


        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image".getBytes());
        mockMvc.perform(multipart("/posts/1/edit")
                        .file(image)
                        .param("title", "Updated Title")
                        .param("text", "Updated Content")
                        .param("tags", "tag1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));


        verify(postService).updatePost(any(Post.class));
    }

    @Test
    void testUpdatePostNotFound() throws Exception {

        when(postService.getPostById(1L)).thenReturn(null);

        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image".getBytes());
        mockMvc.perform(multipart("/posts/1/edit")
                        .file(image)
                        .param("title", "Updated Title")
                        .param("text", "Updated Content")
                        .param("tags", "tag1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));


        verify(postService, never()).updatePost(any(Post.class));
    }

    @Test
    void testLikePost() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/posts/1/like")
                        .param("like", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));


        verify(postService).likePost(1L);
    }

    @Test
    void testDisLikePost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/1/like")
                        .param("like", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));

        verify(postService).disLikePost(1L);
    }

    @Test
    void testDeletePost() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/posts/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        verify(postService).deletePost(1L);
    }

    @Test
    void testEditPost() throws Exception {
        when(postService.getPostById(1L)).thenReturn(testPost);

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-post"))
                .andExpect(model().attribute("post", testPost));

        verify(postService).getPostById(1L);
    }
}
