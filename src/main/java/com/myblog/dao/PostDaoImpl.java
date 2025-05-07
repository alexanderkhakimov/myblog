package com.myblog.dao;

import com.myblog.model.Post;
import com.myblog.model.Tag;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PostDaoImpl implements PostDao {
    private final JdbcTemplate jdbcTemplate;
    private final CommentDao commentDao;
    private final TagDao tagDao;

    public PostDaoImpl(JdbcTemplate jdbcTemplate, CommentDao commentDao, TagDao tagDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.commentDao = commentDao;
        this.tagDao = tagDao;
    }

    @Override
    public List<Post> findAll(int page, int size, String tag) {
        System.out.println("findAll called with page=" + page + ", size=" + size + ", tag=" + tag);

        String sql = "SELECT * FROM posts";
        if (tag != null && !tag.isEmpty()) {
            sql += " INNER JOIN post_tags pt ON posts.id = pt.post_id" +
                    " INNER JOIN tags t ON pt.tag_id = t.id WHERE t.name = ?";
        }
        sql += " ORDER BY created_at DESC LIMIT ? OFFSET ?";
        System.out.println("Executing SQL: " + sql);

        List<Post> posts = jdbcTemplate.query(
                sql,
                (tag != null && !tag.isEmpty()) ? new Object[]{tag, size, (page - 1) * size} : new Object[]{size, (page - 1) * size},
                this::mapRowToPost
        );

        System.out.println("Posts found: " + posts.size());
        for (Post post : posts) {
            System.out.println("Post ID: " + post.getId() + ", Title: " + post.getTitle() + ", Content: " + post.getContent());
            post.setTags(tagDao.findByPostId(post.getId()));
            post.setComments(commentDao.findByPostId(post.getId()));
            System.out.println("Comments loaded: " + post.getComments().size());
        }

        return posts;
    }

    @Override
    public Post findById(Long id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        List<Post> posts = jdbcTemplate.query(sql, new Object[]{id}, this::mapRowToPost);
        if (posts.isEmpty()) {
            return null;
        }
        Post post = posts.get(0);

        post.setTags(tagDao.findByPostId(id));
        post.setComments(commentDao.findByPostId(id));

        System.out.println("Post ID: " + post.getId() + ", Title: " + post.getTitle() + ", Content: " + post.getContent() + ", Comments: " + post.getComments().size());

        return post;
    }

    @Override
    public void save(Post post) {
        String sql = "INSERT INTO posts (title, image_url, content, created_at) VALUES (?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getImageUrl());
            ps.setString(3, post.getContent());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(post.getCreatedAt() != null ? post.getCreatedAt() : java.time.LocalDateTime.now()));
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKey().longValue();
        post.setId(id);
        saveTags(post);
    }

    private void saveTags(Post post) {
        if (post.getTags() != null && !post.getTags().isEmpty()) {
            for (Tag tag : post.getTags()) {
                if (tag.getName() == null || tag.getName().trim().isEmpty()) {
                    continue;
                }
                Tag existingTag = tagDao.findByName(tag.getName());
                Long tagId;
                if (existingTag == null) {
                    tagDao.save(tag);
                    tagId = tag.getId();
                } else {
                    tagId = existingTag.getId();
                }
                if (tagId != null) { // Убедимся, что tagId не null
                    jdbcTemplate.update("INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)", post.getId(), tagId);
                } else {
                    System.err.println("Failed to get tag ID for tag: " + tag.getName());
                }
            }
        }
    }

    @Override
    public void update(Post post) {
        String sql = "UPDATE posts SET title = ?, image_url = ?, content = ? WHERE id = ?";
        jdbcTemplate.update(sql, post.getTitle(), post.getImageUrl(), post.getContent(), post.getId());
        jdbcTemplate.update("DELETE FROM post_tags WHERE post_id = ?", post.getId());
        saveTags(post);
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM posts WHERE id = ?", id);
    }

    @Override
    public void incrementLikes(Long id) {
        jdbcTemplate.update("UPDATE posts SET likes = likes + 1 WHERE id = ?", id);
    }
    @Override
    public void decrementLikes(Long id) {
        jdbcTemplate.update("UPDATE posts SET likes = likes - 1 WHERE id = ?", id);
    }

    private Post mapRowToPost(ResultSet resultSet, int rowNum) throws SQLException {
        Post post = new Post();
        post.setId(resultSet.getLong("id"));
        post.setTitle(resultSet.getString("title"));
        post.setImageUrl(resultSet.getString("image_url"));
        post.setContent(resultSet.getString("content"));
        post.setLikes(resultSet.getInt("likes"));
        post.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        return post;
    }
}