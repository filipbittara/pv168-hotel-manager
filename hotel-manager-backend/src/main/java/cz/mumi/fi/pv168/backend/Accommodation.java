package cz.mumi.fi.pv168.backend;

import java.util.Calendar;

/**
 * Created by MANEOR on 7.3.14.
 */
public class Accommodation {
    private Long id;
    private Guest guest;
    private Room room;
    private Calendar checkIn;
    private Calendar checkOut;

    public Accommodation() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Accommodation that = (Accommodation) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Calendar getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Calendar checkIn) {
        this.checkIn = checkIn;
    }

    public Calendar getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Calendar checkOut) {
        this.checkOut = checkOut;
    }

    @Override
    public String toString() {
        return "Accommodation{" +
                "id=" + id +
                ", guest=" + guest +
                ", room=" + room +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                '}';
    }
}
