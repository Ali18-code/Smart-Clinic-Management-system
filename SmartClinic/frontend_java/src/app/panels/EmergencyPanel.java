package app.panels;

import app.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class EmergencyPanel extends JPanel {
    private JTextField nameField, severityField;
    private JComboBox<String> fromBox, toBox;
    private JTextArea outputArea;

    private static final String[] LOCATIONS = {
        "Reception", "Triage", "Emergency_Room", "ICU", 
        "Pharmacy", "Radiology", "General_Ward", "Operation_Theatre", "Cafeteria"
    };

    public EmergencyPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        add(createHeader(), BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new GridLayout(1, 2, 25, 0));
        mainContent.setOpaque(false);
        mainContent.add(createLeftPanel());
        mainContent.add(createRightPanel());

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("EMERGENCY RESPONSE CONTROL", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(44, 62, 80));
        JLabel subtitle = new JLabel("Priority Queue (Max-Heap) & Graph BFS Navigation");
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subtitle.setForeground(new Color(127, 140, 141));
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createLeftPanel() {
        JPanel container = new JPanel(new GridLayout(2, 1, 0, 20));
        container.setOpaque(false);

        // Triage Section
        JPanel triagePanel = createSectionPanel("TRIAGE & ADMISSION", new Color(231, 76, 60));
        nameField = createStyledField();
        severityField = createStyledField();
        addInputRow(triagePanel, 1, "PATIENT NAME:", nameField);
        addInputRow(triagePanel, 2, "SEVERITY (1-10):", severityField);

        JPanel triageBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        triageBtns.setOpaque(false);
        triageBtns.add(modernButton("ADMIT PATIENT", new Color(192, 57, 43), e -> handleAdmit()));
        triageBtns.add(modernButton("PROCESS NEXT", new Color(44, 62, 80), e -> handleProcessNext()));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(15, 0, 0, 0);
        triagePanel.add(triageBtns, gbc);

        // Navigation Section
        JPanel navPanel = createSectionPanel("HOSPITAL NAVIGATION", new Color(41, 128, 185));
        fromBox = createStyledComboBox();
        toBox = createStyledComboBox();
        addInputRow(navPanel, 1, "START LOCATION:", fromBox);
        addInputRow(navPanel, 2, "TARGET LOCATION:", toBox);

        JButton routeBtn = modernButton("FIND SHORTEST PATH", new Color(52, 152, 219), e -> handleRoute());
        gbc.gridy = 3;
        navPanel.add(routeBtn, gbc);
        
        container.add(triagePanel);
        container.add(navPanel);
        return container;
    }

    private JPanel createSectionPanel(String title, Color borderColor) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(new LineBorder(borderColor, 2, true), new EmptyBorder(15, 15, 15, 15)));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(borderColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        p.add(lbl, gbc);
        return p;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(33, 37, 41));
        panel.setBorder(new TitledBorder(new LineBorder(Color.GRAY), " LIVE DSA TRACE ", TitledBorder.LEFT, TitledBorder.TOP, new Font("Consolas", Font.BOLD, 12), Color.LIGHT_GRAY));
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(33, 37, 41));
        outputArea.setForeground(new Color(46, 204, 113));
        outputArea.setMargin(new Insets(10, 10, 10, 10));
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        
        JButton clearBtn = new JButton("CLEAR TERMINAL LOGS");
        clearBtn.setFont(new Font("Consolas", Font.BOLD, 12));
        clearBtn.setBackground(new Color(45, 50, 55));
        clearBtn.setForeground(new Color(200, 200, 200));
        clearBtn.setBorder(new EmptyBorder(8, 0, 8, 0));
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> outputArea.setText(""));
        panel.add(clearBtn, BorderLayout.SOUTH);
        return panel;
    }

    private void addInputRow(JPanel p, int y, String label, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 0, 5, 0);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.3; p.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; p.add(field, gbc);
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(200, 35));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 214, 229), 1), 
            new EmptyBorder(5, 10, 5, 10)));
        return f;
    }

    private JComboBox<String> createStyledComboBox() {
        return new JComboBox<>(LOCATIONS);
    }

    private JButton modernButton(String text, Color bg, ActionListener al) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.addActionListener(al);
        return b;
    }

    private void handleAdmit() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Patient name required!");
            return;
        }
        executeBackend("{\"action\":\"ADD_EMERGENCY\", \"name\":\"" + SimpleJson.esc(nameField.getText()) + "\", \"severity\":" + getSeverity() + "}", "ADMITTING");
        nameField.setText("");
        severityField.setText("");
    }

    private void handleProcessNext() {
        executeBackend("{\"action\":\"PROCESS_NEXT\"}", "PROCESSING NEXT");
    }

    private void handleRoute() {
        String start = (String) fromBox.getSelectedItem();
        String end = (String) toBox.getSelectedItem();
        if (start.equals(end)) {
            outputArea.append("> Error: Start and Destination cannot be the same.\n");
            return;
        }
        executeBackend("{\"action\":\"ROUTE\", \"from\":\"" + start + "\", \"to\":\"" + end + "\"}", "PATHFINDING");
    }

    private void executeBackend(String json, String action) {
        new Thread(() -> {
            try {
                FileUtil.writeText(BackendRunner.DATA_DIR + "emergency_input.json", json);
                SwingUtilities.invokeLater(() -> outputArea.append("> " + action + "...\n"));
                BackendRunner.run("emergency");
                String result = FileUtil.readText(BackendRunner.DATA_DIR + "emergency_output.json");
                SwingUtilities.invokeLater(() -> {
                    outputArea.append(result + "\n---\n");
                    outputArea.setCaretPosition(outputArea.getDocument().getLength());
                });
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private int getSeverity() {
        try { return Integer.parseInt(severityField.getText()); } catch (Exception e) { return 5; }
    }
}
