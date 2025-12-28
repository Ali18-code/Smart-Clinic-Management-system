package app.panels;

import app.util.BackendRunner;
import app.util.SimpleJson;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Vector;

public class ReportsPanel extends JPanel {

    private JPanel mainPanel;   // ❤️ CardLayout parent (optional)

    private JLabel totalPatientsLabel;
    private JLabel normalPatientsLabel;
    private JLabel emergencyPatientsLabel;

    private DefaultTableModel tableModel;
    private JTable emergenciesTable;

    private JPanel contentPanel;
    private JLabel statusLabel;
    private JButton generateBtn;

    /* ========= CONSTRUCTORS ========= */

    // 🔥 Allows ReportsPanel() to still work
    public ReportsPanel() {
        this(null);
    }

    // 🔥 Preferred constructor when using CardLayout
    public ReportsPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout(18, 18));
        setBorder(new EmptyBorder(20, 30, 20, 30));
        setBackground(new Color(245, 248, 250));

        add(createHeader(), BorderLayout.NORTH);

        contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);

        add(createBottomPanel(), BorderLayout.SOUTH);

        showStatus("Click “Generate Report” to view clinic analytics.");
    }

    /* ========= HEADER ========= */
    private JPanel createHeader() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Clinic Reports & Analytics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel subtitle = new JLabel("Patient statistics and emergency overview");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(107, 114, 128));

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(59, 130, 246));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);

        // ❤️ Works in BOTH CardLayout & Tabbed UI
        backButton.addActionListener(e -> {

            if (mainPanel != null && mainPanel.getLayout() instanceof CardLayout layout) {
                layout.show(mainPanel, "DashboardPanel");
                return;
            }

            JTabbedPane tabs = (JTabbedPane)
                    SwingUtilities.getAncestorOfClass(JTabbedPane.class, ReportsPanel.this);

            if (tabs != null)
                tabs.setSelectedIndex(0);
            else
                System.out.println("No navigation container found");
        });

        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.add(title, BorderLayout.NORTH);
        left.add(subtitle, BorderLayout.SOUTH);

        panel.add(left, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.EAST);

        return panel;
    }

    /* ========= STATUS VIEW ========= */

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

    /* ========= REPORT VIEW ========= */

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

    private JPanel metricCard(String title, JLabel value, Color accent) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(229, 231, 235)));

        JLabel t = new JLabel(title);
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

    /* ========= TABLE ========= */

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

            public boolean isCellEditable(int r, int c) {
                return false;
            }
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

    /* ========= GENERATE BUTTON ========= */

    private JPanel createBottomPanel() {

        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setOpaque(false);

        generateBtn = new JButton("Generate Report");
        generateBtn.setPreferredSize(new Dimension(200, 40));
        generateBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setBackground(new Color(16, 185, 129));

        generateBtn.addActionListener(e -> generateReport());

        p.add(generateBtn);
        return p;
    }

    /* ========= BACKEND CALL ========= */

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
                    return null;
                }
            }

            protected void done() {

                generateBtn.setEnabled(true);

                try {
                    String json = get();

                    if (json == null || json.isEmpty()) {
                        showStatus("❌ Backend returned no data");
                        return;
                    }

                    showReportData();
                    updateUI(json);
                }
                catch (Exception e) {
                    showStatus("❌ Failed to process report");
                }
            }
        };

        worker.execute();
    }

    private void updateUI(String json) {

        totalPatientsLabel.setText("" + SimpleJson.getInt(json, "totalPatients", 0));
        normalPatientsLabel.setText("" + SimpleJson.getInt(json, "normalPatients", 0));
        emergencyPatientsLabel.setText("" + SimpleJson.getInt(json, "emergencyPatients", 0));

        tableModel.setRowCount(0);

        SimpleJson root = new SimpleJson(json);
        ArrayList<SimpleJson> list = root.getJsonArray("topEmergencies");

        int i = 1;
        for (SimpleJson e : list) {

            Vector<Object> row = new Vector<>();
            row.add(i++);
            row.add(e.getString("name"));
            row.add(e.getInt("severity", 0));

            tableModel.addRow(row);
        }
    }
}
