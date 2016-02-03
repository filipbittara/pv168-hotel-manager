package cz.mumi.fi.pv168.backend;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Filip on 7.3.14.
 */
public class GuestManagerImpl implements GuestManager {

    private final static Logger log = LoggerFactory.getLogger(GuestManagerImpl.class);
    private JdbcTemplate jdbc;

    public GuestManagerImpl(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    @Override
    public Guest createGuest(Guest guest) throws ServiceFailureException {
        if (guest == null) {
            String msg = "Guest is null";
            log.error(msg);
            throw new NullPointerException(msg);
        }
        if (guest.getId() != null) {
            String msg = "Guest id is already set";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        SimpleJdbcInsert insertGuest = new SimpleJdbcInsert(jdbc)
                .withTableName("guests").usingGeneratedKeyColumns("id");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("fullName", guest.getFullName())
                .addValue("creditCardNumber", guest.getCreditCardNumber())
                .addValue("passportNumber", guest.getPassportNumber())
                .addValue("vipCustomer", guest.getVipCustomer());
        try {
        Number id = insertGuest.executeAndReturnKey(parameters);
        guest.setId(id.longValue());
        } catch (DataAccessException ex) {
            String msg = "Error while creating guest";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
        return guest;
    }

    @Override
    public void deleteGuest(Guest guest) throws ServiceFailureException {
        if (guest == null) {
            String msg = "Guest is null";
            log.error(msg);
            throw new NullPointerException(msg);
        }
        if (guest.getId() == null) {
            String msg = "Guest id is null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        try {
            jdbc.update("DELETE FROM guests WHERE id=?", guest.getId());
        } catch (DataAccessException ex) {
            String msg = "Error while removing guest";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    @Override
    public void updateGuest(Guest guest) throws ServiceFailureException {
        if (guest == null) {
            String msg = "Guest is null";
            log.error(msg);
            throw new NullPointerException(msg);
        }
        if (guest.getId() == null) {
            String msg = "Guest id is null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        try {
            jdbc.update("UPDATE guests SET fullName=?, creditCardNumber=?, passportNumber=? ,vipCustomer=? WHERE id=?",
                guest.getFullName(), guest.getCreditCardNumber(), guest.getPassportNumber(), guest.getVipCustomer(), guest.getId());
        } catch (DataAccessException ex) {
            String msg = "Error while updating guest";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    private RowMapper<Guest> guestMapper = (rs, rowNum) ->
    {
        Guest guest = new Guest();
        guest.setId(rs.getLong("id"));
        guest.setFullName(rs.getString("fullName"));
        guest.setCreditCardNumber(rs.getString("creditCardNumber"));
        guest.setPassportNumber(rs.getString("passportNumber"));
        guest.setVipCustomer(rs.getBoolean("vipCustomer"));
        return guest;
    };

    @Override
    public Guest findGuestById(Long id) throws ServiceFailureException {
        if (id == null) {
            String msg = "Guest id is null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        try {
            return jdbc.queryForObject("SELECT * FROM guests WHERE id=?", guestMapper, id);
        } catch (EmptyResultDataAccessException ex) {
            String msg = "No such guest";
            log.warn(msg, ex);
            return null;
        } catch (DataAccessException ex) {
            String msg = "Error while retrieving guest";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }

    }

    @Override
    public List<Guest> findAllGuest() throws ServiceFailureException {
        try {
            return jdbc.query("SELECT * FROM guests", guestMapper);
        } catch (DataAccessException ex) {
            String msg = "Error while retrieving guests";
            log.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }
}
