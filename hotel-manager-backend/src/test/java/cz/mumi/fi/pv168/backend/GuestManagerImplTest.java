package cz.mumi.fi.pv168.backend;

import cz.mumi.fi.pv168.backend.GuestManagerImpl;
import cz.mumi.fi.pv168.backend.Guest;
import org.apache.derby.jdbc.ClientDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import static org.junit.Assert.assertEquals;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;

import java.util.*;

import static org.junit.Assert.*;


/**
 * Created by Filip on 12.3.14.
 */
public class GuestManagerImplTest {
    public static final String TEST_NAME = "Fero Omacka";
    public static final String TEST_PASSPORT = "PO288610";
    public static final String TEST_CARD = "4485524475297862";
    public static final String TEST_NAME2 = "Jozef Mrkvicka";
    public static final String TEST_PASSPORT2 = "L2068491";
    public static final String TEST_CARD2 = "4485479979819986";

    private EmbeddedDatabase db;
    private GuestManagerImpl manager;

    @Before
    public void setUp() throws Exception {
        db = new EmbeddedDatabaseBuilder().setType(DERBY).addScript("myschema.sql").build();
        manager = new GuestManagerImpl(db);
    }

    @After
    public void tearDown() throws Exception {
        db.shutdown();
    }

    @Test
    public void createGuest() {
        Guest guest = newGuest(TEST_NAME, TEST_PASSPORT, TEST_CARD, true);
        manager.createGuest(guest);

        Long guestId = guest.getId();
        Guest result = manager.findGuestById(guestId);
        assertEquals(guest, result);
    }

    @Test
    public void createNullGuest() {
        try {
            manager.createGuest(null);
            fail("Did not threw NullPointerException");
        } catch (NullPointerException ex) {
        }
    }

    @Test
    public void updateGuest() {
        Guest guest = newGuest(TEST_NAME, TEST_PASSPORT, TEST_CARD, true);
        manager.createGuest(guest);

        Long guestId = guest.getId();
        guest.setFullName(TEST_NAME2);
        guest.setPassportNumber(TEST_PASSPORT2);
        guest.setCreditCardNumber(TEST_CARD2);
        guest.setVipCustomer(false);
        manager.updateGuest(guest);

        Guest result = manager.findGuestById(guestId);
        assertEquals(TEST_NAME2,result.getFullName());
        assertEquals(TEST_PASSPORT2,result.getPassportNumber());
        assertEquals(TEST_CARD2,result.getCreditCardNumber());
        assertFalse(result.getVipCustomer());
    }

    @Test
    public  void  deleteGuest() {
        Guest guest = newGuest(TEST_NAME, TEST_PASSPORT, TEST_CARD, true);
        manager.createGuest(guest);

        Long guestId = guest.getId();
        manager.deleteGuest(guest);
        Guest result = manager.findGuestById(guestId);
        assertNull(result);
    }

    @Test
    public void testFindAllGuests() throws Exception {
        assertTrue(manager.findAllGuest().isEmpty());

        Guest guest = newGuest(TEST_NAME, TEST_PASSPORT, TEST_CARD, true);
        Guest guest2 = newGuest(TEST_NAME2, TEST_PASSPORT2, TEST_CARD2, false);

        manager.createGuest(guest);
        manager.createGuest(guest2);

        List<Guest> expected = Arrays.asList(guest, guest2);
        List<Guest> actual = manager.findAllGuest();

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

    private static Comparator<Guest> idComparator = new Comparator<Guest>() {

        @Override
        public int compare(Guest o1, Guest o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };
}
