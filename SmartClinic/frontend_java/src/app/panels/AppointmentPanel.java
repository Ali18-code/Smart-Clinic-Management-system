package app.panels;

import app.util.BackendRunner;
import app.util.FileUtil;
import app.util.SimpleJson;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AppointmentPanel extends JPanel {

    private JTextField patientIdField, patientNameField, doctorField, timeField;
    private JTextArea logArea;

    public AppointmentPanel() {

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 244, 248));

        /* ---------------- HEADER ---------------- */
        JLabel title = new JLabel("Appointment Scheduling");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            Container parent = getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout) {
                ((CardLayout) parent.getLayout()).show(parent, "DashboardPanel");
            }
        });

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.add(backBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        /* ---------------- CENTER ---------------- */
        JPanel center = new JPanel(new GridLayout(1, 2, 20, 20));
        center.setOpaque(false);

        center.add(createFormPanel());
        center.add(createLogPanel());

        add(center, BorderLayout.CENTER);
    }

    /* ---------------- FORM ---------------- */
    private JPanel createFormPanel() {

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        panel.add(new JLabel("Patient ID"));
        patientIdField = new JTextField(15);
        panel.add(patientIdField);

        panel.add(new JLabel("Patient Name"));
        patientNameField = new JTextField(15);
        panel.add(patientNameField);

        panel.add(new JLabel("Doctor"));
        doctorField = new JTextField(15);
        panel.add(doctorField);

        panel.add(new JLabel("Time (HH:MM)"));
        timeField = new JTextField(15);
        panel.add(timeField);

        JButton bookBtn = new JButton("Book Appointment");
        JButton serveBtn = new JButton("Serve Next");

        bookBtn.addActionListener(e -> bookAppointment());
        serveBtn.addActionListener(e -> serveNextAppointment());

        panel.add(bookBtn);
        panel.add(serveBtn);

        return panel;
    }

    /* ---------------- LOG ---------------- */
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("System Log"));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));

        panel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        return panel;
    }

    /* ---------------- ACTIONS ---------------- */
    private void bookAppointment() {
        try {
            int id = Integer.parseInt(patientIdField.getText().trim());
            String name = patientNameField.getText().trim();
            String doctor = doctorField.getText().trim();
            String time = timeField.getText().trim();

            if (name.isEmpty() || doctor.isEmpty() || time.isEmpty()) {
                log("❌ All fields are required");
                return;
            }

            String json =
                "{\"action\":\"ADD\",\"id\":" + id +
                ",\"name\":\"" + SimpleJson.esc(name) +
                "\",\"doctor\":\"" + SimpleJson.esc(doctor) +
                "\",\"time\":\"" + SimpleJson.esc(time) + "\"}";

            FileUtil.writeText(BackendRunner.DATA_DIR + "appointment_input.json", json);
            BackendRunner.run("appointment");

            String out = FileUtil.readText(BackendRunner.DATA_DIR + "appointment_output.json");

            if (SimpleJson.getBool(out, "ok", false)) {
                log("✔ Appointment added for " + name);
                clearFields();
            } else {
                log("❌ " + SimpleJson.getString(out, "message"));
            }

        } catch (Exception ex) {
            log("❌ Error: " + ex.getMessage());
        }
    }

    private void serveNextAppointment() {
        try {
            FileUtil.writeText(
                BackendRunner.DATA_DIR + "appointment_input.json",
                "{\"action\":\"SERVE_NEXT\"}"
            );

            BackendRunner.run("appointment");
            String out = FileUtil.readText(BackendRunner.DATA_DIR + "appointment_output.json");

            if (SimpleJson.getBool(out, "ok", false)) {
                log("✔ Served: " + SimpleJson.getString(out, "served"));
            } else {
                log("❌ " + SimpleJson.getString(out, "message"));
            }

        } catch (Exception ex) {
            log("❌ Error: " + ex.getMessage());
        }
    }

    /* ---------------- UTIL ---------------- */
    private void log(String msg) {
        logArea.append(msg + "\n");
    }

    private void clearFields() {
        patientIdField.setText("");
        patientNameField.setText("");
        doctorField.setText("");
        timeField.setText("");
    }
}
