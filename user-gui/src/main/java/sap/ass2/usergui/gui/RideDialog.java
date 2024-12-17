package sap.ass2.usergui.gui;

import javax.swing.*;
import io.vertx.core.json.JsonObject;
import sap.ass2.usergui.domain.Ride;
import sap.ass2.usergui.library.ApplicationAPI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class RideDialog extends JDialog {
    private JComboBox<String> bikesComboBox;        
    private JButton startButton;                    
    private JButton cancelButton;                   
    private UserGUI fatherUserGUI;                  
    private ApplicationAPI app;
    
    private String userRiding;                      
    private List<String> availableBikes;            
    private String bikeSelectedID;                  

    public RideDialog(UserGUI fatherUserGUI, String user, ApplicationAPI app) {
        super(fatherUserGUI, "Start Riding an EBike", true);
        this.app = app;

        this.fatherUserGUI = fatherUserGUI; 
        this.userRiding = user;             
        
        this.app.ebikes().getAllAvailableEbikesIDs()
            .onSuccess(ebikeIds -> {
                this.availableBikes = ebikeIds.stream().map(Object::toString).collect(Collectors.toList());
            
                initializeComponents();                 
                setupLayout();                          
                addEventHandlers();                     
                setLocationRelativeTo(fatherUserGUI);   
                pack();                                 
            })
            .onFailure(ex -> {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            });
    }

    private void initializeComponents() {
        bikesComboBox = new JComboBox<String>(new Vector<>(this.availableBikes));
        startButton = new JButton("Start Riding");
        cancelButton = new JButton("Cancel");
    }

    private void setupLayout() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("E-Bike to ride:"));
        inputPanel.add(bikesComboBox);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout(10, 10)); 
        add(inputPanel, BorderLayout.CENTER); 
        add(buttonPanel, BorderLayout.SOUTH); 
    }

    private static Ride jsonObjToRide(JsonObject obj) {
        return new Ride(obj.getString("rideId"), obj.getString("userId"), obj.getString("ebikeId"));
    }

    private void addEventHandlers() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bikeSelectedID = bikesComboBox.getSelectedItem().toString(); 
                cancelButton.setEnabled(false); 

                app.rides().beginRide(userRiding, bikeSelectedID)
                    .onSuccess(rideObj -> {
                        fatherUserGUI.setLaunchedRide(jsonObjToRide(rideObj));
                        dispose(); 
                    })
                    .onFailure(ex -> {
                        JOptionPane.showMessageDialog(fatherUserGUI, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                    });
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); 
            }
        });
    }
}
