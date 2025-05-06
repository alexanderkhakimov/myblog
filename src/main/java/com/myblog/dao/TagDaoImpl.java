package com.myblog.dao;

import com.myblog.model.Tag;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class TagDaoImpl implements TagDao {
    private final JdbcTemplate jdbcTemplate;

    public TagDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Tag findByName(String name) {
        String sql = "SELECT * FROM tags WHERE name = ?";
        List<Tag> tags = jdbcTemplate.query(sql, new Object[]{name}, this::mapRowToTag);
        return tags.isEmpty() ? null : tags.get(0);
    }


    @Override
    public void save(Tag tag) {
        String sql = "INSERT INTO tags (name) VALUES (?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, tag.getName());
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKey().longValue();
        tag.setId(id);

    }

    @Override
    public List<Tag> findByPostId(Long postId) {
        String sql = "SELECT t.* FROM tags t JOIN post_tags pt ON t.id=pt.tag_id WHERE post_id = ?";
        return jdbcTemplate.query(sql, new Object[]{postId}, this::mapRowToTag);
    }

    private Tag mapRowToTag(ResultSet resultSet, int rowNum) throws SQLException {
        Tag tag = new Tag();
        tag.setId(resultSet.getLong("id"));
        tag.setName(resultSet.getString("name"));
        return tag;

    }
}
