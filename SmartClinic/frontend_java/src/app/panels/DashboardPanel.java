package app.panels;

import app.util.BackendRunner;
import app.util.FileUtil;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class DashboardPanel extends JPanel {

    private JLabel totalPatients, totalEmergencies, criticalCases;
    private boolean loading = false;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 250));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        loadStats();
        new Timer(10000, e -> loadStats()).start();
    }

    /* ================= HEADER ================= */

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel title = new JLabel("Smart Clinic Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(30, 58, 138)); // Medical blue

        JLabel subtitle = new JLabel("Live patient and emergency overview");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(75, 85, 99));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    /* ================= MAIN ================= */

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setOpaque(false);

        main.add(createSummaryPanel(), BorderLayout.NORTH);
        main.add(createActionsPanel(), BorderLayout.CENTER);

        return main;
    }

    /* ================= SUMMARY ================= */

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 15));
        panel.setOpaque(false);

        totalPatients = valueLabel();
        totalEmergencies = valueLabel();
        criticalCases = valueLabel();

        panel.add(summaryCard("Total Patients", totalPatients, new Color(37, 99, 235)));
        panel.add(summaryCard("Emergency Cases", totalEmergencies, new Color(220, 38, 38)));
        panel.add(summaryCard("Critical Alerts", criticalCases, new Color(202, 138, 4)));

        return panel;
    }

    private JPanel summaryCard(String title, JLabel value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(229, 231, 235)));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setForeground(new Color(55, 65, 81));

        value.setForeground(accent);

        JPanel pad = new JPanel(new BorderLayout());
        pad.setOpaque(false);
        pad.setBorder(new EmptyBorder(12, 14, 12, 14));
        pad.add(t, BorderLayout.NORTH);
        pad.add(value, BorderLayout.CENTER);

        card.add(pad);
        return card;
    }

    private JLabel valueLabel() {
        JLabel l = new JLabel("0");
        l.setFont(new Font("Segoe UI", Font.BOLD, 34));
        l.setForeground(new Color(31, 41, 55));
        return l;
    }

    /* ================= ACTIONS ================= */

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        panel.add(actionButton("Patient Management", "PatientPanel"));
        panel.add(actionButton("Emergency Room", "EmergencyPanel"));
        panel.add(actionButton("Reports & Analytics", "ReportsPanel"));
        panel.add(refreshButton());

        return panel;
    }

    private JButton actionButton(String text, String target) {
        JButton b = new JButton(text);
        styleActionButton(b);
        b.addActionListener(e ->
                ((CardLayout) getParent().getLayout()).show(getParent(), target)
        );
        return b;
    }

    private JButton refreshButton() {
        JButton b = new JButton("Refresh Live Data");
        styleActionButton(b);
        b.addActionListener(e -> loadStats());
        return b;
    }

    private void styleActionButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(Color.WHITE);
        b.setForeground(new Color(30, 58, 138));
        b.setBorder(new LineBorder(new Color(203, 213, 225)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(0, 52));
    }

    /* ================= DATA ================= */

    private void loadStats() {
        if (loading) return;
        loading = true;

        new Thread(() -> {
            try {
                FileUtil.writeText(
                        BackendRunner.DATA_DIR + "patient_input.json",
                        "{\"action\":\"VIEW_ALL\"}"
                );
                BackendRunner.run("patient");

                String p = safeRead(
                        BackendRunner.DATA_DIR + "patients_db.json",
                        BackendRunner.DATA_DIR + "patient_output.json"
                );
                String e = safeRead(
                        BackendRunner.DATA_DIR + "emergency_db.json",
                        BackendRunner.DATA_DIR + "emergency_output.json"
                );

                int tp = count(p, "\"id\"");
                int te = count(e, "\"name\"");
                int crit = countSeverity(e, 7);

                SwingUtilities.invokeLater(() -> {
                    totalPatients.setText(String.valueOf(tp));
                    totalEmergencies.setText(String.valueOf(te));
                    criticalCases.setText(String.valueOf(crit));
                });

            } catch (Exception ignored) {
            } finally {
                loading = false;
            }
        }).start();
    }

    private String safeRead(String p1, String p2) {
        try { return FileUtil.readText(p1); }
        catch (Exception e1) {
            try { return FileUtil.readText(p2); }
            catch (Exception e2) { return ""; }
        }
    }

    private int count(String text, String token) {
        if (text == null) return 0;
        int c = 0, i = 0;
        while ((i = text.indexOf(token, i)) != -1) {
            c++; i += token.length();
        }
        return c;
    }

    private int countSeverity(String json, int threshold) {
        if (json == null) return 0;
        int count = 0, i = 0;
        while ((i = json.indexOf("\"severity\"", i)) != -1) {
            int j = json.indexOf(":", i) + 1;
            while (j < json.length() && !Character.isDigit(json.charAt(j))) j++;
            StringBuilder n = new StringBuilder();
            while (j < json.length() && Character.isDigit(json.charAt(j))) n.append(json.charAt(j++));
            try {
                if (!n.toString().isEmpty() && Integer.parseInt(n.toString()) >= threshold)
                    count++;
            } catch (Exception ignore) {}
            i = j;
        }
        return count;
    }
}
