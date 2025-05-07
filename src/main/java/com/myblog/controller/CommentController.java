package com.myblog.controller;

import com.myblog.model.Comment;
import com.myblog.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public String addComment(@RequestParam("postId") Long postId, @RequestParam("text") String text) {
        if (text == null || text.trim().isEmpty()) {
            return "redirect:/posts/" + postId + "?error=Comment content cannot be empty";
        }
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setContent(text.trim());
        commentService.saveComment(comment);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/{id}/edit")
    public String updateComment(@PathVariable(name = "id") Long id, @RequestParam("text") String text) {
        if (text == null || text.trim().isEmpty()) {
            Comment comment = commentService.getCommentById(id);
            if (comment == null) {
                return "redirect:/posts?error=Comment not found";
            }
            return "redirect:/posts/" + comment.getPostId() + "?error=Comment content cannot be empty";
        }
        Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            return "redirect:/posts?error=Comment not found";
        }
        comment.setContent(text.trim());
        commentService.updateComment(comment);
        return "redirect:/posts/" + comment.getPostId();
    }

    @PostMapping("/{id}/delete")
    public String deleteComment(@PathVariable(name = "id") Long id, @RequestParam(name = "postId") Long postId) {
        commentService.deleteComment(id);

        return "redirect:/posts/" + postId;

    }
}
