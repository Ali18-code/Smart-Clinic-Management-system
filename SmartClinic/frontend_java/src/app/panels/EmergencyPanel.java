package app.panels;

import app.util.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class EmergencyPanel extends JPanel {

    private JTextField nameField, severityField, fromField, toField;
    private JTextArea outputArea;

    public EmergencyPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(242, 245, 248)); // Soft medical gray-blue
        setBorder(new EmptyBorder(25, 25, 25, 25));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createButtonPanel(), BorderLayout.CENTER);
        add(createOutputPanel(), BorderLayout.SOUTH);
    }

    // ================= FORM SECTION =================
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        TitledBorder title = BorderFactory.createTitledBorder(
                new LineBorder(new Color(200, 200, 200), 1), " Emergency Admission Form ");
        title.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.setBorder(BorderFactory.createCompoundBorder(title, new EmptyBorder(15, 20, 15, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = createStyledField();
        severityField = createStyledField();
        fromField = createStyledField();
        toField = createStyledField();

        addRow(panel, gbc, 0, "Patient Name:", nameField);
        addRow(panel, gbc, 1, "Severity Score (1-10):", severityField);
        addRow(panel, gbc, 2, "Current Location (From):", fromField);
        addRow(panel, gbc, 3, "Destination (To):", toField);

        return panel;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int y, String label, JTextField field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(50, 60, 70));
        
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0;
        p.add(lbl, gbc);

        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1;
        p.add(field, gbc);
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(250, 35));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(210, 210, 210)), 
            new EmptyBorder(5, 10, 5, 10)));
        return f;
    }

    // ================= BUTTONS SECTION =================
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        panel.setOpaque(false);

        JButton addBtn = modernButton("ADD EMERGENCY", new Color(192, 57, 43));
        JButton processBtn = modernButton("PROCESS NEXT", new Color(230, 126, 34));
        JButton routeBtn = modernButton("CALCULATE ROUTE", new Color(41, 128, 185));

        addBtn.addActionListener(e -> addEmergency());
        processBtn.addActionListener(e -> runBackend("PROCESS_NEXT", false));
        routeBtn.addActionListener(e -> runBackend("ROUTE", true));

        panel.add(addBtn);
        panel.add(processBtn);
        panel.add(routeBtn);

        return panel;
    }

    private JButton modernButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setPreferredSize(new Dimension(220, 45));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent e) { b.setBackground(bg); }
        });

        return b;
    }

    // ================= OUTPUT TERMINAL =================
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        outputArea = new JTextArea(12, 30);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(30, 30, 30)); 
        outputArea.setForeground(new Color(0, 255, 100)); // Matrix Green
        outputArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane sp = new JScrollPane(outputArea);
        TitledBorder tb = BorderFactory.createTitledBorder(
            new LineBorder(new Color(100, 100, 100)), " SYSTEM LOGS / DSA TRACE ");
        tb.setTitleColor(new Color(150, 150, 150));
        sp.setBorder(tb);

        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    // ================= ACTION HANDLERS =================
    private void addEmergency() { 
        if (validateInput()) runBackend("ADD_EMERGENCY", true); 
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty() || fromField.getText().trim().isEmpty() || toField.getText().trim().isEmpty()) {
            showError("All fields are required.");
            return false;
        }
        try {
            int s = Integer.parseInt(severityField.getText().trim());
            if (s < 1 || s > 10) { showError("Severity must be 1–10."); return false; }
        } catch (NumberFormatException e) { showError("Severity must be a number."); return false; }
        return true;
    }

    private void runBackend(String action, boolean includeRoute) {
        // Run in a thread so the UI doesn't freeze during C++ execution
        new Thread(() -> {
            try {
                String json = buildJson(action, includeRoute);
                FileUtil.writeText(BackendRunner.DATA_DIR + "emergency_input.json", json);
                
                SwingUtilities.invokeLater(() -> outputArea.append(">> Running: " + action + "...\n"));
                
                BackendRunner.run("emergency");
                
                String result = FileUtil.readText(BackendRunner.DATA_DIR + "emergency_output.json");
                
                SwingUtilities.invokeLater(() -> {
                    outputArea.append(result + "\n");
                    outputArea.append("--------------------------------------------------\n");
                    // Auto-scroll to bottom
                    outputArea.setCaretPosition(outputArea.getDocument().getLength());
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> showError("Backend Error: " + ex.getMessage()));
            }
        }).start();
    }

    private String buildJson(String action, boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("{").append("\"action\":\"").append(action).append("\"");
        if (full) {
            sb.append(",\"name\":\"").append(nameField.getText().replace("\"", "\\\"")).append("\"")
              .append(",\"severity\":").append(severityField.getText())
              .append(",\"from\":\"").append(fromField.getText().replace("\"", "\\\"")).append("\"")
              .append(",\"to\":\"").append(toField.getText().replace("\"", "\\\"")).append("\"");
        }
        return sb.append("}").toString();
    }

    private void showError(String msg) { 
        JOptionPane.showMessageDialog(this, msg, "Input Error", JOptionPane.ERROR_MESSAGE); 
    }
}