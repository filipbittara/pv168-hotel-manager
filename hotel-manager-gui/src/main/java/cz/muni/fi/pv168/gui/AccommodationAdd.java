package cz.muni.fi.pv168.gui;

import cz.mumi.fi.pv168.backend.Guest;
import cz.mumi.fi.pv168.backend.Accommodation;
import cz.mumi.fi.pv168.backend.Room;
import cz.mumi.fi.pv168.backend.GuestManager;
import cz.mumi.fi.pv168.backend.RoomManager;
import cz.mumi.fi.pv168.backend.SpringConfig;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import org.slf4j.Logger;

/**
 * Created by Filip on 14.5.2014.
 */
public class AccommodationAdd extends JFrame{
    private JPanel AccPanel;
    private JButton AccConfirmButton;
    private JLabel AccRooLabel;
    private JLabel AccGueLabel;
    private JLabel AccChiLabel;
    private JLabel AccChoLabel;
    private JLabel AccDateFormatLabel1;
    private JLabel AccDateFormatLabel2;
    private JTable table1;
    private JTable table2;
    private JTextField textField1;
    private JTextField textField2;
    private JSpinner spinner1;
    private JSpinner spinner2;
    private JSpinner spinner3;
    private JSpinner spinner4;
    private JSpinner spinner5;
    private JSpinner spinner6;
    private JLabel AccErrLabel;
    private JScrollPane tablePane1;
    private JScrollPane tablePane2;
    Border redBorder = BorderFactory.createLineBorder(Color.RED);
    Border defaultBorder = UIManager.getBorder("ScrollPane.border");


    private final static Logger log = LoggerFactory.getLogger(AccommodationAdd.class);

    static ResourceBundle texts = ResourceBundle.getBundle("Texts");

    private GuestManager guestManager;
    private RoomManager roomManager;
    private GuestTableModel guestTableModel;
    private RoomTableModel roomTableModel;

    private boolean asEdit;
    private Accommodation accommodation;



    public AccommodationAdd(AccommodationTableModel accommodationTableModel, boolean asEdit, Accommodation accommodation) {
        super();
        this.asEdit = asEdit;
        this.accommodation = accommodation;

        AccConfirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Room room;
                Guest guest;
                Calendar checkIn;
                Calendar checkOut;
                
                tablePane1.setBorder(defaultBorder);
                tablePane2.setBorder(defaultBorder);
                
                if(table1.getSelectedRow() == -1 || table2.getSelectedRow() == -1) {
                    AccErrLabel.setVisible(true);
                    if(table1.getSelectedRow() == -1)
                        tablePane1.setBorder(redBorder);
                    if(table2.getSelectedRow() == -1)
                        tablePane2.setBorder(redBorder);
                }
                else {
                    room = roomTableModel.getRoomForRow(table2.getSelectedRow());
                    guest = guestTableModel.getGuestForRow(table1.getSelectedRow());
                    
                    checkIn = new GregorianCalendar((Integer) spinner1.getValue(), (Integer) spinner2.getValue() - 1, (Integer) spinner3.getValue());
                    checkOut = new GregorianCalendar((Integer) spinner4.getValue(), (Integer) spinner5.getValue() - 1, (Integer) spinner6.getValue());
                    
                    Accommodation temp = newAccommodation(room, guest, checkIn, checkOut);
                    
                    if (asEdit) {
                        temp.setId(AccommodationAdd.this.accommodation.getId());
                        accommodationTableModel.editAccommodation(temp);
                    } else {
                        accommodationTableModel.addAccommodation(temp);
                    }
                    
                    Container frame = AccConfirmButton.getParent();
                    do
                        frame = frame.getParent();
                    while (!(frame instanceof JFrame));
                    ((JFrame) frame).dispose();
                }
            }
        });

        final TableRowSorter<RoomTableModel> roomSorter = new TableRowSorter<RoomTableModel>(roomTableModel);
        table2.setRowSorter(roomSorter);
        final TableRowSorter<GuestTableModel> guestSorter = new TableRowSorter<GuestTableModel>(guestTableModel);
        table1.setRowSorter(guestSorter);

        textField1.addKeyListener(KeyAdapterSelect(roomSorter, textField1));
        textField2.addKeyListener(KeyAdapterSelect(guestSorter, textField2));
    }

    public KeyAdapter KeyAdapterSelect(TableRowSorter sorter, JTextField textField) {
        return new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                RowFilter<Object,Object> filter = new RowFilter<Object,Object>() {
                    public boolean include(Entry<? extends Object, ? extends Object> entry) {
                        for (int i = entry.getValueCount() - 1; i >= 0; i--) {
                            if (entry.getStringValue(i).toLowerCase().contains(textField.getText().toLowerCase())) {
                                return true;
                            }
                        }
                        return false;
                    }
                };
                sorter.setRowFilter(filter);
            }
        };
    }

    public void start() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                String temp = "words_new_accommodation";
                if (asEdit) {
                    temp = "words_edit_accommodation";
                    int selectedRow = guestTableModel.getRowForGuest(accommodation.getGuest());
                    table1.setRowSelectionInterval(selectedRow,selectedRow);
                    selectedRow = roomTableModel.getRowForRoom(accommodation.getRoom());
                    table2.setRowSelectionInterval(selectedRow,selectedRow);
                    GregorianCalendar date = (GregorianCalendar) AccommodationAdd.this.accommodation.getCheckIn();
                    spinner1.setValue(date.get(Calendar.YEAR));
                    spinner2.setValue(date.get(Calendar.MONTH)+1);
                    spinner3.setValue(date.get(Calendar.DAY_OF_MONTH));
                    date = (GregorianCalendar) AccommodationAdd.this.accommodation.getCheckOut();
                    spinner4.setValue(date.get(Calendar.YEAR));
                    spinner5.setValue(date.get(Calendar.MONTH)+1);
                    spinner6.setValue(date.get(Calendar.DAY_OF_MONTH));
                }
                JFrame frame = new JFrame();
                frame.setTitle(texts.getString(temp));
                AccommodationAdd.this.AccPanel.setPreferredSize(new Dimension(600, 400));
                frame.setContentPane(AccommodationAdd.this.AccPanel);
                frame.pack();
                AccErrLabel.setForeground(Color.RED);
                AccErrLabel.setVisible(false);
                frame.setVisible(true);
            }
        });
    }

    private static Accommodation newAccommodation(Room room, Guest guest, Calendar checkIn, Calendar checkOut) {
        Accommodation accommodation = new Accommodation();
        accommodation.setRoom(room);
        accommodation.setGuest(guest);
        accommodation.setCheckIn(checkIn);
        accommodation.setCheckOut(checkOut);
        return accommodation;
    }

    private void createUIComponents() {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        guestManager = ctx.getBean("guestManager", GuestManager.class);
        roomManager = ctx.getBean("roomManager", RoomManager.class);
        guestTableModel = new GuestTableModel(guestManager);
        roomTableModel = new RoomTableModel(roomManager);

        table2 = new JTable(roomTableModel);
        table1 = new JTable(guestTableModel);

        spinner1 = new JSpinner(new SpinnerNumberModel(1,0,Integer.MAX_VALUE,1));
        spinner2 = new JSpinner(new SpinnerNumberModel(1,1,12,1));
        spinner3 = new JSpinner(new SpinnerNumberModel(1,1,31,1));

        spinner4 = new JSpinner(new SpinnerNumberModel(1,0,Integer.MAX_VALUE,1));
        spinner5 = new JSpinner(new SpinnerNumberModel(1,1,12,1));
        spinner6 = new JSpinner(new SpinnerNumberModel(1,1,31,1));

        AccErrLabel = new JLabel();
    }
}
