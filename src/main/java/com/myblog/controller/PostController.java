package com.myblog.controller;

import com.myblog.model.Post;
import com.myblog.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String getPosts(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "tag", required = false) String tag,
            Model model
    ) {
        model.addAttribute("posts", postService.getPosts(page, size, tag));
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("tag", tag);
        return "posts";
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable(name = "id") Long id, Model model) {
        Post post = postService.getPostById(id);
        if (post == null) {
            return "redirect:/posts";
        }
        model.addAttribute("post", post);
        return "post";
    }

    @GetMapping("/add")
    public String showAddPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "add-post";
    }

    @PostMapping
    public String savePost(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Model model
    ) {
        Post post = new Post();
        String result = postService.processPost(post, title, text, tags, image, model);
        if (result != null) {
            model.addAttribute("post", post);
            model.addAttribute("error", result);
            return result; // Возвращает "add-post" с ошибкой
        }
        postService.savePost(post);
        return "redirect:/posts";
    }

    @PostMapping("/{id}/edit")
    public String updatePost(
            @PathVariable(name = "id") Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Model model
    ) {
        Post post = postService.getPostById(id);
        if (post == null) {
            model.addAttribute("error", "Post not found");
            return "redirect:/posts";
        }
        String result = postService.processPost(post, title, text, tags, image, model);
        if (result != null) {
            return result; // Возвращает "add-post" с ошибкой
        }
        postService.updatePost(post);
        return "redirect:/posts";
    }

    @PostMapping("/{id}/like")
    public String likePost(@PathVariable(name = "id") Long id, @RequestParam(name = "like") boolean like) {
        if (like) {
            postService.likePost(id);
        } else {
            postService.disLikePost(id);
        }
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable(name = "id") Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }

    @GetMapping("/{id}/edit")
    public String editPost(@PathVariable(name = "id") Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute(post);
        return "add-post";
    }

}