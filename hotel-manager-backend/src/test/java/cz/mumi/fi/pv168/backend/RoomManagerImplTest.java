package cz.mumi.fi.pv168.backend;

import cz.mumi.fi.pv168.backend.RoomType;
import cz.mumi.fi.pv168.backend.RoomManagerImpl;
import cz.mumi.fi.pv168.backend.Room;
import junit.framework.TestCase;
import org.apache.derby.jdbc.ClientDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import static org.junit.Assert.*;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;

import java.util.*;

/**
 * Created by MANEOR on 11.3.14.
 */
public class RoomManagerImplTest {

    public static final int TEST_NUMBER = 1;
    public static final int TEST_CAPACITY = 4;
    public static final int TEST_NUMBER2 = 2;
    public static final int TEST_CAPACITY2 = 8;
    private EmbeddedDatabase db;
    private RoomManagerImpl manager;

    @Before
    public void setUp() throws Exception {
        db = new EmbeddedDatabaseBuilder().setType(DERBY).addScript("myschema.sql").build();
        manager = new RoomManagerImpl(db);
    }

    @After
    public void tearDown() throws Exception {
        db.shutdown();
    }


    @Test
    public void testCreateRoom() throws Exception {
        Room room = newRoom(TEST_NUMBER, RoomType.DELUXE, TEST_CAPACITY, "Nice Room");
        manager.createRoom(room);

        Long roomId = room.getId();
        assertNotNull("ID is null", roomId);

        Room result = manager.findRoomById(roomId);
        assertEquals(room, result);
    }

    @Test
    public void testDeleteRoom() throws Exception {
        Room room = newRoom(TEST_NUMBER, RoomType.DELUXE, TEST_CAPACITY, "Nice Room");
        manager.createRoom(room);

        Long roomId = room.getId();
        manager.deleteRoom(room);
        Room result = manager.findRoomById(roomId);

        assertNull(result);
    }

    @Test
    public void updateRoom() throws  Exception {
        Room room = newRoom(TEST_NUMBER, RoomType.DELUXE, TEST_CAPACITY, "Nice Room");
        manager.createRoom(room);

        Long roomId = room.getId();
        room.setNumber(TEST_NUMBER2);
        room.setType(RoomType.SUITE);
        room.setCapacity(TEST_CAPACITY2);
        room.setNote(null);
        manager.updateRoom(room);

        Room result = manager.findRoomById(roomId);
        assertEquals(TEST_NUMBER2,result.getNumber());
        assertEquals(RoomType.SUITE,result.getType());
        assertEquals(TEST_CAPACITY2,result.getCapacity());
        assertNull(result.getNote());
    }


    @Test
    public void testFindAllRooms() throws Exception {

        assertTrue(manager.findAllRooms().isEmpty());

        Room room1 = newRoom(TEST_NUMBER, RoomType.DELUXE, TEST_CAPACITY, "Nice Room");
        Room room2 = newRoom(TEST_NUMBER2, RoomType.STANDARD, TEST_CAPACITY2, "Standard Room");

        manager.createRoom(room1);
        manager.createRoom(room2);

        List<Room> expected = Arrays.asList(room1, room2);
        List<Room> actual = manager.findAllRooms();

        Collections.sort(actual, idComparator);
        Collections.sort(expected,idComparator);

        assertEquals(expected, actual);

    }

    private static Room newRoom(int number, RoomType type, int capacity, String note) {
        Room room = new Room();
        room.setCapacity(capacity);
        room.setNumber(number);
        room.setType(type);
        room.setNote(note);
        return room;
    }

    private static Comparator<Room> idComparator = new Comparator<Room>() {

        @Override
        public int compare(Room o1, Room o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };
}
