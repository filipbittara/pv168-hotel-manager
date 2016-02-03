package cz.muni.fi.pv168.gui;

import cz.mumi.fi.pv168.backend.Accommodation;
import cz.mumi.fi.pv168.backend.Guest;
import cz.mumi.fi.pv168.backend.Room;
import cz.mumi.fi.pv168.backend.GuestManager;
import cz.mumi.fi.pv168.backend.AccommodationManager;
import cz.mumi.fi.pv168.backend.RoomManager;
import cz.mumi.fi.pv168.backend.SpringConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Created by Filip on 13.5.2014.
 */
public class MainForm {
    private JTabbedPane tabbedPane;
    JPanel panel1;
    private JTable AccTable;
    private JButton AccAddButton;
    private JButton AccEditButton;
    private JButton AccDeleteButton;
    private JTextField AccFilterTextField;
    private JTable RooTable;
    private JButton RooAddButton;
    private JButton RooEditButton;
    private JButton RooDeleteButton;
    private JTextField RooFilterTextField;
    private JTable GueTable;
    private JButton GueAddButton;
    private JButton GueEditButton;
    private JButton GueDeleteButton;
    private JTextField GueFilterTextField;
    private JLabel AccLabel;
    private JLabel RooLabel;
    private JLabel GueLabel;


    final static Logger log = LoggerFactory.getLogger(MainForm.class);

    static ResourceBundle texts = ResourceBundle.getBundle("Texts");
    private AccommodationManager accommodationManager;
    private AccommodationTableModel accommodationTableModel;
    private GuestManager guestManager;
    private GuestTableModel guestTableModel;
    private RoomManager roomManager;
    private RoomTableModel roomTableModel;

    public MainForm() {
        super();
        log.info("new MainForm()");
        AccDeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = AccTable.convertRowIndexToModel(AccTable.getSelectedRow());
                accommodationTableModel.deleteAccommodationForRow(row);
            }
        });
        GueDeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = GueTable.convertRowIndexToModel(GueTable.getSelectedRow());
                guestTableModel.deleteGuestForRow(row);
            }
        });
        RooDeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = RooTable.convertRowIndexToModel(RooTable.getSelectedRow());
                roomTableModel.deleteRoomForRow(row);
            }
        });
        RooAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final RoomAdd roomAdd = new RoomAdd(roomTableModel, false, null);
                roomAdd.start();
            }
        });
        RooEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = RooTable.convertRowIndexToModel(RooTable.getSelectedRow());
                Room room = roomTableModel.getRoomForRow(row);
                final RoomAdd roomAdd = new RoomAdd(roomTableModel, true, room);
                roomAdd.start();
            }
        });

        GueAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final GuestAdd guestAdd = new GuestAdd(guestTableModel, false, null);
                guestAdd.start();
            }
        });

        GueEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = GueTable.convertRowIndexToModel(GueTable.getSelectedRow());
                Guest guest = guestTableModel.getGuestForRow(row);
                final GuestAdd guestAdd = new GuestAdd(guestTableModel, true, guest);
                guestAdd.start();
            }
        });

        AccAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final AccommodationAdd accommodationAdd = new AccommodationAdd(accommodationTableModel, false, null);
                accommodationAdd.start();
            }
        });

        AccEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = AccTable.convertRowIndexToModel(AccTable.getSelectedRow());
                Accommodation accommodation = accommodationTableModel.getAccommodationForRow(row);
                final AccommodationAdd accommodationAdd = new AccommodationAdd(accommodationTableModel, true, accommodation);
                accommodationAdd.start();
            }
        });

        final TableRowSorter<RoomTableModel> roomSorter = new TableRowSorter<RoomTableModel>(roomTableModel);
        RooTable.setRowSorter(roomSorter);
        final TableRowSorter<GuestTableModel> guestSorter = new TableRowSorter<GuestTableModel>(guestTableModel);
        GueTable.setRowSorter(guestSorter);
        final TableRowSorter<AccommodationTableModel> accommodationSorter = new TableRowSorter<AccommodationTableModel>(accommodationTableModel);
        AccTable.setRowSorter(accommodationSorter);

        RooFilterTextField.addKeyListener(KeyAdapterSelect(roomSorter, RooFilterTextField));
        GueFilterTextField.addKeyListener(KeyAdapterSelect(guestSorter, GueFilterTextField));
        AccFilterTextField.addKeyListener(KeyAdapterSelect(accommodationSorter, AccFilterTextField));

        AccTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();
                    int column = target.getSelectedColumn();

                    if(column == 0 ) {
                        int selectedRow = guestTableModel.getRowForGuest(accommodationTableModel.getAccommodationForRow(row).getGuest());
                        guestSorter.setRowFilter(null);
                        GueTable.setRowSelectionInterval(selectedRow,selectedRow);
                        tabbedPane.setSelectedIndex(2);
                    }
                    else if (column == 1){
                        int selectedRow = roomTableModel.getRowForRoom(accommodationTableModel.getAccommodationForRow(row).getRoom());
                        roomSorter.setRowFilter(null);
                        RooTable.setRowSelectionInterval(selectedRow,selectedRow);
                        tabbedPane.setSelectedIndex(1);
                    }
                }
            }
        });
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

    public static void main(String[] args) {
        log.info("zaciname");

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setTitle(texts.getString("words_hotel_manager"));
                MainForm mainForm = new MainForm();
                mainForm.panel1.setPreferredSize(new Dimension(800, 600));
                frame.setContentPane(mainForm.panel1);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }



    private void createUIComponents() {
        log.info("createUIComponents()");
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        accommodationManager = ctx.getBean("accommodationManager", AccommodationManager.class);
        guestManager = ctx.getBean("guestManager", GuestManager.class);
        roomManager = ctx.getBean("roomManager", RoomManager.class);

        accommodationTableModel = new AccommodationTableModel(accommodationManager);
        AccTable = new JTable(accommodationTableModel);
        ListSelectionModel accSelectionModel = AccTable.getSelectionModel();
        accSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(AccTable.getSelectedRowCount()==1) {
                    AccDeleteButton.setEnabled(true);
                    AccEditButton.setEnabled(true);
                } else {
                    AccDeleteButton.setEnabled(false);
                    AccEditButton.setEnabled(false);
                }
            }
        });
        guestTableModel = new GuestTableModel(guestManager);
        GueTable = new JTable(guestTableModel);
        ListSelectionModel gueSelectionModel = GueTable.getSelectionModel();
        gueSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gueSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(GueTable.getSelectedRowCount()==1) {
                    GueDeleteButton.setEnabled(true);
                    GueEditButton.setEnabled(true);
                } else {
                    GueDeleteButton.setEnabled(false);
                    GueEditButton.setEnabled(false);
                }
            }
        });
        roomTableModel = new RoomTableModel(roomManager);
        RooTable = new JTable(roomTableModel);
        ListSelectionModel rooSelectionModel = RooTable.getSelectionModel();
        rooSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rooSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(RooTable.getSelectedRowCount()==1) {
                    RooDeleteButton.setEnabled(true);
                    RooEditButton.setEnabled(true);
                } else {
                    RooDeleteButton.setEnabled(false);
                    RooEditButton.setEnabled(false);
                }
            }
        });
    }
}


