package app.panels;

import app.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PatientPanel extends JPanel {

    private JTextField id, name, age, disease, date;
    private DefaultTableModel model;

    public PatientPanel() {
        setLayout(new BorderLayout(10,10));
        setBackground(new Color(245,247,250));

        JPanel form = new JPanel(new GridLayout(2,5,10,10));
        form.setBorder(BorderFactory.createTitledBorder("Patient Information"));

        id = new JTextField();
        name = new JTextField();
        age = new JTextField();
        disease = new JTextField();
        date = new JTextField();

        form.add(new JLabel("Patient ID"));
        form.add(new JLabel("Name"));
        form.add(new JLabel("Age"));
        form.add(new JLabel("Disease"));
        form.add(new JLabel("Appointment Date"));

        form.add(id);
        form.add(name);
        form.add(age);
        form.add(disease);
        form.add(date);

        add(form, BorderLayout.NORTH);

        JPanel buttons = new JPanel();
        JButton addBtn = styledButton("Add Patient", new Color(46,204,113));
        JButton delBtn = styledButton("Delete Patient", new Color(231,76,60));
        JButton searchBtn = styledButton("Search Patient", new Color(52,152,219));
        JButton viewBtn = styledButton("View All", new Color(127,140,141));

        buttons.add(addBtn);
        buttons.add(delBtn);
        buttons.add(searchBtn);
        buttons.add(viewBtn);
        add(buttons, BorderLayout.CENTER);

        model = new DefaultTableModel(
                new String[]{"ID","Name","Age","Disease","Date"}, 0);
        JTable table = new JTable(model);
        styleTable(table);
        add(new JScrollPane(table), BorderLayout.SOUTH);

        addBtn.addActionListener(e -> handleAdd());
        viewBtn.addActionListener(e -> handleView());
    }

    private void handleAdd() {
        try {
            String json = "{"
                    + "\"action\":\"ADD\","
                    + "\"id\":" + id.getText() + ","
                    + "\"name\":\"" + name.getText() + "\","
                    + "\"age\":" + age.getText() + ","
                    + "\"disease\":\"" + disease.getText() + "\","
                    + "\"date\":\"" + date.getText() + "\""
                    + "}";

            FileUtil.writeText(BackendRunner.DATA_DIR + "patient_input.json", json);
            BackendRunner.run("patient");
            JOptionPane.showMessageDialog(this, "Patient added successfully");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void handleView() {
        try {
            FileUtil.writeText(BackendRunner.DATA_DIR + "patient_input.json",
                    "{\"action\":\"VIEW_ALL\"}");
            BackendRunner.run("patient");
            String out = FileUtil.readText(
                    BackendRunner.DATA_DIR + "patient_output.json");

            model.setRowCount(0);
            // simple display only
            if(out.contains("patients")) {
                JOptionPane.showMessageDialog(this,"Patients loaded from backend");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
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

    private void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(52,152,219));
        table.getTableHeader().setForeground(Color.WHITE);
    }
}
