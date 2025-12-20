package app.panels;

import app.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class EmergencyPanel extends JPanel {

    private JTextField nameField, severityField, fromField, toField;
    private JTextArea outputArea;
    private JPanel cardPanel;

    public EmergencyPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(236, 240, 241)); // Light medical background
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header Section
        add(createHeader(), BorderLayout.NORTH);

        // Main Content: Split into Form (Left) and Terminal (Right)
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 25, 0));
        mainContent.setOpaque(false);

        mainContent.add(createLeftPanel());  // Form & Navigation
        mainContent.add(createRightPanel()); // System Logs & Trace

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel title = new JLabel("EMERGENCY RESPONSE CONTROL", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(44, 62, 80));
        
        JLabel subtitle = new JLabel("Priority Queue & BFS Pathfinding Integration");
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subtitle.setForeground(new Color(127, 140, 141));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createLeftPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 20));
        container.setOpaque(false);

        // Admission Form Card
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(new LineBorder(new Color(231, 76, 60), 2, true));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = createStyledField("Patient Name");
        severityField = createStyledField("1-10 (High Priority)");
        fromField = createStyledField("Start Room");
        toField = createStyledField("Target Room");

        addInputRow(formCard, gbc, 0, "👤 PATIENT NAME:", nameField);
        addInputRow(formCard, gbc, 1, "🚨 SEVERITY SCORE:", severityField);
        addInputRow(formCard, gbc, 2, "📍 CURRENT LOC:", fromField);
        addInputRow(formCard, gbc, 3, "🏥 DESTINATION:", toField);

        // Action Buttons
        JPanel btnPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        btnPanel.setOpaque(false);
        
        btnPanel.add(modernButton("ADD TO PRIORITY HEAP", new Color(192, 57, 43), e -> addEmergency()));
        btnPanel.add(modernButton("PROCESS HIGHEST PRIORITY", new Color(44, 62, 80), e -> runBackend("PROCESS_NEXT", false)));
        btnPanel.add(modernButton("GENERATE SHORTEST PATH (BFS)", new Color(41, 128, 185), e -> runBackend("ROUTE", true)));

        container.add(formCard, BorderLayout.CENTER);
        container.add(btnPanel, BorderLayout.SOUTH);
        
        return container;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(33, 37, 41));
        panel.setBorder(new TitledBorder(new LineBorder(Color.GRAY), " LIVE DSA TRACE LOGS ", 
                       TitledBorder.LEFT, TitledBorder.TOP, new Font("Consolas", Font.BOLD, 12), Color.LIGHT_GRAY));

        outputArea = new JTextArea();
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(33, 37, 41));
        outputArea.setForeground(new Color(46, 204, 113)); // Terminal Green
        outputArea.setMargin(new Insets(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(outputArea);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void addInputRow(JPanel p, GridBagConstraints gbc, int y, String label, JTextField field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.3;
        p.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        p.add(field, gbc);
    }

    private JTextField createStyledField(String placeholder) {
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(200, 40));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 214, 229), 1), 
            new EmptyBorder(5, 10, 5, 10)));
        return f;
    }

    private JButton modernButton(String text, Color bg, ActionListener al) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addActionListener(al);
        
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }

    // Action Logic (Simplified version of your handlers)
    private void addEmergency() {
        if(validateInput()) runBackend("ADD_EMERGENCY", true);
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Patient name required!");
            return false;
        }
        return true;
    }

    private void runBackend(String action, boolean includeDetails) {
        new Thread(() -> {
            try {
                String json = "{\"action\":\"" + action + "\"}"; // Simplified JSON builder
                FileUtil.writeText(BackendRunner.DATA_DIR + "emergency_input.json", json);
                
                SwingUtilities.invokeLater(() -> outputArea.append("> Executing " + action + "...\n"));
                BackendRunner.run("emergency");
                
                String result = FileUtil.readText(BackendRunner.DATA_DIR + "emergency_output.json");
                SwingUtilities.invokeLater(() -> {
                    outputArea.append(result + "\n------------------\n");
                    outputArea.setCaretPosition(outputArea.getDocument().getLength());
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
