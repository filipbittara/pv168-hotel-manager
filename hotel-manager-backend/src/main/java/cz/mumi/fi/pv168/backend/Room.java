package cz.mumi.fi.pv168.backend;

/**
 * Created by MANEOR on 7.3.14.
 */
public class Room {
    private Long id;
    private int number;
    private RoomType type;
    private int capacity;
    private String note;

    public Room() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;

        if (capacity != room.capacity) return false;
        if (number != room.number) return false;
        if (note != null ? !note.equals(room.note) : room.note != null) return false;
        if (id != null ? !id.equals(room.id) : room.id != null) return false;
        if (type != room.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + number;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + capacity;
        result = 31 * result + (note != null ? note.hashCode() : 0);
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }
}