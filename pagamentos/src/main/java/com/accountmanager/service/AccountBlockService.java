package com.accountmanager.service;

import com.accountmanager.model.Account;
import com.accountmanager.model.NotificationType;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AccountBlockService {
    private final NotificationService notificationService;
    private final List<Account> blockedAccounts = new ArrayList<>();

    public AccountBlockService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void blockAccount(Account account, String reason) {
        if (!account.isBlocked()) {
            // Registra o bloqueio
            account.setBlocked(true);
            account.setBlockedDate(LocalDateTime.now());
            account.setBlockReason(reason);
            blockedAccounts.add(account);

            // Envia notificação de bloqueio
            String blockMessage = String.format("""
                CONTA BLOQUEADA
                Titular: %s
                Motivo: %s
                Data do Bloqueio: %s
                Valor Pendente: R$ %.2f
                
                Para desbloquear a conta, efetue o pagamento do valor pendente.
                """,
                    account.getOwnerName(),
                    reason,
                    account.getBlockedDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    account.getExpectedPayment().subtract(account.getBalance())
            );

            notificationService.sendWarning(account.getId(), blockMessage, NotificationType.BLOCKED);
            log.warn("Conta bloqueada: {} - {}", account.getId(), reason);
        }
    }

    public void unblockAccount(Account account) {
        if (account.isBlocked()) {
            // Verifica se pode desbloquear
            BigDecimal pendingAmount = account.getExpectedPayment().subtract(account.getBalance());
            if (pendingAmount.compareTo(BigDecimal.ZERO) > 0) {
                String message = String.format("Não é possível desbloquear. Valor pendente: R$ %.2f", pendingAmount);
                throw new IllegalStateException(message);
            }

            // Realiza o desbloqueio
            account.setBlocked(false);
            account.setBlockedDate(null);
            account.setBlockReason(null);
            blockedAccounts.remove(account);

            // Envia notificação de desbloqueio
            String unblockMessage = String.format("""
                CONTA DESBLOQUEADA
                Titular: %s
                Data do Desbloqueio: %s
                Status: Pagamento Regularizado
                
                Sua conta foi desbloqueada após a confirmação do pagamento.
                Agradecemos a regularização.
                """,
                    account.getOwnerName(),
                    LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );

            notificationService.sendWarning(account.getId(), unblockMessage, NotificationType.WARNING);
            log.info("Conta desbloqueada: {}", account.getId());
        }
    }

    public boolean isBlocked(Account account) {
        return blockedAccounts.contains(account);
    }

    public List<Account> getBlockedAccounts() {
        return new ArrayList<>(blockedAccounts);
    }
}