package com.accountmanager.service;

import com.accountmanager.model.Account;
import com.accountmanager.model.NotificationType;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class AccountService {
    private final Map<String, Account> accountsDatabase = new HashMap<>();
    private final NotificationService notificationService;
    private final AccountBlockService blockService;

    public AccountService(NotificationService notificationService, AccountBlockService blockService) {
        this.notificationService = notificationService;
        this.blockService = blockService;
    }

    public Account createAccount(String ownerName, BigDecimal expectedPayment, LocalDateTime paymentDueDate) {
        Account account = Account.builder()
                .id(generateId())
                .ownerName(ownerName)
                .balance(BigDecimal.ZERO)
                .expectedPayment(expectedPayment)
                .paymentDueDate(paymentDueDate)
                .blocked(false)
                .build();

        accountsDatabase.put(account.getId(), account);
        log.info("Criada nova conta para: {}", ownerName);
        return account;
    }

    public void receivePayment(String accountId, BigDecimal amount) {
        Account account = getAccountById(accountId);

        // Mesmo que a conta esteja bloqueada, aceita o pagamento
        account.setBalance(account.getBalance().add(amount));
        log.info("Recebido pagamento de {} para conta: {}", amount, accountId);

        // Verifica se o pagamento atingiu ou superou o valor esperado
        if (account.getBalance().compareTo(account.getExpectedPayment()) >= 0) {
            // Se a conta estava bloqueada, desbloqueia automaticamente
            if (account.isBlocked()) {
                try {
                    blockService.unblockAccount(account);
                    notificationService.sendWarning(accountId,
                            "Conta desbloqueada automaticamente após confirmação do pagamento total.",
                            NotificationType.WARNING);
                } catch (Exception e) {
                    log.error("Erro ao desbloquear conta: {}", e.getMessage());
                }
            }

            notificationService.sendWarning(accountId,
                    String.format("Pagamento total recebido. Saldo atual: R$ %.2f", account.getBalance()),
                    NotificationType.WARNING);
        } else {
            // Se ainda falta pagamento, informa o valor restante
            BigDecimal remaining = account.getExpectedPayment().subtract(account.getBalance());
            notificationService.sendWarning(accountId,
                    String.format("Pagamento parcial recebido. Valor restante: R$ %.2f", remaining),
                    NotificationType.WARNING);
        }
    }

    public void checkAccountsStatus() {
        LocalDateTime now = LocalDateTime.now();

        for (Account account : accountsDatabase.values()) {
            // Verifica se está vencida e não tem pagamento completo
            if (!account.isBlocked() &&
                    now.isAfter(account.getPaymentDueDate()) &&
                    account.getBalance().compareTo(account.getExpectedPayment()) < 0) {

                blockService.blockAccount(account, "Prazo de pagamento expirado");
            }
        }
    }

    public Account getAccountById(String accountId) {
        return Optional.ofNullable(accountsDatabase.get(accountId))
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada: " + accountId));
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accountsDatabase.values());
    }

    private String generateId() {
        return String.format("ACC%d", System.currentTimeMillis());
    }
}