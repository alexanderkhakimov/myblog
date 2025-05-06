package com.myblog.controller;

import com.myblog.model.Post;
import com.myblog.model.Tag;
import com.myblog.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        try {
            // Валидация вручную
            if (title == null || title.trim().isEmpty()) {
                model.addAttribute("error", "Title is required");
                model.addAttribute("post", post);
                return "add-post";
            }
            if (text == null || text.trim().isEmpty()) {
                model.addAttribute("error", "Content is required");
                model.addAttribute("post", post);
                return "add-post";
            }
            post.setTitle(title);
            post.setContent(text);
            if (tags != null && !tags.trim().isEmpty()) {
                String[] tagArray = tags.trim().split("\\s*,\\s*");
                List<Tag> tagList = Arrays.stream(tagArray)
                        .filter(tag -> !tag.isEmpty())
                        .map(name -> {
                            Tag tag = new Tag();
                            tag.setName(name);
                            return tag;
                        })
                        .collect(Collectors.toList());
                if (tagList.stream().anyMatch(tag -> tag.getName().length() > 50)) {
                    model.addAttribute("error", "Each tag must be 50 characters or less");
                    model.addAttribute("post", post);
                    return "add-post";
                }
                post.setTags(tagList);
            } else {
                post.setTags(new ArrayList<>());
            }
            if (image != null && !image.isEmpty()) {
                String contentType = image.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    model.addAttribute("error", "Only image files are allowed");
                    model.addAttribute("post", post);
                    return "add-post";
                }
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                String uploadDir = "C:/Program Files/Apache Software Foundation/Tomcat 11.0/webapps/myblog/static/uploads/";
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    boolean created = uploadDirFile.mkdirs();
                    if (!created) {
                        model.addAttribute("error", "Failed to create upload directory");
                        model.addAttribute("post", post);
                        return "add-post";
                    }
                }
                File destinationFile = new File(uploadDir + fileName);
                image.transferTo(destinationFile);
                post.setImageUrl("/static/uploads/" + fileName);
            }
            postService.savePost(post);
            return "redirect:/posts";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload image: " + e.getMessage());
            model.addAttribute("post", post);
            return "add-post";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to save post: " + e.getMessage());
            model.addAttribute("post", post);
            return "add-post";
        }
    }

    @PostMapping("/{id}/like")
    public String likePost(@PathVariable(name = "id") Long id) {
        postService.likePost(id);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable(name = "id") Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }
}