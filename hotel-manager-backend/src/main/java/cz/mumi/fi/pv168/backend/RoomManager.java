package cz.mumi.fi.pv168.backend;

import java.util.List;

/**
 * Created by Filip on 7.3.14.
 */
public interface RoomManager {
    /**
     * Stores new room into database. Id for the new room is automatically
     * generated and stored into id attribute.
     *
     * @param room room to be created.
     * @throws NullPointerException when room is null.
     * @throws IllegalArgumentException when room has already assigned id.
     * @throws  ServiceFailureException when db operation fails.
     */
    public Room createRoom(Room room);

    /**
     * Deletes room from database.
     *
     * @param room room to be deleted from db.
     * @throws NullPointerException when room is null.
     * @throws IllegalArgumentException when room has null id.
     * @throws  ServiceFailureException when db operation fails.
     */
    public void deleteRoom(Room room);

    /**
     * Updates room in database.
     *
     * @param room updated room to be stored into database.
     * @throws NullPointerException when room is null.
     * @throws IllegalArgumentException when room has null id.
     * @throws  ServiceFailureException when db operation fails.
     */
    public void updateRoom(Room room);

    /**
     * Returns room with given id.
     *
     * @param id primary key of requested room.
     * @return room with given id or null if such room does not exist.
     * @throws IllegalArgumentException when given id is null.
     * @throws  ServiceFailureException when db operation fails.
     */
    public Room findRoomById(Long id);

    /**
     * Returns list of all rooms in the database.
     *
     * @return list of all rooms in database.
     * @throws  ServiceFailureException when db operation fails.
     */
    public List<Room> findAllRooms();

}
