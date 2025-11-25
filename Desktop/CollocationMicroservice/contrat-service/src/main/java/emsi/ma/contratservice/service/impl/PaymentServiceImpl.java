package emsi.ma.contratservice.service.impl;

import emsi.ma.contratservice.domain.entity.Payment;
import emsi.ma.contratservice.repository.PaymentRepository;
import emsi.ma.contratservice.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public Payment create(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> getById(Long id) {
        return paymentRepository.findById(id);
    }

    @Override
    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    @Override
    public List<Payment> getByContractId(Long contractId) {
        return paymentRepository.findAll().stream()
                .filter(p -> p.getContractId().equals(contractId))
                .toList();
    }

    @Override
    public Payment update(Long id, Payment payment) {
        payment.setId(id);
        return paymentRepository.save(payment);
    }

    @Override
    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }
}




