package cz.mumi.fi.pv168.backend;

import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;


import javax.sql.DataSource;
import java.util.List;
import org.slf4j.Logger;

/**
 * Created by Filip on 7.3.14.
 */
public class RoomManagerImpl implements RoomManager {

    private final static Logger log = LoggerFactory.getLogger(RoomManager.class);

    private JdbcTemplate jdbc;

    public RoomManagerImpl(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    @Override
    public Room createRoom(Room room) {
        if (room == null) {
            String msg = "Room is null";             
            log.error(msg);             
            throw new NullPointerException(msg);
        }
        if (room.getId() != null) {
            String msg = "Room id is already set";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        SimpleJdbcInsert insertRoom = new SimpleJdbcInsert(jdbc)
                .withTableName("rooms").usingGeneratedKeyColumns("id");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("number", room.getNumber())
                .addValue("roomType", room.getType().ordinal())
                .addValue("capacity", room.getCapacity())
                .addValue("note", room.getNote());
        try {
            Number id = insertRoom.executeAndReturnKey(parameters);
            room.setId(id.longValue());
        } catch (DataAccessException ex) {
            String msg = "Error while creating room";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
        return room;
    }

    @Override
    public void deleteRoom(Room room) {
        if (room == null) {
            String msg = "Room is null";             
            log.error(msg);             
            throw new NullPointerException(msg);
        }
        if (room.getId() == null) {
            String msg = "Room id is null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        try {
            jdbc.update("DELETE FROM rooms WHERE id=?", room.getId());
        } catch (DataAccessException ex) {
            String msg = "Error while removing room";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public void updateRoom(Room room) {
        if (room == null) {
            String msg = "Room is null";             
            log.error(msg);             
            throw new NullPointerException(msg);
        }
        if (room.getId() == null) {
            String msg = "Room id is null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        try {
            jdbc.update("UPDATE rooms SET number=?, roomType=?, capacity=?, note=? WHERE id=?",
                room.getNumber(),room.getType().ordinal(),room.getCapacity(),room.getNote(),room.getId());
        } catch (DataAccessException ex) {
            String msg = "Error while updating room";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    private RowMapper<Room> roomMapper = (rs, rowNum) ->
    {
        Room room = new Room();
        room.setId(rs.getLong("id"));
        room.setNumber(rs.getInt("number"));
        room.setType(RoomType.values()[rs.getInt("roomType")]);
        room.setCapacity(rs.getInt("capacity"));
        room.setNote(rs.getString("note"));
        return room;
    };

    @Override
    public Room findRoomById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        try {
            return jdbc.queryForObject("SELECT * FROM rooms WHERE id=?", roomMapper, id);
        } catch (EmptyResultDataAccessException ex) {
            String msg = "No such room";
            log.warn(msg, ex);
            return null;
        } catch (DataAccessException ex) {
            String msg = "Error while retrieving room";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public List<Room> findAllRooms() {
        try {
            return jdbc.query("SELECT * FROM rooms", roomMapper);
        } catch (DataAccessException ex) {
            String msg = "Error while retrieving rooms";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }
}
