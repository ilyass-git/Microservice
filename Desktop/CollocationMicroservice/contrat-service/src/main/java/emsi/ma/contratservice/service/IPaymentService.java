package emsi.ma.contratservice.service;

import emsi.ma.contratservice.domain.entity.Payment;
import java.util.List;
import java.util.Optional;

public interface IPaymentService {
    Payment create(Payment payment);
    Optional<Payment> getById(Long id);
    List<Payment> getAll();
    List<Payment> getByContractId(Long contractId);
    Payment update(Long id, Payment payment);
    void delete(Long id);
}




