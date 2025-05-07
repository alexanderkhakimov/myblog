package com.myblog.service;

import com.myblog.dao.PostDao;
import com.myblog.model.Post;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostDao postDao;

    public PostService(PostDao postDao) {
        this.postDao = postDao;
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
}
