package cz.muni.fi.pv168.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.mumi.fi.pv168.backend.Room;
import cz.mumi.fi.pv168.backend.RoomManager;
import cz.mumi.fi.pv168.backend.ServiceFailureException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Filip on 13.5.2014.
 */
public class RoomTableModel extends DefaultTableModel {


    private final RoomManager roomManager;
    private List<Room> rooms;
    private final static Logger log = LoggerFactory.getLogger(RoomTableModel.class);

    public RoomTableModel(RoomManager roomManager) {
        this.roomManager = roomManager;
        try {
            rooms = roomManager.findAllRooms();
        } catch (ServiceFailureException e) {
            log.error("Cannot read rooms", e);
        }
    }

    @Override
    public int getRowCount() {
        return rooms != null ? rooms.size() : 0;
    }

    @Override
    public boolean isCellEditable(int row, int column)
    {
        return false;
    }

    public int getRowForRoom(Room room)
    {
        return rooms.indexOf(room);
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
                return texts.getString("word_number");
            case 1:
                return texts.getString("word_type");
            case 2:
                return texts.getString("word_capacity");
            case 3:
                return texts.getString("word_note");
            default:
                String msg = "No column number " + column;
                log.error(msg);
                throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Room room = rooms.get(row);
        switch (column) {
            case 0:
                return room.getNumber();
            case 1:
                return room.getType();
            case 2:
                return room.getCapacity();
            case 3:
                return room.getNote();
            default:
                String msg = "No column number " + column;
                log.error(msg);
                throw new IllegalArgumentException(msg);
        }
    }

    public Room getRoomForRow(int row) {
        return rooms.get(row);
    }


    public void editRoom(Room room){
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    roomManager.updateRoom(room);
                    rooms = roomManager.findAllRooms();
                    fireTableDataChanged();
                } catch (ServiceFailureException ex) {
                    log.warn("Cannot edit room " + room, ex);
                }
                return null;
            }

            @Override
            protected void done() {
                fireTableDataChanged();
            }
        }.execute();
    }


    public void addRoom(Room room) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    roomManager.createRoom(room);
                    rooms = roomManager.findAllRooms();
                    fireTableDataChanged();
                } catch (ServiceFailureException ex) {
                    log.info("Cannot add room " + room, ex);
                }
                return null;
            }

            @Override
            protected void done() {
                fireTableDataChanged();
            }
        }.execute();
    }


    public void deleteRoomForRow(int row) {
        Room room = rooms.get(row);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    roomManager.deleteRoom(room);
                    rooms = roomManager.findAllRooms();
                } catch (ServiceFailureException ex) {
                    log.info("Cannot delete room " + room, ex);
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