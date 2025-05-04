package com.myblog.controller;

import com.myblog.model.Post;
import com.myblog.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String getPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String tag,
            Model model
    ) {
        model.addAttribute("posts", postService.getPosts(page, size, tag));

        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("tag", tag);

        return "posts";
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable Long id, Model model) {
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
    public String savePost(@ModelAttribute Post post) {
        postService.savePost(post);

        return "redirect:/posts";
    }

    @PostMapping("{id}/like")
    public String likePost(@PathVariable Long id) {
        postService.likePost(id);
        return "redirect:/posts" + id;
    }

    @PostMapping("posts/{id}/delete")
    public String deletePost(@PathVariable Long id){
        postService.deletePost(id);
        return "redirect:/posts";
    }
}
