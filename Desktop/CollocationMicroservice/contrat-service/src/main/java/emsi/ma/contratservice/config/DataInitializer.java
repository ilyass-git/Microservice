package emsi.ma.contratservice.config;

import emsi.ma.contratservice.domain.entity.Contract;
import emsi.ma.contratservice.domain.entity.ContractStatus;
import emsi.ma.contratservice.domain.entity.Payment;
import emsi.ma.contratservice.domain.entity.PaymentType;
import emsi.ma.contratservice.domain.entity.Tenant;
import emsi.ma.contratservice.repository.ContractRepository;
import emsi.ma.contratservice.repository.PaymentRepository;
import emsi.ma.contratservice.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ContractRepository contractRepository;
    private final PaymentRepository paymentRepository;
    private final TenantRepository tenantRepository;

    @Override
    public void run(String... args) {
        log.info("Initialisation des données de test pour Contrat Service...");
        initializeContracts();
        initializePayments();
        initializeTenants();
        log.info("Initialisation des données terminée.");
    }

    private void initializeContracts() {
        if (contractRepository.count() == 0) {
            log.info("Création des contrats de test...");
            
            Contract contract1 = Contract.builder()
                    .propertyId(1L)
                    .startDate(LocalDate.now().plusDays(1))
                    .endDate(LocalDate.now().plusMonths(12))
                    .status(ContractStatus.ACTIVE)
                    .build();
            contractRepository.save(contract1);

            Contract contract2 = Contract.builder()
                    .propertyId(1L)
                    .startDate(LocalDate.now().minusMonths(3))
                    .endDate(LocalDate.now().plusMonths(9))
                    .status(ContractStatus.ACTIVE)
                    .build();
            contractRepository.save(contract2);

            Contract contract3 = Contract.builder()
                    .propertyId(2L)
                    .startDate(LocalDate.now().plusDays(15))
                    .endDate(LocalDate.now().plusMonths(6))
                    .status(ContractStatus.DRAFT)
                    .build();
            contractRepository.save(contract3);

            log.info("3 contrats créés.");
        } else {
            log.info("Des contrats existent déjà. Aucune création.");
        }
    }

    private void initializePayments() {
        if (paymentRepository.count() == 0) {
            log.info("Création des paiements de test...");
            
            var contracts = contractRepository.findAll();
            if (contracts.size() >= 2) {
                Payment payment1 = Payment.builder()
                        .contractId(contracts.get(0).getId())
                        .amount(new BigDecimal("2000.00"))
                        .dueDate(LocalDate.now().plusDays(5))
                        .type(PaymentType.RENT)
                        .build();
                paymentRepository.save(payment1);

                Payment payment2 = Payment.builder()
                        .contractId(contracts.get(0).getId())
                        .amount(new BigDecimal("4000.00"))
                        .dueDate(LocalDate.now().plusMonths(1))
                        .type(PaymentType.DEPOSIT)
                        .build();
                paymentRepository.save(payment2);

                Payment payment3 = Payment.builder()
                        .contractId(contracts.get(1).getId())
                        .amount(new BigDecimal("1800.00"))
                        .dueDate(LocalDate.now().plusDays(10))
                        .type(PaymentType.RENT)
                        .build();
                paymentRepository.save(payment3);

                log.info("3 paiements créés.");
            }
        } else {
            log.info("Des paiements existent déjà. Aucune création.");
        }
    }

    private void initializeTenants() {
        if (tenantRepository.count() == 0) {
            log.info("Création des locataires de test...");
            
            var contracts = contractRepository.findAll();
            if (contracts.size() >= 2) {
                Tenant tenant1 = Tenant.builder()
                        .contractId(contracts.get(0).getId())
                        .userId(1L)
                        .roomId(1L)
                        .build();
                tenantRepository.save(tenant1);

                Tenant tenant2 = Tenant.builder()
                        .contractId(contracts.get(0).getId())
                        .userId(2L)
                        .roomId(2L)
                        .build();
                tenantRepository.save(tenant2);

                Tenant tenant3 = Tenant.builder()
                        .contractId(contracts.get(1).getId())
                        .userId(3L)
                        .roomId(null)
                        .build();
                tenantRepository.save(tenant3);

                log.info("3 locataires créés.");
            }
        } else {
            log.info("Des locataires existent déjà. Aucune création.");
        }
    }
}






