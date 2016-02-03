package cz.muni.fi.pv168.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.mumi.fi.pv168.backend.Room;
import cz.mumi.fi.pv168.backend.RoomType;
import cz.mumi.fi.pv168.backend.ServiceFailureException;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by Filip on 13.5.2014.
 */
public class RoomAdd {
    final static Logger log = LoggerFactory.getLogger(RoomAdd.class);
    static ResourceBundle texts = ResourceBundle.getBundle("Texts");
    private JTextField RooAddCapTextField;
    private JTextField RooAddNumTextField;
    private JTextField RooAddNotTextField;
    private JButton RooAddConfirmButton;
    private JComboBox RooAddTypTextField;
    private JPanel panel1;
    private JLabel NUMBER;
    private JLabel CAPACITY;
    private JLabel TYPE;
    private JLabel NOTE;
    private JLabel RooErrLabel;
    private JLabel RooNumErrLabel;
    private boolean asEdit;
    private Room room;
    Border redBorder = BorderFactory.createLineBorder(Color.RED);
    Border defaultBorder = UIManager.getBorder("TextField.border");

    public RoomAdd(RoomTableModel roomTableModel, boolean asEdit, Room room) {
        super();
        this.asEdit = asEdit;
        this.room = room;



        RooAddConfirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RooAddCapTextField.setBorder(defaultBorder);
                RooAddNumTextField.setBorder(defaultBorder);
                RooAddCapTextField.setForeground(Color.BLACK);
                RooAddNumTextField.setForeground(Color.BLACK);
                
                RooErrLabel.setVisible(false);
                RooNumErrLabel.setVisible(false);
                
                
                if(RooAddCapTextField.getText().isEmpty() || RooAddNumTextField.getText().isEmpty()
                        || !isNumeric(RooAddCapTextField.getText()) || !isNumeric(RooAddNumTextField.getText())) {
                    
                    if(RooAddCapTextField.getText().isEmpty() || RooAddNumTextField.getText().isEmpty())
                        RooErrLabel.setVisible(true);
                    
                    if(!isNumeric(RooAddCapTextField.getText()) || !isNumeric(RooAddNumTextField.getText()))
                        RooNumErrLabel.setVisible(true);
                    
                    if(RooAddCapTextField.getText().isEmpty())
                        RooAddCapTextField.setBorder(redBorder);
                    if(RooAddNumTextField.getText().isEmpty())
                        RooAddNumTextField.setBorder(redBorder);
                    
                    if(!isNumeric(RooAddCapTextField.getText()))
                        RooAddCapTextField.setForeground(Color.RED);
                    if(!isNumeric(RooAddNumTextField.getText()))
                        RooAddNumTextField.setForeground(Color.RED);
                }
                
                else {
                    int capacity = Integer.parseInt(RooAddCapTextField.getText());
                    int number = Integer.parseInt(RooAddNumTextField.getText());
                    String note = RooAddNotTextField.getText();
                    RoomType type;
                    
                    switch ((String) RooAddTypTextField.getSelectedItem()) {
                        case "DELUXE":
                            type = RoomType.DELUXE;
                            break;
                        case "SUITE":
                            type = RoomType.SUITE;
                            break;
                        case "STANDARD":
                            type = RoomType.STANDARD;
                            break;
                        default:
                            type = null;
                    }
                    
                    final Room temp = newRoom(number, type, capacity, note);
                    if (asEdit) {
                        temp.setId(room.getId());
                        roomTableModel.editRoom(temp);
                    } else {
                        roomTableModel.addRoom(temp);
                    }
                    
                    Container frame = RooAddConfirmButton.getParent();
                    do
                        frame = frame.getParent();
                    while (!(frame instanceof JFrame));
                    ((JFrame) frame).dispose();
                }
            }
        });
    }

    private static boolean isNumeric (String s) {
        if(s.isEmpty())
            return true;
        try {
            Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static Room newRoom(int number, RoomType type, int capacity, String note) {
        Room room = new Room();
        room.setCapacity(capacity);
        room.setNumber(number);
        room.setType(type);
        room.setNote(note);
        return room;
    }

    public void start() {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                String temp  = "words_new_room";
                if (asEdit) {
                    temp  = "words_edit_room";
                    RooAddCapTextField.setText(Integer.toString(room.getCapacity()));
                    RooAddNumTextField.setText(Integer.toString(room.getNumber()));
                    RooAddNotTextField.setText(room.getNote());
                    switch (room.getType()) {
                        case DELUXE:
                            RooAddTypTextField.setSelectedIndex(0);
                            break;
                        case SUITE:
                            RooAddTypTextField.setSelectedIndex(1);
                            break;
                        case STANDARD:
                            RooAddTypTextField.setSelectedIndex(2);
                            break;
                        default:
                            RooAddTypTextField.setSelectedIndex(0);
                    }
                }   JFrame frame = new JFrame();
                frame.setTitle(texts.getString(temp));
                frame.setContentPane(RoomAdd.this.panel1);
                frame.pack();
                RooErrLabel.setForeground(Color.RED);
                RooNumErrLabel.setForeground(Color.RED);
                RooErrLabel.setVisible(false);
                RooNumErrLabel.setVisible(false);
                frame.setVisible(true);
            }
        });
    }

    private void createUIComponents() {
        Vector<String> myTypes = new Vector<>();
        myTypes.add("DELUXE");
        myTypes.add("SUITE");
        myTypes.add("STANDARD");

        RooAddTypTextField = new JComboBox(myTypes);
        RooAddCapTextField = new JTextField();
        RooAddNumTextField = new JTextField();
        RooAddNotTextField = new JTextField();
        RooErrLabel = new JLabel();
        RooNumErrLabel = new JLabel();
    }
}
