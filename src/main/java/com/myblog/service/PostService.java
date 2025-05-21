package com.myblog.service;

import com.myblog.dao.PostDao;
import com.myblog.model.Post;
import com.myblog.model.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {
    private final PostDao postDao;
    private final TagService tagService;
    private final String uploadDir;

    public PostService(PostDao postDao, TagService tagService, @Value("${file.upload-dir}") String uploadDir) {
        this.postDao = postDao;
        this.tagService = tagService;
        this.uploadDir = uploadDir;
    }

    public List<Post> getPosts(int page, int size, String tag) {
        return postDao.findAll(page, size, tag);
    }

    public Post getPostById(Long id) {
        return postDao.findById(id);
    }

    public void savePost(Post post) {
        postDao.save(post);
    }

    public void updatePost(Post post) {
        postDao.update(post);
    }

    public void deletePost(Long id) {
        postDao.delete(id);
    }

    public void likePost(Long id) {
        postDao.incrementLikes(id);
    }
    public void disLikePost(Long id) {
        postDao.decrementLikes(id);
    }

    public String processPost(Post post, String title, String text, String tags, MultipartFile image, Model model) {
        try {
            // Валидация
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

            post.setTitle(title.trim());
            post.setContent(text.trim());

            // Обработка тегов
            if (tags != null && !tags.trim().isEmpty()) {
                List<Tag> tagList = tagService.processTags(tags.trim());
                if (tagList == null) {
                    model.addAttribute("error", "Each tag must be 50 characters or less");
                    model.addAttribute("post", post);
                    return "add-post";
                }
                post.setTags(tagList);
            } else {
                post.setTags(new ArrayList<>());
            }

            // Обработка изображения
            if (image != null && !image.isEmpty()) {
                String contentType = image.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    model.addAttribute("error", "Only image files are allowed");
                    model.addAttribute("post", post);
                    return "add-post";
                }

                String fileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(image.getOriginalFilename());
                Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path targetLocation = uploadPath.resolve(fileName);
                image.transferTo(targetLocation);
                post.setImageUrl("/uploads/images/" + fileName);
            }

            return null; // Успешная обработка
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload image: " + e.getMessage());
            model.addAttribute("post", post);
            return "add-post";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to process post: " + e.getMessage());
            model.addAttribute("post", post);
            return "add-post";
        }
    }
}
