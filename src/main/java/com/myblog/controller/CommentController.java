package com.myblog.controller;

import com.myblog.model.Comment;
import com.myblog.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public String addComment(@ModelAttribute Comment comment) {
        commentService.saveComment(comment);
        return "redirect:/posts/" + comment.getPostId();
    }

    @PostMapping("/{id}")
    public String updateComment(@PathVariable Long id, @ModelAttribute Comment comment) {
        comment.setId(id);
        commentService.updateComment(comment);

        return "redirect:/posts/" + comment.getPostId();
    }

    @PostMapping("/{id}/delete")
    public String deleteComment(@PathVariable Long id) {
        Comment comment = (Comment) commentService.getCommentById(id);
        commentService.deleteComment(id);

        return "redirect:/posts/" + comment.getPostId();

    }
}
