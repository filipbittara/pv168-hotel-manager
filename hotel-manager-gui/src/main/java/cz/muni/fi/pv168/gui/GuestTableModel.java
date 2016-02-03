package cz.muni.fi.pv168.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.mumi.fi.pv168.backend.Guest;
import cz.mumi.fi.pv168.backend.GuestManager;
import cz.mumi.fi.pv168.backend.ServiceFailureException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.ResourceBundle;


/**
 * Created by Filip on 13.5.2014.
 */
public class GuestTableModel extends DefaultTableModel {


    private final GuestManager guestManager;
    private List<Guest> guests;
    private final static Logger log = LoggerFactory.getLogger(GuestTableModel.class);

    public GuestTableModel(GuestManager guestManager) {
        this.guestManager = guestManager;
        try {
            guests = guestManager.findAllGuest();
        } catch (ServiceFailureException ex) {
            log.error("Cannot read guests", ex);
        }
    }

    @Override
    public boolean isCellEditable(int row, int column)
    {
        return false;
    }

    @Override
    public int getRowCount() {
        return guests != null ? guests.size() : 0;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    static ResourceBundle texts = ResourceBundle.getBundle("Texts");

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return texts.getString("words_full_name");
            case 1:
                return texts.getString("words_passport_number");
            case 2:
                return texts.getString("words_credit_card_number");
            case 3:
                return texts.getString("shortcut_VIP");
            default:
                String msg = "No column number " + column;
                log.error(msg);
                throw new IllegalArgumentException(msg);
        }
    }

    public int getRowForGuest(Guest guest)
    {
        return guests.indexOf(guest);
    }

    @Override
    public Object getValueAt(int row, int column) {
        Guest guest = guests.get(row);
        switch (column) {
            case 0:
                return guest.getFullName();
            case 1:
                return guest.getPassportNumber();
            case 2:
                return guest.getCreditCardNumber();
            case 3:
                if(guest.getVipCustomer())
                    return texts.getString("word_yes");
                else
                    return texts.getString("word_no");
            default:
                String msg = "No column number " + column;
                log.error(msg);
                throw new IllegalArgumentException(msg);
        }
    }

    public Guest getGuestForRow(int row) {
        return guests.get(row);
    }

    public void editGuest(Guest guest){

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    guestManager.updateGuest(guest);
                    guests = guestManager.findAllGuest();
                    fireTableDataChanged();
                } catch (ServiceFailureException ex) {
                    log.warn("Cannot edit guest " + guest, ex);
                }
                return null;
            }

            @Override
            protected void done() {
                fireTableDataChanged();
            }
        }.execute();

    }

    public void addGuest(Guest guest) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    guestManager.createGuest(guest);
                    guests = guestManager.findAllGuest();
                    fireTableDataChanged();
                } catch (ServiceFailureException ex) {
                    log.warn("Cannot add guest " + guest, ex);
                }
                return null;
            }

            @Override
            protected void done() {
                fireTableDataChanged();
            }
        }.execute();
    }

    public void deleteGuestForRow(int row) {
        Guest guest = guests.get(row);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    guestManager.deleteGuest(guest);
                    guests = guestManager.findAllGuest();
                } catch (ServiceFailureException ex) {
                    log.warn("Cannot delete guest " + guest, ex);
                }
                return null;
            }

            @Override
            protected void done() {
                fireTableDataChanged();
            }
        }.execute();
    }
}