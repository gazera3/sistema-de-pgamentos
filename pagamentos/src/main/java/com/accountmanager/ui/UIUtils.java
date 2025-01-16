package com.accountmanager.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class UIUtils {
    public static void setupTable(JTable table) {
        // Configuração visual da tabela
        table.setRowHeight(25);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Configuração das cores
        table.setSelectionBackground(new Color(232, 242, 254));
        table.setSelectionForeground(Color.BLACK);

        // Centraliza o conteúdo das células
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    public static JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 30));
        return button;
    }

    public static void setupNotificationArea(JTextArea area) {
        area.setFont(new Font("Arial", Font.PLAIN, 12));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setMargin(new Insets(5, 5, 5, 5));
    }

    public static JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    public static GridBagConstraints createGridConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        return gbc;
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "Erro",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
    }
}