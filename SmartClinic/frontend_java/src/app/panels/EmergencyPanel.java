package app.panels;

import app.util.*;
import java.awt.*;
import javax.swing.*;

public class EmergencyPanel extends JPanel {

    private JTextField name, severity, from, to;
    private JTextArea output;

    public EmergencyPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));

        // ================= FORM PANEL =================
        JPanel form = new JPanel(new GridLayout(2, 4, 10, 10));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Emergency Details"
        ));

        name = new JTextField();
        severity = new JTextField();
        from = new JTextField();
        to = new JTextField();

        form.add(new JLabel("Patient Name"));
        form.add(new JLabel("Severity (1-10)"));
        form.add(new JLabel("From Room"));
        form.add(new JLabel("To Room"));

        form.add(name);
        form.add(severity);
        form.add(from);
        form.add(to);

        add(form, BorderLayout.NORTH);

        // ================= BUTTON PANEL =================
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        buttons.setBackground(new Color(245, 247, 250));

        JButton addBtn     = classicButton("Add Emergency Patient", new Color(192, 57, 43));
        JButton processBtn = classicButton("Process Next", new Color(243, 156, 18));
        JButton routeBtn   = classicButton("Show Shortest Route", new Color(41, 128, 185));

        buttons.add(addBtn);
        buttons.add(processBtn);
        buttons.add(routeBtn);

        add(buttons, BorderLayout.CENTER);

        // ================= OUTPUT AREA =================
        output = new JTextArea(7, 30);
        output.setFont(new Font("Consolas", Font.PLAIN, 13));
        output.setEditable(false);
        output.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "System Output"
        ));

        add(new JScrollPane(output), BorderLayout.SOUTH);

        // ================= ACTIONS =================
        addBtn.addActionListener(e -> runBackend("ADD_EMERGENCY"));
        processBtn.addActionListener(e -> runBackend("PROCESS"));
        routeBtn.addActionListener(e -> runBackend("ROUTE"));
    }

    // ================= BACKEND CALL =================
    private void runBackend(String action) {
        try {
            String json = "{"
                    + "\"action\":\"" + action + "\","
                    + "\"name\":\"" + name.getText() + "\","
                    + "\"severity\":" + severity.getText() + ","
                    + "\"from\":\"" + from.getText() + "\","
                    + "\"to\":\"" + to.getText() + "\""
                    + "}";

            FileUtil.writeText(
                    BackendRunner.DATA_DIR + "emergency_input.json", json);

            BackendRunner.run("emergency");

            output.append(
                    FileUtil.readText(
                            BackendRunner.DATA_DIR + "emergency_output.json") + "\n");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ================= PROFESSIONAL CLASSIC BUTTON =================
    private JButton classicButton(String text, Color bg) {
        JButton b = new JButton(text);

        // TEXT
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);

        // BACKGROUND
        b.setBackground(bg);
        b.setOpaque(true);
        b.setContentAreaFilled(true);

        // SIZE
        b.setPreferredSize(new Dimension(240, 45));

        // CLASSIC DESKTOP LOOK
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        return b;
    }
}
