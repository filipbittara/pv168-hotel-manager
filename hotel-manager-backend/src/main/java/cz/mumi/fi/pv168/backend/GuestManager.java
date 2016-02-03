package cz.mumi.fi.pv168.backend;

import java.util.List;

/**
 * Created by Filip on 7.3.14.
 */
public interface GuestManager {
    /**
     * Stores new guest into database. Id for the new guest is automatically
     * generated and stored into id attribute.
     *
     * @param guest guest to be created.
     * @throws NullPointerException when guest is null.
     * @throws IllegalArgumentException when guest has already assigned id.
     * @throws  ServiceFailureException when db operation fails.
     */
    public Guest createGuest(Guest guest) throws ServiceFailureException ;

    /**
     * Deletes guest from database.
     *
     * @param guest guest to be deleted from db.
     * @throws NullPointerException when guest is null.
     * @throws IllegalArgumentException when guest has null id.
     * @throws  ServiceFailureException when db operation fails.
     */
    public void deleteGuest(Guest guest) throws ServiceFailureException ;
    
    /**
     * Updates guest in database.
     *
     * @param guest updated guest to be stored into database.
     * @throws NullPointerException when guest is null.
     * @throws IllegalArgumentException when guest has null id.
     * @throws  ServiceFailureException when db operation fails.
     */
    public void updateGuest(Guest guest) throws ServiceFailureException ;

    /**
     * Returns guest with given id.
     *
     * @param id primary key of requested guest.
     * @return guest with given id or null if such guest does not exist.
     * @throws IllegalArgumentException when given id is null.
     * @throws  ServiceFailureException when db operation fails.
     */
    public Guest findGuestById(Long id) throws ServiceFailureException ;
    
    /**
     * Returns list of all guests in the database.
     *
     * @return list of all guests in database.
     * @throws  ServiceFailureException when db operation fails.
     */
    public List<Guest> findAllGuest() throws ServiceFailureException ;
}
