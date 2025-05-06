package com.myblog.dao;

import com.myblog.model.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CommentDaoImpl implements CommentDao {
    private final JdbcTemplate jdbcTemplate;

    public CommentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Comment> findByPostId(Long postId) {
        String sql = "SELECT * FROM comments WHERE post_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new Object[]{postId}, this::mapRowToComment);
    }


    @Override
    public void save(Comment comment) {
        String sql = "INSERT INTO comments (post_id,content) VALUES (?, ?)";
        jdbcTemplate.update(sql, comment.getPostId(), comment.getContent());
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        comment.setId(id);

    }

    @Override
    public void update(Comment comment) {
        String sql = "UPDATE comments SET content = ? WHERE id = ?";
        jdbcTemplate.update(sql,comment.getContent(),comment.getId());
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        jdbcTemplate.update(sql,id);
    }

    private Comment mapRowToComment(ResultSet resultSet, int rowNum) throws SQLException {
        Comment comment = new Comment();
        comment.setId(resultSet.getLong("id"));
        comment.setPostId(resultSet.getLong("post_id"));
        comment.setContent(resultSet.getString("content"));
        comment.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());

        return comment;
    }
}
