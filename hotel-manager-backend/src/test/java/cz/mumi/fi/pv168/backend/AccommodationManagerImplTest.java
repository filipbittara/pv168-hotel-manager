package cz.mumi.fi.pv168.backend;

import cz.mumi.fi.pv168.backend.Accommodation;
import cz.mumi.fi.pv168.backend.RoomType;
import cz.mumi.fi.pv168.backend.GuestManagerImpl;
import cz.mumi.fi.pv168.backend.AccommodationManagerImpl;
import cz.mumi.fi.pv168.backend.RoomManagerImpl;
import cz.mumi.fi.pv168.backend.Guest;
import cz.mumi.fi.pv168.backend.Room;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import java.util.*;

import static org.junit.Assert.*;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;

/**
 * Created by Filip on 1.4.14.
 */
public class AccommodationManagerImplTest {

    public static final String TEST_NAME = "Fero Omacka";
    public static final String TEST_PASSPORT = "PO288610";
    public static final String TEST_CARD = "4485524475297862";
    public static final int TEST_NUMBER = 1;
    public static final int TEST_NUMBER2 = 2;
    public static final int TEST_CAPACITY = 4;
    public static final int TEST_CAPACITY2 = 3;
    public static final String TEST_NAME2 = "Kadli Kazimir";
    public static final String TEST_PASSPORT2 = "PO153087";
    public static final String TEST_CARD2 = "6536328245671123";

    private EmbeddedDatabase db;
    private AccommodationManagerImpl accommodationManager;
    private GuestManagerImpl guestManager;
    private RoomManagerImpl roomManager;


    @Before
    public void setUp() throws Exception {
        db = new EmbeddedDatabaseBuilder().setType(DERBY).addScript("myschema.sql").build();
        accommodationManager = new AccommodationManagerImpl(db);
        guestManager = new GuestManagerImpl(db);
        roomManager = new RoomManagerImpl(db);
        accommodationManager.setGuestManager(guestManager);
        accommodationManager.setRoomManager(roomManager);
    }

    @After
    public void tearDown() throws Exception {
        db.shutdown();
    }

    @Test
    public void testCreateAccommodation() throws Exception {
        Room room = newRoom(TEST_NUMBER, RoomType.DELUXE, TEST_CAPACITY, "Nice Room");
        Guest guest = newGuest(TEST_NAME, TEST_PASSPORT, TEST_CARD, true);

        Accommodation accommodation = newAccommodation(guestManager.createGuest(guest), roomManager.createRoom(room),
                Calendar.getInstance(), Calendar.getInstance());
        accommodationManager.createAccommodation(accommodation);

        Long accommodationId = accommodation.getId();
        assertNotNull(accommodationId);

        Accommodation result = accommodationManager.findAccommodationById(accommodationId);
        assertEquals(accommodation, result);
    }

    @Test
    public void testDeleteAccommodation() throws Exception {
        Room room = newRoom(TEST_NUMBER, RoomType.DELUXE, TEST_CAPACITY, "Nice Room");
        Guest guest = newGuest(TEST_NAME, TEST_PASSPORT, TEST_CARD, true);

        Accommodation accommodation = newAccommodation(guestManager.createGuest(guest), roomManager.createRoom(room),
                Calendar.getInstance(), Calendar.getInstance());
        accommodationManager.createAccommodation(accommodation);

        Long accommodationId = accommodation.getId();
        accommodationManager.deleteAccommodation(accommodation);
        Accommodation result = accommodationManager.findAccommodationById(accommodationId);

        assertNull(result);
    }

    @Test
    public void testUpdateAccommodation() throws Exception {
        Room room = newRoom(TEST_NUMBER, RoomType.DELUXE, TEST_CAPACITY, "Nice Room");
        Guest guest = newGuest(TEST_NAME, TEST_PASSPORT, TEST_CARD, true);
        Room room2 = newRoom(TEST_NUMBER2, RoomType.STANDARD, TEST_CAPACITY2, "Poor Room");
        Guest guest2 = newGuest(TEST_NAME2, TEST_PASSPORT2, TEST_CARD2, false);

        Accommodation accommodation = newAccommodation(guestManager.createGuest(guest), roomManager.createRoom(room),
                Calendar.getInstance(), Calendar.getInstance());
        accommodationManager.createAccommodation(accommodation);
        Long accommodationId = accommodation.getId();

        accommodation.setGuest(guestManager.createGuest(guest2));
        accommodation.setRoom(roomManager.createRoom(room2));

        accommodationManager.updateAccommodation(accommodation);
        Accommodation result = accommodationManager.findAccommodationById(accommodationId);

        assertEquals(guest2, result.getGuest());
        assertEquals(room2, result.getRoom());
    }

    @Test
    public void testFindAllAccommodations() throws Exception {
        assertTrue(accommodationManager.findAllAccommodations().isEmpty());
        Room room = newRoom(TEST_NUMBER, RoomType.DELUXE, TEST_CAPACITY, "Nice Room");
        Guest guest = newGuest(TEST_NAME, TEST_PASSPORT, TEST_CARD, true);
        Room room2 = newRoom(TEST_NUMBER, RoomType.STANDARD, TEST_CAPACITY2, "Poor Room");
        Guest guest2 = newGuest(TEST_NAME2, TEST_PASSPORT2, TEST_CARD2, false);

        Accommodation accommodation1 = newAccommodation(guestManager.createGuest(guest), roomManager.createRoom(room),
                Calendar.getInstance(), Calendar.getInstance());
        Accommodation accommodation2 = newAccommodation(guestManager.createGuest(guest2), roomManager.createRoom(room2),
                Calendar.getInstance(), Calendar.getInstance());

        accommodationManager.createAccommodation(accommodation1);
        accommodationManager.createAccommodation(accommodation2);

        List<Accommodation> expected = Arrays.asList(accommodation1,accommodation2);
        List<Accommodation> actual = accommodationManager.findAllAccommodations();

        Collections.sort(actual, idComparator);
        Collections.sort(expected,idComparator);

        assertEquals(expected, actual);
    }

    @Test
    public void testFindAllAccommodationsForRoom() throws Exception {
        assertTrue(accommodationManager.findAllAccommodations().isEmpty());
        Room room = newRoom(TEST_NUMBER, RoomType.DELUXE, TEST_CAPACITY, "Nice Room");
        Guest guest = newGuest(TEST_NAME, TEST_PASSPORT, TEST_CARD, true);
        Room room2 = newRoom(TEST_NUMBER2, RoomType.STANDARD, TEST_CAPACITY2, "Poor Room");
        Guest guest2 = newGuest(TEST_NAME2, TEST_PASSPORT2, TEST_CARD2, false);

        Accommodation accommodation1 = newAccommodation(guestManager.createGuest(guest), roomManager.createRoom(room),
                Calendar.getInstance(), Calendar.getInstance());
        Accommodation accommodation2 = newAccommodation(guestManager.createGuest(guest2), roomManager.createRoom(room2),
                Calendar.getInstance(), Calendar.getInstance());

        accommodationManager.createAccommodation(accommodation1);
        accommodationManager.createAccommodation(accommodation2);

        List<Accommodation> expected = Arrays.asList(accommodation1);
        List<Accommodation> actual = accommodationManager.findAllAccommodationsForGuest(guest);

        Collections.sort(actual, idComparator);
        Collections.sort(expected,idComparator);

        assertEquals(expected, actual);
    }

    @Test
    public void testFindAllAccommodationsForGuest() throws Exception {
        assertTrue(accommodationManager.findAllAccommodations().isEmpty());
        Room room = newRoom(TEST_NUMBER, RoomType.DELUXE, TEST_CAPACITY, "Nice Room");
        Guest guest = newGuest(TEST_NAME, TEST_PASSPORT, TEST_CARD, true);
        Room room2 = newRoom(TEST_NUMBER2, RoomType.STANDARD, TEST_CAPACITY, "Poor Room");
        Guest guest2 = newGuest(TEST_NAME2, TEST_PASSPORT2, TEST_CARD2, false);

        Accommodation accommodation1 = newAccommodation(guestManager.createGuest(guest), roomManager.createRoom(room),
                Calendar.getInstance(), Calendar.getInstance());
        Accommodation accommodation2 = newAccommodation(guestManager.createGuest(guest2), roomManager.createRoom(room2),
                Calendar.getInstance(), Calendar.getInstance());

        accommodationManager.createAccommodation(accommodation1);
        accommodationManager.createAccommodation(accommodation2);

        List<Accommodation> expected = Arrays.asList(accommodation1);
        List<Accommodation> actual = accommodationManager.findAllAccommodationsForRoom(room);

        Collections.sort(actual, idComparator);
        Collections.sort(expected,idComparator);

        assertEquals(expected, actual);

    }

    private static Guest newGuest(String fullName, String passportNumber, String creditCardNumber, boolean vipCustomer) {
        Guest guest = new Guest();
        guest.setFullName(fullName);
        guest.setPassportNumber(passportNumber);
        guest.setCreditCardNumber(creditCardNumber);
        guest.setVipCustomer(vipCustomer);
        return guest;
    }

    private static Room newRoom(int number, RoomType type, int capacity, String note) {
        Room room = new Room();
        room.setCapacity(capacity);
        room.setNumber(number);
        room.setType(type);
        room.setNote(note);
        return room;
    }

    private static Accommodation newAccommodation(Guest guest, Room room, Calendar checkIn, Calendar checkOut){
        Accommodation accommodation = new Accommodation();
        accommodation.setGuest(guest);
        accommodation.setRoom(room);
        accommodation.setCheckIn(checkIn);
        accommodation.setCheckOut(checkOut);
        return accommodation;
    }

    private static Comparator<Accommodation> idComparator = new Comparator<Accommodation>() {

        @Override
        public int compare(Accommodation o1, Accommodation o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };
}
