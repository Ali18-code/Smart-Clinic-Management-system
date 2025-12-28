package app.panels;

import app.util.BackendRunner;
import app.util.SimpleJson;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ReportsPanel extends JPanel {

    private JLabel totalPatientsLabel;
    private JLabel normalPatientsLabel;
    private JLabel emergencyPatientsLabel;

    private DefaultTableModel tableModel;
    private JTable emergenciesTable;

    private JPanel contentPanel;
    private JLabel statusLabel;
    private JButton generateBtn;

    public ReportsPanel() {

        setLayout(new BorderLayout(18, 18));
        setBorder(new EmptyBorder(20, 30, 20, 30));
        setBackground(new Color(245, 248, 250));

        add(createHeader(), BorderLayout.NORTH);

        contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);

        add(createBottomPanel(), BorderLayout.SOUTH);

        showStatus("Click “Generate Report” to view clinic analytics.");
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Clinic Reports");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel subtitle = new JLabel("Patient statistics and emergency analysis");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(107, 114, 128));

        JButton backButton = new JButton("←  Back");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(59, 130, 246));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(110, 36));
        backButton.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        // ⭐ SAFE BACK BUTTON — works even when wrapped in panels
        backButton.addActionListener(e -> {

            JTabbedPane tabs = (JTabbedPane)
                    SwingUtilities.getAncestorOfClass(JTabbedPane.class, ReportsPanel.this);

            if (tabs != null) {
                // Go to first tab (Dashboard)
                tabs.setSelectedIndex(0);

                // OR if your tab name is "Dashboard", use:
                // int idx = tabs.indexOfTab("Dashboard");
                // if (idx >= 0) tabs.setSelectedIndex(idx);
            } else {
                System.out.println("No JTabbedPane found — cannot go back");
            }
        });

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.NORTH);
        titlePanel.add(subtitle, BorderLayout.SOUTH);

        panel.add(titlePanel, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createContentPanel() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        statusLabel.setForeground(new Color(75, 85, 99));

        panel.add(statusLabel, BorderLayout.CENTER);
        return panel;
    }

    private void showStatus(String text) {
        contentPanel.removeAll();
        statusLabel.setText(text);
        contentPanel.add(statusLabel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showReportData() {

        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout(20, 24));

        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 16));
        stats.setOpaque(false);

        stats.add(metricCard("Total Patients",
                totalPatientsLabel = new JLabel("0"),
                new Color(37, 99, 235)));

        stats.add(metricCard("General Ward",
                normalPatientsLabel = new JLabel("0"),
                new Color(21, 128, 61)));

        stats.add(metricCard("Emergency Ward",
                emergencyPatientsLabel = new JLabel("0"),
                new Color(185, 28, 28)));

        contentPanel.add(stats, BorderLayout.NORTH);
        contentPanel.add(createTableSection(), BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel metricCard(String name, JLabel value, Color accent) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(229, 231, 235)));

        JLabel t = new JLabel(name);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setForeground(new Color(107, 114, 128));

        value.setFont(new Font("Segoe UI", Font.BOLD, 34));
        value.setForeground(accent);

        JPanel pad = new JPanel(new BorderLayout());
        pad.setOpaque(false);
        pad.setBorder(new EmptyBorder(14, 16, 14, 16));
        pad.add(t, BorderLayout.NORTH);
        pad.add(value, BorderLayout.CENTER);

        card.add(pad);

        return card;
    }

    private JPanel createTableSection() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Top Emergency Cases");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        panel.add(title, BorderLayout.NORTH);
        panel.add(createTable(), BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane createTable() {

        tableModel = new DefaultTableModel(
                new String[]{"#", "Patient Name", "Severity"}, 0) {

            public boolean isCellEditable(int r, int c) { return false; }
        };

        emergenciesTable = new JTable(tableModel);
        emergenciesTable.setRowHeight(32);
        emergenciesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emergenciesTable.setShowGrid(false);

        JTableHeader header = emergenciesTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(243, 244, 246));

        emergenciesTable.getColumnModel()
                .getColumn(2)
                .setCellRenderer(new SeverityRenderer());

        return new JScrollPane(emergenciesTable);
    }

    private JPanel createBottomPanel() {

        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setOpaque(false);

        generateBtn = new JButton("Generate Report");
        generateBtn.setPreferredSize(new Dimension(200, 40));
        generateBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setBackground(new Color(16, 185, 129));
        generateBtn.setFocusPainted(false);
        generateBtn.setBorderPainted(false);
        generateBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        generateBtn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        generateBtn.addActionListener(e -> generateReport());

        p.add(generateBtn);
        return p;
    }

    private void generateReport() {

        generateBtn.setEnabled(false);
        showStatus("Generating report…");

        SwingWorker<String, Void> worker = new SwingWorker<>() {

            protected String doInBackground() {

                try {
                    BackendRunner.run("reports");

                    BufferedReader br = new BufferedReader(
                            new FileReader(BackendRunner.DATA_DIR + "reports_output.json"));

                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null)
                        sb.append(line);

                    br.close();
                    return sb.toString();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void done() {

                generateBtn.setEnabled(true);

                try {
                    String json = get();

                    if (json == null || json.isEmpty()) {
                        showStatus("❌ Backend returned no report data.");
                        return;
                    }

                    showReportData();
                    updateUI(json);
                }
                catch (Exception e) {
                    showStatus("❌ Failed to process report.");
                }
            }
        };

        worker.execute();
    }

    private void updateUI(String json) {

        int totalPatients = SimpleJson.getInt(json, "totalPatients", 0);
        if (totalPatients == 0) {
            showStatus("ⓘ Report generated, but no patient data was found.\n" +
                    "Please ensure 'patients_db.json' and 'emergency_db.json' contain data.");
            return;
        }

        totalPatientsLabel.setText("" + totalPatients);
        normalPatientsLabel.setText("" + SimpleJson.getInt(json, "normalPatients", 0));
        emergencyPatientsLabel.setText("" + SimpleJson.getInt(json, "emergencyPatients", 0));

        tableModel.setRowCount(0);

        SimpleJson root = new SimpleJson(json);
        ArrayList<SimpleJson> emergencies = root.getJsonArray("topEmergencies");

        int i = 1;
        for (SimpleJson emergency : emergencies) {
            Vector<Object> row = new Vector<>();
            row.add(i++);
            row.add(emergency.getString("name"));
            row.add(emergency.getInt("severity", 0));
            tableModel.addRow(row);
        }
    }

    private static class SeverityRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean s, boolean f, int r, int c) {

            super.getTableCellRendererComponent(t, v, s, f, r, c);

            setHorizontalAlignment(CENTER);
            setFont(getFont().deriveFont(Font.BOLD));

            int sev = Integer.parseInt(v.toString());

            if (sev >= 8) setForeground(new Color(185, 28, 28));
            else if (sev >= 5) setForeground(new Color(217, 119, 6));
            else setForeground(new Color(21, 128, 61));

            return this;
        }
    }
}
