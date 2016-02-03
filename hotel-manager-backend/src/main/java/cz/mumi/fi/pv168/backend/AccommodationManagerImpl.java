package cz.mumi.fi.pv168.backend;

import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import sun.rmi.runtime.Log;

import javax.sql.DataSource;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.slf4j.Logger;

/**
 * Created by Filip on 7.3.14.
 */
public class AccommodationManagerImpl implements AccommodationManager {

    private Logger log = LoggerFactory.getLogger(AccommodationManager.class);
    private JdbcTemplate jdbc;
    private GuestManager guestManager;
    private RoomManager roomManager;

    public AccommodationManagerImpl(DataSource dataSource) {
        jdbc = new JdbcTemplate(dataSource);
    }

    public void setGuestManager(GuestManager guestManager) {
        this.guestManager = guestManager;
    }

    public void setRoomManager(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @Override
    public Accommodation createAccommodation(Accommodation accommodation) {
        if (accommodation == null) {
            String msg = "Accommodation is null";
            log.error(msg);
            throw new NullPointerException(msg);
        }
        if (accommodation.getId() != null) {
            String msg = "Accommodation id is already set";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        SimpleJdbcInsert insertAccommodation = new SimpleJdbcInsert(jdbc).withTableName("accommodations").usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("guestId", accommodation.getGuest().getId())
                .addValue("roomId", accommodation.getRoom().getId())
                .addValue("checkIn", new Date(accommodation.getCheckIn().getTimeInMillis()))
                .addValue("checkOut", new Date(accommodation.getCheckOut().getTimeInMillis()));
        try {
            Number id = insertAccommodation.executeAndReturnKey(parameters);
            accommodation.setId(id.longValue());
        } catch (DataAccessException ex) {
            String msg = "Error while creating accommodation";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
        return accommodation;
    }

    @Override
    public void deleteAccommodation(Accommodation accommodation) {
        if (accommodation == null) {
            String msg = "Accommodation is null";
            log.error(msg);
            throw new NullPointerException(msg);
        }
        if (accommodation.getId() == null) {
            String msg = "Accommodation id is null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        try {
            jdbc.update("DELETE FROM accommodations WHERE id=?", accommodation.getId());
        } catch (DataAccessException ex) {
            String msg = "Error while removing accommodation";
            log.error(msg,ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public void updateAccommodation(Accommodation accommodation) {
        if (accommodation == null) {
            String msg = "Accommodation is null";
            log.error(msg);
            throw new NullPointerException(msg);
        }
        if (accommodation.getId() == null) {
            String msg = "Accommodation id is null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        try {
        jdbc.update("UPDATE accommodations SET guestId=?, roomId=?, checkIn=?, checkOut=? WHERE id=?",
                accommodation.getGuest().getId(), accommodation.getRoom().getId(),
                new Date(accommodation.getCheckIn().getTimeInMillis()), new Date(accommodation.getCheckOut().getTimeInMillis()), accommodation.getId());
        } catch (DataAccessException ex) {
            String msg = "Error while updating accommodation";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    private RowMapper<Accommodation> accommodationMapper = (rs, rowNum) ->
    {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(rs.getLong("id"));

        Guest guest = null;
        try {
            guest = guestManager.findGuestById(rs.getLong("guestId"));
        } catch (DataAccessException ex) {
            String msg = "Error while retrieving guest";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
        accommodation.setGuest(guest);

        Room room = null;
        try {
            room = roomManager.findRoomById(rs.getLong("roomId"));
        } catch (DataAccessException ex) {
            String msg = "Error while retrieving room";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
        accommodation.setRoom(room);

        Calendar c = new GregorianCalendar();
        c.setTime(rs.getDate("checkIn"));
        accommodation.setCheckIn(c);
        c = new GregorianCalendar();
        c.setTime(rs.getDate("checkOut"));
        accommodation.setCheckOut(c);
        return accommodation;
    };

    @Override
    public Accommodation findAccommodationById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        try {
            return jdbc.queryForObject("SELECT * FROM accommodations WHERE id=?", accommodationMapper, id);
        } catch (EmptyResultDataAccessException ex) {
            String msg = "No such accommodation";
            log.warn(msg);
            return null;
        } catch (DataAccessException ex) {
            String msg = "Error while retrieving accommodation";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public List<Accommodation> findAllAccommodations() {
        try {
            return jdbc.query("SELECT * FROM accommodations", accommodationMapper);
        } catch (DataAccessException ex) {
            String msg = "Error while retrieving accommodations";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public List<Accommodation> findAllAccommodationsForRoom(Room room) {
        try {
            return jdbc.query("SELECT * FROM accommodations WHERE roomId=?", accommodationMapper, room.getId());
        } catch (DataAccessException ex) {
            String msg = "Error while retrieving accommodations";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public List<Accommodation> findAllAccommodationsForGuest(Guest guest) {
        try {
            return jdbc.query("SELECT * FROM accommodations WHERE guestId=?", accommodationMapper, guest.getId());
        } catch (DataAccessException ex) {
            String msg = "Error while retrieving accommodations";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }
}
