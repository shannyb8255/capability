package com.cpk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class CapabilityCalculatorUI extends JFrame {
    private final JTextField filePathField;
    private final JButton browseButton;
    private final JButton runButton;
    private final JTextArea outputArea;

    public CapabilityCalculatorUI() {
        super("CPK Capability Calculator");

        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        filePathField = new JTextField();
        browseButton = new JButton("Select Excel File");
        runButton = new JButton("Run Analysis");

        topPanel.add(filePathField, BorderLayout.CENTER);
        topPanel.add(browseButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(runButton, BorderLayout.CENTER);

        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        browseButton.addActionListener(this::onBrowse);
        runButton.addActionListener(this::onRun);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onBrowse(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void onRun(ActionEvent e) {
        String filePath = filePathField.getText().trim();
        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a file first.");
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "File does not exist.");
            return;
        }

        outputArea.append("Running analysis on: " + file.getName() + "\n");
        ExcelCapabilityTemplate processor = new ExcelCapabilityTemplate();
        processor.processFile(file);
        outputArea.append("Analysis complete!\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CapabilityCalculatorUI::new);
    }
}
