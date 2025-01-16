package com.accountmanager.ui;

import com.accountmanager.model.Account;
import com.accountmanager.service.AccountBlockService;
import com.accountmanager.service.AccountService;
import com.accountmanager.service.NotificationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {
    private final AccountService accountService;
    private final AccountBlockService blockService;
    private final NotificationService notificationService;

    private JTable accountsTable;
    private DefaultTableModel tableModel;
    private JTextArea notificationsArea;

    public MainFrame() {
        // Inicializa serviços na ordem correta
        notificationService = new NotificationService();
        blockService = new AccountBlockService(notificationService);
        accountService = new AccountService(notificationService, blockService);

        // Configura a janela principal
        setTitle("Sistema de Gestão de Contas");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Cria os componentes da interface
        createComponents();

        // Atualiza a tabela inicialmente
        refreshAccountsTable();
    }

    private void createComponents() {
        // Painel principal com layout de borda
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel superior com botões
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        // Painel central dividido entre tabela e notificações
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createTablePanel());
        splitPane.setRightComponent(createNotificationsPanel());
        splitPane.setResizeWeight(0.7);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Adiciona o painel principal à janela
        add(mainPanel);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = UIUtils.createButton("Nova Conta", null);
        addButton.addActionListener(e -> showNewAccountDialog());

        JButton paymentButton = UIUtils.createButton("Registrar Pagamento", null);
        paymentButton.addActionListener(e -> showPaymentDialog());

        JButton checkButton = UIUtils.createButton("Verificar Contas", null);
        checkButton.addActionListener(e -> {
            accountService.checkAccountsStatus();
            refreshAccountsTable();
            refreshNotifications();
        });

        panel.add(addButton);
        panel.add(paymentButton);
        panel.add(checkButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Contas"));

        // Cria o modelo da tabela
        String[] columns = {"ID", "Titular", "Saldo", "Valor Esperado", "Vencimento", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Cria a tabela
        accountsTable = new JTable(tableModel);
        UIUtils.setupTable(accountsTable);

        // Adiciona listener para seleção
        accountsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refreshNotifications();
            }
        });

        JScrollPane scrollPane = new JScrollPane(accountsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createNotificationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Notificações"));

        notificationsArea = new JTextArea();
        UIUtils.setupNotificationArea(notificationsArea);

        JScrollPane scrollPane = new JScrollPane(notificationsArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void showNewAccountDialog() {
        JTextField nameField = new JTextField(20);
        JTextField amountField = new JTextField(10);
        JTextField daysField = new JTextField(5);

        JPanel panel = UIUtils.createFormPanel();
        GridBagConstraints gbc = UIUtils.createGridConstraints();

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nome do Titular:"), gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Valor Esperado:"), gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Dias até Vencimento:"), gbc);
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(daysField, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Nova Conta", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                BigDecimal amount = new BigDecimal(amountField.getText());
                int days = Integer.parseInt(daysField.getText());

                Account account = accountService.createAccount(
                        name,
                        amount,
                        LocalDateTime.now().plusDays(days)
                );

                refreshAccountsTable();
                UIUtils.showSuccess(this, "Conta criada com sucesso!\nID: " + account.getId());

            } catch (Exception e) {
                UIUtils.showError(this, "Erro ao criar conta: " + e.getMessage());
            }
        }
    }

    private void showPaymentDialog() {
        if (accountsTable.getSelectedRow() == -1) {
            UIUtils.showError(this, "Selecione uma conta na tabela primeiro");
            return;
        }

        String accountId = (String) tableModel.getValueAt(
                accountsTable.getSelectedRow(), 0);

        JTextField amountField = new JTextField(10);
        JPanel panel = UIUtils.createFormPanel();
        GridBagConstraints gbc = UIUtils.createGridConstraints();

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Valor do Pagamento:"), gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(amountField, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Registrar Pagamento", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                BigDecimal amount = new BigDecimal(amountField.getText());
                accountService.receivePayment(accountId, amount);
                refreshAccountsTable();
                refreshNotifications();

                UIUtils.showSuccess(this, "Pagamento registrado com sucesso!");

            } catch (Exception e) {
                UIUtils.showError(this, "Erro ao registrar pagamento: " + e.getMessage());
            }
        }
    }

    private void refreshAccountsTable() {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Account account : accountService.getAllAccounts()) {
            Object[] rowData = {
                    account.getId(),
                    account.getOwnerName(),
                    String.format("R$ %.2f", account.getBalance()),
                    String.format("R$ %.2f", account.getExpectedPayment()),
                    account.getPaymentDueDate().format(formatter),
                    account.isBlocked() ? "BLOQUEADA" : "ATIVA"
            };
            tableModel.addRow(rowData);
        }
    }

    private void refreshNotifications() {
        StringBuilder notifications = new StringBuilder();

        if (accountsTable.getSelectedRow() != -1) {
            String accountId = (String) tableModel.getValueAt(
                    accountsTable.getSelectedRow(), 0);

            notificationService.getNotificationsForAccount(accountId)
                    .forEach(notification ->
                            notifications.append(notification.getMessage())
                                    .append("\n-------------------\n"));
        }

        notificationsArea.setText(notifications.toString());
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}