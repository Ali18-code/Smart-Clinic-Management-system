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
        "Reception", "Triage", "Emergency Room", "ICU", 
        "Pharmacy", "Radiology", "General Ward", "Operation Theatre", "Cafeteria"
    };

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
        JPanel container = new JPanel(new GridLayout(2, 1, 0, 20));
        container.setOpaque(false);

        // 1. TRIAGE SECTION (Priority Queue)
        JPanel triagePanel = createSectionPanel("TRIAGE & ADMISSION (Priority Queue)", new Color(231, 76, 60));
        
        nameField = createStyledField();
        severityField = createStyledField();

        addInputRow(triagePanel, 0, "PATIENT NAME:", nameField);
        addInputRow(triagePanel, 1, "SEVERITY (1-10):", severityField);

        JPanel triageBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        triageBtns.setOpaque(false);
        triageBtns.add(modernButton("ADMIT PATIENT", new Color(192, 57, 43), e -> handleAdmit()));
        triageBtns.add(modernButton("PROCESS NEXT", new Color(44, 62, 80), e -> handleProcessNext()));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.insets = new Insets(15, 0, 0, 0);
        triagePanel.add(triageBtns, gbc);

        // 2. NAVIGATION SECTION (Graph BFS)
        JPanel navPanel = createSectionPanel("HOSPITAL NAVIGATION (Graph BFS)", new Color(41, 128, 185));
        
        fromBox = createStyledComboBox();
        toBox = createStyledComboBox();

        addInputRow(navPanel, 0, "START LOCATION:", fromBox);
        addInputRow(navPanel, 1, "TARGET LOCATION:", toBox);

        JButton routeBtn = modernButton("FIND SHORTEST PATH", new Color(52, 152, 219), e -> handleRoute());
        navPanel.add(routeBtn, gbc);
        
        container.add(triagePanel);
        container.add(navPanel);
        return container;
    }

    private JPanel createSectionPanel(String title, Color borderColor) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(borderColor, 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(borderColor);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = -1; gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        p.add(lbl, gbc);
        
        return p;
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
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.GRAY);
        
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.3;
        p.add(lbl, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        p.add(field, gbc);
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
        JComboBox<String> box = new JComboBox<>(LOCATIONS);
        box.setPreferredSize(new Dimension(200, 35));
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setBackground(Color.WHITE);
        return box;
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

    // ---------------- ACTIONS ----------------

    private void handleAdmit() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a patient name.");
            return;
        }
        String json = "{\"action\":\"ADD_EMERGENCY\", \"name\":\"" + SimpleJson.esc(nameField.getText()) + "\", \"severity\":" + getSeverity() + "}";
        executeBackend(json, "ADMIT PATIENT");
        nameField.setText("");
        severityField.setText("");
    }

    private void handleProcessNext() {
        executeBackend("{\"action\":\"PROCESS_NEXT\"}", "PROCESS NEXT PATIENT");
    }

    private void handleRoute() {
        String start = (String) fromBox.getSelectedItem();
        String end = (String) toBox.getSelectedItem();
        if (start.equals(end)) {
            outputArea.append("> Error: Start and Destination cannot be the same.\n");
            return;
        }
        String json = "{\"action\":\"ROUTE\", \"start\":\"" + start + "\", \"end\":\"" + end + "\"}";
        executeBackend(json, "CALCULATE SHORTEST PATH");
    }

    private void executeBackend(String jsonPayload, String logAction) {
        new Thread(() -> {
            try {
                FileUtil.writeText(BackendRunner.DATA_DIR + "emergency_input.json", jsonPayload);
                
                SwingUtilities.invokeLater(() -> outputArea.append("> " + logAction + "...\n"));
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

    private int getSeverity() {
        try {
            int s = Integer.parseInt(severityField.getText().trim());
            return Math.max(1, Math.min(10, s));
        } catch (Exception e) { return 5; } // Default
    }
}
