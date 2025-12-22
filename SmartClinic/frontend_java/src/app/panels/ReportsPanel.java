package app.panels;

import app.util.BackendRunner;
import app.util.SimpleJson;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

public class ReportsPanel extends JPanel {

    private JLabel totalPatientsLabel;
    private JLabel normalPatientsLabel;
    private JLabel emergencyPatientsLabel;
    private DefaultTableModel tableModel;
    private JTable emergenciesTable;
    private JPanel contentPanel;
    private JLabel statusLabel;

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

    /* ================= HEADER ================= */
    private JComponent createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Clinic Reports");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(31, 41, 55));

        JLabel subtitle = new JLabel("Patient statistics and emergency analysis");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(107, 114, 128));

        panel.add(title, BorderLayout.NORTH);
        panel.add(subtitle, BorderLayout.SOUTH);
        return panel;
    }

    /* ================= CONTENT ================= */
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

    /* ================= REPORT UI ================= */
    private void showReportData() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout(20, 24));

        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 16));
        stats.setOpaque(false);

        stats.add(metricCard("Total Patients", totalPatientsLabel = new JLabel("0"), new Color(37, 99, 235)));
        stats.add(metricCard("General Ward", normalPatientsLabel = new JLabel("0"), new Color(21, 128, 61)));
        stats.add(metricCard("Emergency Ward", emergencyPatientsLabel = new JLabel("0"), new Color(185, 28, 28)));

        contentPanel.add(stats, BorderLayout.NORTH);
        contentPanel.add(createTableSection(), BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel metricCard(String title, JLabel value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
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

    /* ================= TABLE ================= */
    private JPanel createTableSection() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setOpaque(false);

        JLabel title = new JLabel("Top Emergency Cases");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(31, 41, 55));

        panel.add(title, BorderLayout.NORTH);
        panel.add(createEmergenciesTable(), BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane createEmergenciesTable() {
        tableModel = new DefaultTableModel(
                new String[]{"#", "Patient Name", "Severity"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        emergenciesTable = new JTable(tableModel) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(249, 250, 251) : Color.WHITE);
                    c.setForeground(new Color(31, 41, 55));
                } else {
                    c.setBackground(new Color(37, 99, 235));
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        };

        emergenciesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emergenciesTable.setRowHeight(34);
        emergenciesTable.setShowGrid(false);
        emergenciesTable.setFillsViewportHeight(true);

        JTableHeader header = emergenciesTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(243, 244, 246));
        header.setForeground(new Color(31, 41, 55));
        header.setBorder(new LineBorder(new Color(229, 231, 235)));
        header.setOpaque(true);

        emergenciesTable.getColumnModel().getColumn(2)
                .setCellRenderer(new SeverityCellRenderer());

        return new JScrollPane(emergenciesTable);
    }

    /* ================= BOTTOM ================= */
    private JPanel createBottomPanel() {
        JButton btn = new JButton("Generate Report");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(37, 99, 235));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 26, 10, 26));
        btn.addActionListener(e -> generateReport());

        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.add(btn);
        return p;
    }

    /* ================= BACKEND ================= */
    private void generateReport() {
        showStatus("Generating report… Please wait.");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            protected String doInBackground() throws Exception {
                BackendRunner.run("reports");
                BufferedReader br = new BufferedReader(
                        new FileReader(BackendRunner.DATA_DIR + "reports_output.json"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();
                return sb.toString();
            }

            protected void done() {
                try {
                    String json = get();
                    showReportData();
                    updateUIWithJson(json);
                } catch (Exception e) {
                    showStatus("Error generating report.");
                }
            }
        };
        worker.execute();
    }

    private void updateUIWithJson(String json) {
        totalPatientsLabel.setText(String.valueOf(SimpleJson.getInt(json, "totalPatients", 0)));
        normalPatientsLabel.setText(String.valueOf(SimpleJson.getInt(json, "normalPatients", 0)));
        emergencyPatientsLabel.setText(String.valueOf(SimpleJson.getInt(json, "emergencyPatients", 0)));

        tableModel.setRowCount(0);

        Pattern p = Pattern.compile("\"name\":\\s*\"(.*?)\",\\s*\"severity\":\\s*(\\d+)");
        Matcher m = p.matcher(json);
        int i = 1;
        while (m.find()) {
            Vector<Object> row = new Vector<>();
            row.add(i++);
            row.add(m.group(1));
            row.add(Integer.parseInt(m.group(2)));
            tableModel.addRow(row);
        }
    }

    /* ================= SEVERITY COLOR ================= */
    private static class SeverityCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean s, boolean f, int r, int c) {

            super.getTableCellRendererComponent(t, v, s, f, r, c);
            int sev = (Integer) v;
            setHorizontalAlignment(CENTER);
            setFont(getFont().deriveFont(Font.BOLD));

            if (sev >= 8) setForeground(s ? Color.WHITE : new Color(185, 28, 28));
            else if (sev >= 5) setForeground(s ? Color.WHITE : new Color(217, 119, 6));
            else setForeground(s ? Color.WHITE : new Color(21, 128, 61));

            return this;
        }
    }
}
