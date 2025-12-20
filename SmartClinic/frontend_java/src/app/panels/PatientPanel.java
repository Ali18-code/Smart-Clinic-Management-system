package app.panels;

import app.util.BackendRunner;
import app.util.FileUtil;
import app.util.SimpleJson;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer; // Import needed for header fix
import javax.swing.table.DefaultTableModel;

public class PatientPanel extends JPanel {

    private JTextField id, name, age, disease, date;
    private DefaultTableModel model;

    public PatientPanel() {
        // Increased padding around the whole panel for a modern look
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 244, 248));

        // ---------------- FORM ----------------
        JPanel form = new JPanel(new GridLayout(2, 5, 15, 5));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Labels with better fonts
        Font labelFont = new Font("Segoe UI", Font.BOLD, 12);
        String[] labels = {"Patient ID", "Name", "Age", "Disease", "Appointment Date"};
        for (String s : labels) {
            JLabel lbl = new JLabel(s);
            lbl.setFont(labelFont);
            lbl.setForeground(new Color(70, 80, 90));
            form.add(lbl);
        }

        // Initialize fields with the fix
        id = createStyledField();
        name = createStyledField();
        age = createStyledField();
        disease = createStyledField();
        date = createStyledField();

        form.add(id);
        form.add(name);
        form.add(age);
        form.add(disease);
        form.add(date);

        add(form, BorderLayout.NORTH);

        // ---------------- BUTTONS ----------------
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        buttonContainer.setOpaque(false);

        JButton addBtn    = createModernButton("Add", new Color(46, 204, 113));    // Green
        JButton delBtn    = createModernButton("Delete", new Color(231, 76, 60)); // Red
        JButton searchBtn = createModernButton("Search", new Color(52, 152, 219)); // Blue
        JButton viewBtn   = createModernButton("View All", new Color(52, 73, 94)); // Dark Blue
        JButton backBtn   = createModernButton("Back", new Color(149, 165, 166));  // Gray

        buttonContainer.add(addBtn);
        buttonContainer.add(delBtn);
        buttonContainer.add(searchBtn);
        buttonContainer.add(viewBtn);
        buttonContainer.add(backBtn);

        add(buttonContainer, BorderLayout.CENTER);

        // ---------------- TABLE ----------------
        model = new DefaultTableModel(
                new String[]{"ID", "Name", "Age", "Disease", "Date"}, 0);

        JTable table = new JTable(model);
        styleTable(table); // Apply the header fix here
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(800, 250));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
        
        // Add Double-Click Listener to load data into form
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        id.setText(model.getValueAt(row, 0).toString());
                        name.setText(model.getValueAt(row, 1).toString());
                        age.setText(model.getValueAt(row, 2).toString());
                        disease.setText(model.getValueAt(row, 3).toString());
                        date.setText(model.getValueAt(row, 4).toString());
                    }
                }
            }
        });

        add(scroll, BorderLayout.SOUTH);

        // ---------------- ACTIONS ----------------
        addBtn.addActionListener(e -> handleAdd());
        viewBtn.addActionListener(e -> handleView());
        backBtn.addActionListener(e -> {
            Container parent = getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout) {
                ((CardLayout) parent.getLayout()).show(parent, "DashboardPanel");
            }
        });
    }

    // ---------------------------------------------------------
    // FIX #1: Ensure TextFields have a default size (15 columns)
    // ---------------------------------------------------------
    private JTextField createStyledField() {
        JTextField field = new JTextField(15); // Fix: Set column width to 15
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    // ---------------------------------------------------------
    // FIX #2: Custom Renderer for Table Header Visibility
    // ---------------------------------------------------------
    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setGridColor(new Color(230, 230, 230));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Create the custom renderer to force Blue Background + White Text
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(52, 152, 219));
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 13));
        headerRenderer.setHorizontalAlignment(JLabel.LEFT);
        
        // Add padding
        headerRenderer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(200, 200, 200)), 
            BorderFactory.createEmptyBorder(0, 10, 0, 0)
        ));

        // Apply renderer to all columns
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
    }

    private JButton createModernButton(String text, Color baseColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(baseColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 40));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(baseColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(baseColor);
            }
        });

        return btn;
    }

    private void handleAdd() {
        try {
            String json = "{" +
                "\"action\":\"ADD\"," +
                "\"id\":" + id.getText().trim() + "," +
                "\"name\":\"" + SimpleJson.esc(name.getText()) + "\"," +
                "\"age\":" + age.getText().trim() + "," +
                "\"disease\":\"" + SimpleJson.esc(disease.getText()) + "\"," +
                "\"date\":\"" + SimpleJson.esc(date.getText()) + "\"" +
                "}";

            FileUtil.writeText(BackendRunner.DATA_DIR + "patient_input.json", json);
            BackendRunner.run("patient");

            String response = FileUtil.readText(BackendRunner.DATA_DIR + "patient_output.json");
            if (SimpleJson.getBool(response, "ok", false)) {
                JOptionPane.showMessageDialog(this, "Patient Added Successfully!");
                handleView();
                id.setText(""); name.setText(""); age.setText(""); disease.setText(""); date.setText("");
            } else {
                String msg = SimpleJson.getString(response, "message");
                JOptionPane.showMessageDialog(this, "Error: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleView() {
        try {
            FileUtil.writeText(BackendRunner.DATA_DIR + "patient_input.json", "{\"action\":\"VIEW_ALL\"}");
            BackendRunner.run("patient");

            String json = FileUtil.readText(BackendRunner.DATA_DIR + "patient_output.json");
            model.setRowCount(0);

            int start = json.indexOf("\"patients\":[");
            if (start != -1) {
                String list = json.substring(start + 12);
                int end = list.lastIndexOf("]");
                if (end != -1) {
                    list = list.substring(0, end);
                    if (list.trim().isEmpty()) return;
                    
                    String[] items = list.split("\\},\\{");
                    for (String item : items) {
                        String s = "{" + item.replace("{", "").replace("}", "") + "}";
                        model.addRow(new Object[]{
                            SimpleJson.getInt(s, "id", 0),
                            SimpleJson.getString(s, "name"),
                            SimpleJson.getInt(s, "age", 0),
                            SimpleJson.getString(s, "disease"),
                            SimpleJson.getString(s, "date")
                        });
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
