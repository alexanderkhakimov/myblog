package com.myblog.dao;

import com.myblog.model.Post;
import com.myblog.model.Tag;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PostDaoImpl implements PostDao {
    private final JdbcTemplate jdbcTemplate;

    public PostDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Post> findAll(int page, int size, String tag) {
        String sql = "SELECT p.*, t.id AS tag_id, t.name AS tag_name FROM posts p " +
                "LEFT JOIN post_tags pt ON p.id = pt.post_id " +
                "LEFT JOIN tags t ON pt.tag_id = t.id";
        if (tag != null && !tag.isEmpty()) {
            sql += " WHERE t.name = ?";
        }
        sql += " ORDER BY p.created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, (tag != null && !tag.isEmpty()) ? new Object[]{tag, size, (page - 1) * size} : new Object[]{size, (page - 1) * size}, this::mapRowToPost);
    }

    @Override
    public Post findById(Long id) {
        String sql = "SELECT p.*, t.id AS tag_id, t.name AS tag_name FROM posts p " +
                "LEFT JOIN post_tags pt ON p.id = ps.post_id " +
                "LEFT JOIN tags t ON pt.tag_id = t,id WHERE p.id = ?";
        List<Post> posts = jdbcTemplate.query(sql, new Object[]{id}, this::mapRowToPost);

        return posts.isEmpty() ? null : posts.get(0);
    }

    @Override
    public void save(Post post) {
        String sql = " INSERT INTO posts (title, image_url,content) VALUES (?,?,?)";
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        post.setId(id);
        saveTags(post);
    }

    private void saveTags(Post post) {
        if (post.getTags() != null) {
            for (Tag tag : post.getTags()) {
                Long tagId = jdbcTemplate.queryForObject("SELECT id FROM tags WHERE name = ?", Long.class, tag.getName());
                if (tagId == null) {
                    jdbcTemplate.update("INSERT INTO tags (name) VALUES (?)", tag.getName());
                    tagId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
                }
                jdbcTemplate.update("INSERT INTO post_tags (post_id, tag_id) VALUES (?,?)", post.getId(), tagId);
            }
        }
    }

    @Override
    public void update(Post post) {
        String sql = "UPDATE posts SET title = ?, image_url = ?, content = ? WHERE id = ?";
        jdbcTemplate.update(sql, post.getTitle(), post.getImageUrl(), post.getContent());
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
