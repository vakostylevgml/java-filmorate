package ru.yandex.practicum.filmorate.dal.user;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.event.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .id(rs.getInt("id"))
                .userId(rs.getInt("user_id"))
                .timestamp(rs.getTimestamp("e_timestamp"))
                .entityId(rs.getInt("entity_id"))
                .operation(rs.getString("operation"))
                .eventType(rs.getString("type")).build();
    }
}