package sap.ass2.admingui.gui;

import javax.swing.*;
import sap.ass2.admingui.library.ApplicationAPI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddEBikeDialog extends JDialog {
    private JTextField idField;     
    private JTextField xCoordField; 
    private JTextField yCoordField; 
    private JButton okButton;       
    private JButton cancelButton;   
    private ApplicationAPI app; 

    public AddEBikeDialog(AdminGUI owner, ApplicationAPI app) {
        super(owner, "Adding E-Bike", true);    
        this.app = app; 
        initializeComponents();                 
        setupLayout();                          
        addEventHandlers();                     
        pack();                                 
        setLocationRelativeTo(owner);           
    }

    private void initializeComponents() {
        idField = new JTextField(15);           
        xCoordField = new JTextField(15);       
        yCoordField = new JTextField(15);       
        okButton = new JButton("OK");           
        cancelButton = new JButton("Cancel");   
    }

    private void setupLayout() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("E-Bike ID:")); 
        inputPanel.add(idField); 
        inputPanel.add(new JLabel("E-Bike location - X coord:")); 
        inputPanel.add(xCoordField); 
        inputPanel.add(new JLabel("E-Bike location - Y coord:")); 
        inputPanel.add(yCoordField); 

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton); 
        buttonPanel.add(cancelButton); 

        setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.CENTER); 
        add(buttonPanel, BorderLayout.SOUTH); 
    }

    private void addEventHandlers() {
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String xCoord = xCoordField.getText();
                String yCoord = yCoordField.getText();

                app.ebikes().createEbike(id, Integer.parseInt(xCoord), Integer.parseInt(yCoord));
                dispose(); 
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
