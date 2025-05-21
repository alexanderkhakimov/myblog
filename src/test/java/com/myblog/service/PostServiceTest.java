package com.myblog.service;

import com.myblog.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;


import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class PostServiceTest {
    @Autowired
    private PostService postService;

    private Post testPost;

    @BeforeEach
    void setUp(){
        testPost = new Post();
        testPost.setTitle("Тестовый пост");
        testPost.setContent("Это тестовый пост.");
        postService.savePost(testPost);
    }

    @Test
    void testSavePost() {
        Post newPost = new Post();
        newPost.setTitle("Тестовый пост 2");
        newPost.setContent("Это тестовый пост 2.");
        postService.savePost(newPost);

        assertNotNull(postService.getPostById(newPost.getId()),"Не должен быть null");
    }

    @Test
    void testGetPostById() {
        Post newPost = new Post();
        newPost.setTitle("Тестовый пост 2");
        newPost.setContent("Это тестовый пост 2.");
        postService.savePost(newPost);

        assertEquals("Тестовый пост 2", postService.getPostById(newPost.getId()).getTitle(), "Должны совпадать заголовки \"Тестовый пост 2\"");
    }


    @Test
    void testUpdatePost() {
        Post post = postService.getPostById(testPost.getId());
        post.setTitle("Измененый пост");
        postService.updatePost(post);

        assertEquals("Измененый пост",postService.getPostById(testPost.getId()).getTitle(),"Названия поста должны совпадать!");
    }

    @Test
    void testDeletePost() {
        Long id = testPost.getId();
        postService.deletePost(id);

        Post post = postService.getPostById(id);
        assertNull(post,"Должен быть null!");

    }

    @Test
    void testLikePost() {
        Long id = testPost.getId();
        postService.likePost(id);

       int like = postService.getPostById(id).getLikes();

       assertEquals(1, like, "Должен быть 1 лайк!");

    }

    @Test
    void testDisLikePost() {
        Long id = testPost.getId();
        postService.likePost(id);
        postService.disLikePost(id);

        int like = postService.getPostById(id).getLikes();

        assertEquals(0, like, "Должен быть 0 лайков!");

    }

}
