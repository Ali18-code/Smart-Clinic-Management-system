package app.panels;

import app.util.*;
import java.awt.*;
import javax.swing.*;

public class EmergencyPanel extends JPanel {

    private JTextField name, severity, from, to;
    private JTextArea output;

    public EmergencyPanel() {
        setLayout(new BorderLayout(10,10));
        setBackground(new Color(245,247,250));

        JPanel form = new JPanel(new GridLayout(2,4,10,10));
        form.setBorder(BorderFactory.createTitledBorder("Emergency Details"));

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

        JPanel buttons = new JPanel();
        JButton add = styledButton("Add Emergency Patient", new Color(231,76,60));
        JButton process = styledButton("Process Next", new Color(241,196,15));
        JButton route = styledButton("Show Shortest Route", new Color(52,152,219));

        buttons.add(add);
        buttons.add(process);
        buttons.add(route);
        add(buttons, BorderLayout.CENTER);

        output = new JTextArea();
        output.setFont(new Font("Consolas", Font.PLAIN, 13));
        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.SOUTH);

        add.addActionListener(e -> runBackend("ADD_EMERGENCY"));
        process.addActionListener(e -> runBackend("PROCESS"));
        route.addActionListener(e -> runBackend("ROUTE"));
    }

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

    private JButton styledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8,15,8,15));
        return b;
    }
}
