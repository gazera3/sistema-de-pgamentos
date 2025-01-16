package com.accountmanager;

import com.accountmanager.model.Account;
import com.accountmanager.service.AccountBlockService;
import com.accountmanager.service.AccountService;
import com.accountmanager.service.NotificationService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
public class Main {
    public static void main(String[] args) {
        // Inicializa serviços na ordem correta
        NotificationService notificationService = new NotificationService();
        AccountBlockService blockService = new AccountBlockService(notificationService);
        AccountService accountService = new AccountService(notificationService, blockService);

        try {
            // Criar uma nova conta com vencimento em 3 dias
            Account account = accountService.createAccount(
                    "João Silva",
                    new BigDecimal("1000.00"),
                    LocalDateTime.now().plusDays(3)
            );

            // Sistema verifica status inicial
            accountService.checkAccountsStatus();

            // Recebe pagamento parcial
            accountService.receivePayment(account.getId(), new BigDecimal("500.00"));

            // Simula vencimento
            account.setPaymentDueDate(LocalDateTime.now().minusDays(1));
            accountService.checkAccountsStatus();

            // Tenta fazer pagamento com conta bloqueada (agora vai funcionar e desbloquear automaticamente)
            accountService.receivePayment(account.getId(), new BigDecimal("500.00"));

            // Lista todas as notificações da conta
            notificationService.getNotificationsForAccount(account.getId())
                    .forEach(notification ->
                            log.info("Notificação: {} - {}",
                                    notification.getType(),
                                    notification.getMessage()));

        } catch (Exception e) {
            log.error("Erro ao processar operações: ", e);
        }
    }
}