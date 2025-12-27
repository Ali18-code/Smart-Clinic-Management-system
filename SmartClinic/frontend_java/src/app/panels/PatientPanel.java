package app.panels;

import app.util.BackendRunner;
import app.util.FileUtil;
import app.util.SimpleJson;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class PatientPanel extends JPanel {

    private JTextField id, name, age, disease, date;
    private JTextField searchField;

    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    public PatientPanel() {

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

        Font labelFont = new Font("Segoe UI", Font.BOLD, 12);
        String[] labels = {"Patient ID", "Name", "Age", "Disease", "Appointment Date"};
        for (String s : labels) {
            JLabel lbl = new JLabel(s);
            lbl.setFont(labelFont);
            lbl.setForeground(new Color(70, 80, 90));
            form.add(lbl);
        }

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

        // ---------------- TABLE & SORTER ----------------
        model = new DefaultTableModel(
                new String[]{"ID", "Name", "Age", "Disease", "Date"},
                0
        );

        table = new JTable(model);
        styleTable(table);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(800, 250));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));

        // Double click = load row into form
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int viewRow = table.getSelectedRow();
                    if (viewRow != -1) {
                        int row = table.convertRowIndexToModel(viewRow);
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

        // ---------------- BUTTONS ----------------
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        buttonContainer.setOpaque(false);

        JButton addBtn = createModernButton("Add", new Color(46, 204, 113));
        JButton delBtn = createModernButton("Delete", new Color(231, 76, 60));
        JButton viewBtn = createModernButton("View All", new Color(52, 73, 94));
        JButton backBtn = createModernButton("Back", new Color(149, 165, 166));

        searchField = new JTextField(15);
        searchField.setBorder(BorderFactory.createTitledBorder("Search Filter"));

        // SEARCH — works safely now 🙂
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        buttonContainer.add(addBtn);
        buttonContainer.add(delBtn);
        buttonContainer.add(searchField);
        buttonContainer.add(viewBtn);
        buttonContainer.add(backBtn);

        add(buttonContainer, BorderLayout.CENTER);

        // ---------------- ACTIONS ----------------
        addBtn.addActionListener(e -> handleAdd());
        viewBtn.addActionListener(e -> handleView());
        delBtn.addActionListener(e -> handleDelete());

        backBtn.addActionListener(e -> {
            Container parent = getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout) {
                ((CardLayout) parent.getLayout()).show(parent, "DashboardPanel");
            }
        });
    }

    // ---------------- HELPERS ----------------
    private void filter() {
        if (sorter == null) return;

        String text = searchField.getText();
        if (text.trim().isEmpty()) sorter.setRowFilter(null);
        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setGridColor(new Color(230, 230, 230));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(52, 152, 219));
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 13));
        headerRenderer.setHorizontalAlignment(JLabel.LEFT);

        headerRenderer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 10, 0, 0)
        ));

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
            public void mouseEntered(MouseEvent e) { btn.setBackground(baseColor.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(baseColor); }
        });

        return btn;
    }

    // ---------------- BACKEND ACTIONS ----------------
    private void handleAdd() {
        try {
            String json =
                    "{"
                            + "\"action\":\"ADD\","
                            + "\"id\":" + id.getText().trim() + ","
                            + "\"name\":\"" + SimpleJson.esc(name.getText()) + "\","
                            + "\"age\":" + age.getText().trim() + ","
                            + "\"disease\":\"" + SimpleJson.esc(disease.getText()) + "\","
                            + "\"date\":\"" + SimpleJson.esc(date.getText()) + "\""
                            + "}";

            FileUtil.writeText(BackendRunner.DATA_DIR + "patient_input.json", json);
            BackendRunner.run("patient");

            String response = FileUtil.readText(BackendRunner.DATA_DIR + "patient_output.json");

            if (SimpleJson.getBool(response, "ok", false)) {
                JOptionPane.showMessageDialog(this, "Patient Added Successfully!");
                handleView();
                id.setText("");
                name.setText("");
                age.setText("");
                disease.setText("");
                date.setText("");
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Error: " + SimpleJson.getString(response, "message"),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleView() {
        try {
            FileUtil.writeText(
                    BackendRunner.DATA_DIR + "patient_input.json",
                    "{\"action\":\"VIEW_ALL\"}"
            );

            BackendRunner.run("patient");

            String json = FileUtil.readText(BackendRunner.DATA_DIR + "patient_output.json");

            model.setRowCount(0);

            int start = json.indexOf("\"patients\":[");
            if (start == -1) return;

            String list = json.substring(start + 12);
            int end = list.lastIndexOf("]");
            if (end == -1) return;

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

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleDelete() {

        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient first.");
            return;
        }

        int row = table.convertRowIndexToModel(viewRow);

        int pid = (int) model.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete selected patient?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            String json =
                    "{"
                            + "\"action\":\"DELETE\","
                            + "\"id\":" + pid
                            + "}";

            FileUtil.writeText(BackendRunner.DATA_DIR + "patient_input.json", json);
            BackendRunner.run("patient");

            String response = FileUtil.readText(BackendRunner.DATA_DIR + "patient_output.json");

            if (SimpleJson.getBool(response, "ok", false)) {
                model.removeRow(row);
                JOptionPane.showMessageDialog(this, "Deleted Successfully 👍");
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete record.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Delete Error: " + ex.getMessage());
        }
    }
}
