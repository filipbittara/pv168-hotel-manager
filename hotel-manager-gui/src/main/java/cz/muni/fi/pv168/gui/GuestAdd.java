package cz.muni.fi.pv168.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.mumi.fi.pv168.backend.Guest;
import cz.mumi.fi.pv168.backend.ServiceFailureException;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
/**
 * Created by Filip on 13.5.2014.
 */
public class GuestAdd {
    private JTextField GueNamTextFiel;
    private JTextField GuePassTextFiel;
    private JTextField GueCarTextFiel;
    private JRadioButton GueAddRadioButton;
    private JButton GueAddConfirmButton;
    private JLabel GueNamLabel;
    private JLabel GuePassLabel;
    private JLabel GueCreLabel;
    private JLabel GueVipLabel;
    private JPanel GuePanel;
    private JLabel GueErrLabel;
    Border redBorder = BorderFactory.createLineBorder(Color.RED);
    Border defaultBorder = UIManager.getBorder("TextField.border");


    private final static Logger log = LoggerFactory.getLogger(GuestAdd.class);

    static ResourceBundle texts = ResourceBundle.getBundle("Texts");

    private boolean asEdit;
    private Guest guest;

    public GuestAdd(GuestTableModel guestTableModel, boolean asEdit, Guest guest) {
        super();
        this.asEdit = asEdit;
        this.guest = guest;

        GueAddConfirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GueNamTextFiel.setBorder(defaultBorder);
                GueCarTextFiel.setBorder(defaultBorder);
                GuePassTextFiel.setBorder(defaultBorder);
                
                
                if(GueNamTextFiel.getText().isEmpty() || GueCarTextFiel.getText().isEmpty() || GuePassTextFiel.getText().isEmpty()){
                    GueErrLabel.setVisible(true);
                    
                    if(GueNamTextFiel.getText().isEmpty())
                        GueNamTextFiel.setBorder(redBorder);
                    if(GueCarTextFiel.getText().isEmpty())
                        GueCarTextFiel.setBorder(redBorder);
                    if(GuePassTextFiel.getText().isEmpty())
                        GuePassTextFiel.setBorder(redBorder);
                    
                }
                else {
                    final Guest temp = newGuest(GueNamTextFiel.getText(), GueCarTextFiel.getText(), GuePassTextFiel.getText(), GueAddRadioButton.isSelected());
                    if (asEdit) {
                        temp.setId(guest.getId());
                        guestTableModel.editGuest(temp);
                    } else {
                        guestTableModel.addGuest(temp);
                    }
                    
                    Container frame = GueAddConfirmButton.getParent();
                    do
                        frame = frame.getParent();
                    while (!(frame instanceof JFrame));
                    ((JFrame) frame).dispose();
                }
            }
        });
    }

    public void start() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                String temp = "words_new_guest";
                if (asEdit) {
                    temp = "words_edit_guest";
                    
                    GueNamTextFiel.setText(guest.getFullName());
                    GueCarTextFiel.setText(guest.getCreditCardNumber());
                    GuePassTextFiel.setText(guest.getPassportNumber());
                    GueAddRadioButton.setSelected(guest.getVipCustomer());
                }   JFrame frame = new JFrame();
                frame.setTitle(texts.getString(temp));
                frame.setContentPane(GuestAdd.this.GuePanel);
                frame.pack();
                GueErrLabel.setForeground(Color.RED);
                GueErrLabel.setVisible(false);
                frame.setVisible(true);
            }
        });
    }

    private static Guest newGuest(String fullName, String creditCardNumber, String passportNUmber, boolean vip) {
        Guest guest = new Guest();
        guest.setFullName(fullName);
        guest.setCreditCardNumber(creditCardNumber);
        guest.setPassportNumber(passportNUmber);
        guest.setVipCustomer(vip);
        return guest;
    }

    private void createUIComponents() {

        GueNamTextFiel = new JTextField();
        GueCarTextFiel = new JTextField();
        GuePassTextFiel = new JTextField();
        GueAddRadioButton = new JRadioButton();
        GueAddRadioButton.setSelected(false);
        GueErrLabel = new JLabel();
    }
}
