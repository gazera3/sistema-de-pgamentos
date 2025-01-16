package com.accountmanager;

import com.accountmanager.model.Account;
import com.accountmanager.service.AccountBlockService;
import com.accountmanager.service.NotificationService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
public class BlockDemo {
    public static void main(String[] args) {
        // Inicializa os serviços
        NotificationService notificationService = new NotificationService();
        AccountBlockService blockService = new AccountBlockService(notificationService);

        try {
            // Cria uma conta de exemplo
            Account account = Account.builder()
                    .id("ACC123")
                    .ownerName("João Silva")
                    .balance(new BigDecimal("500.00"))
                    .expectedPayment(new BigDecimal("1000.00"))
                    .paymentDueDate(LocalDateTime.now().minusDays(1))
                    .blocked(false)
                    .build();

            log.info("Conta criada: {}", account.getOwnerName());

            // Bloqueia a conta por falta de pagamento
            blockService.blockAccount(account, "Pagamento não realizado no prazo");
            log.info("Status da conta após bloqueio: Bloqueada = {}", account.isBlocked());

            // Tenta desbloquear sem pagamento completo
            try {
                blockService.unblockAccount(account);
            } catch (IllegalStateException e) {
                log.error("Tentativa de desbloqueio falhou: {}", e.getMessage());
            }

            // Simula recebimento do pagamento completo
            account.setBalance(new BigDecimal("1000.00"));
            log.info("Pagamento recebido. Novo saldo: R$ {}", account.getBalance());

            // Desbloqueia a conta após pagamento
            blockService.unblockAccount(account);
            log.info("Status da conta após regularização: Bloqueada = {}", account.isBlocked());

            // Lista todas as notificações
            log.info("Histórico de notificações:");
            notificationService.getNotificationsForAccount(account.getId())
                    .forEach(notification ->
                            log.info("Tipo: {} - Mensagem: {}",
                                    notification.getType(),
                                    notification.getMessage()));

        } catch (Exception e) {
            log.error("Erro durante a demonstração: ", e);
        }
    }
}