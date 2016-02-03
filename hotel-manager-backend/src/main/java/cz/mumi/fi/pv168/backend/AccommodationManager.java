package cz.mumi.fi.pv168.backend;

import java.util.List;

/**
 * Created by Filip on 7.3.14.
 */
public interface AccommodationManager {
    /**
     * Stores new accommodation into database. Id for the new accommodation is automatically
     * generated and stored into id attribute.
     *
     * @param accommodation accommodation to be created.
     * @throws NullPointerException when accommodation is null.
     * @throws IllegalArgumentException when accommodation has already assigned id.
     * @throws  ServiceFailureException when db operation fails.
     */
    public Accommodation createAccommodation(Accommodation accommodation);

    /**
     * Deletes accommodation from database.
     *
     * @param accommodation accommodation to be deleted from db.
     * @throws NullPointerException when accommodation is null.
     * @throws IllegalArgumentException when accommodation has null id.
     * @throws  ServiceFailureException when db operation fails.
     */
    public void deleteAccommodation(Accommodation accommodation);

    /**
     * Updates accommodation in database.
     *
     * @param accommodation updated accommodation to be stored into database.
     * @throws NullPointerException when accommodation is null.
     * @throws IllegalArgumentException when accommodation has null id.
     * @throws  ServiceFailureException when db operation fails.
     */
    public void updateAccommodation(Accommodation accommodation);

    /**
     * Returns accommodation with given id.
     *
     * @param id primary key of requested accommodation.
     * @return accommodation with given id or null if such accommodation does not exist.
     * @throws IllegalArgumentException when given id is null.
     * @throws  ServiceFailureException when db operation fails.
     */
    public Accommodation findAccommodationById(Long id);

    /**
     * Returns list of all accommodations in the database.
     *
     * @return list of all accommodations in database.
     * @throws  ServiceFailureException when db operation fails.
     */
    public List<Accommodation> findAllAccommodations();

    /**
     * Returns list of all accommodations in the database for given accommodation.
     *
     * @param room room to search accommodations for.
     * @return list of all accommodations in database.
     * @throws  ServiceFailureException when db operation fails.
     */
    public List<Accommodation> findAllAccommodationsForRoom(Room room);

    /**
     * Returns list of all accommodations in the database for given guest.
     *
     * @param guest guest to search accommodations for.
     * @return list of all accommodations in database.
     * @throws  ServiceFailureException when db operation fails.
     */
    public List<Accommodation> findAllAccommodationsForGuest(Guest guest);
}
