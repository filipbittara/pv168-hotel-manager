package cz.muni.fi.pv168.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.mumi.fi.pv168.backend.Accommodation;
import cz.mumi.fi.pv168.backend.AccommodationManager;
import cz.mumi.fi.pv168.backend.ServiceFailureException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Filip on 13.5.2014.
 */
public class AccommodationTableModel extends DefaultTableModel {

    private final AccommodationManager accommodationManager;
    private List<Accommodation> accommodations;
    private final static Logger log = LoggerFactory.getLogger(AccommodationTableModel.class);

    public AccommodationTableModel(AccommodationManager accommodationManager) {
        this.accommodationManager = accommodationManager;
        try {
            accommodations = accommodationManager.findAllAccommodations();
        } catch (ServiceFailureException ex) {
            log.error("Cannot read accommodations", ex);
        }
    }

    @Override
    public int getRowCount() {
        return accommodations != null ? accommodations.size() : 0;
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
                return texts.getString("word_guest");
            case 1:
                return texts.getString("word_room");
            case 2:
                return texts.getString("words_check_in");
            case 3:
                return texts.getString("words_check_out");
            default:
                String msg = "No column number " + column;
                log.error(msg);
                throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Accommodation accommodation = accommodations.get(row);
        switch (column) {
            case 0:
                return accommodation.getGuest();
            case 1:
                return accommodation.getRoom();
            case 2:
                GregorianCalendar date = (GregorianCalendar)accommodation.getCheckIn();
                return date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DAY_OF_MONTH);
            case 3:
                GregorianCalendar date2 = (GregorianCalendar)accommodation.getCheckOut();
                return date2.get(Calendar.YEAR) + "-" + (date2.get(Calendar.MONTH) + 1) + "-" + date2.get(Calendar.DAY_OF_MONTH);
            default:
                String msg = "No column number " + column;
                log.error(msg);
                throw new IllegalArgumentException(msg);
        }
    }

    public Accommodation getAccommodationForRow(int row) {
        return accommodations.get(row);
    }

    @Override
    public boolean isCellEditable(int row, int column)
    {
        return false;
    }

    public void editAccommodation(Accommodation accommodation){
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    accommodationManager.updateAccommodation(accommodation);
                    accommodations = accommodationManager.findAllAccommodations();
                } catch (ServiceFailureException ex) {
                    log.warn("Cannot edit accommodation " + accommodation, ex);
                }
                return null;
            }

            @Override
            protected void done() {
                fireTableDataChanged();
            }
        }.execute();
    }

    public void addAccommodation(Accommodation accommodation) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    accommodationManager.createAccommodation(accommodation);
                    accommodations = accommodationManager.findAllAccommodations();
                    fireTableDataChanged();
                } catch (ServiceFailureException ex) {
                    log.warn("Cannot add accommodation " + accommodation, ex);
                }
                return null;
            }

            @Override
            protected void done() {
                fireTableDataChanged();
            }
        }.execute();
    }

    public void deleteAccommodationForRow(int row) {
        Accommodation accommodation = accommodations.get(row);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    accommodationManager.deleteAccommodation(accommodation);
                    accommodations = accommodationManager.findAllAccommodations();
                } catch (ServiceFailureException e) {
                    log.warn("Cannot delete accommodation " + accommodation, e);
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