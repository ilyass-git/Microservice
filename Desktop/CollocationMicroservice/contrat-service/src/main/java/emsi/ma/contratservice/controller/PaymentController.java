package emsi.ma.contratservice.controller;

import emsi.ma.contratservice.domain.dto.PaymentDto;
import emsi.ma.contratservice.domain.entity.Payment;
import emsi.ma.contratservice.mapper.PaymentMapper;
import emsi.ma.contratservice.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @PostMapping
    public ResponseEntity<PaymentDto> create(@RequestBody PaymentDto paymentDto) {
        Payment payment = paymentMapper.toEntity(paymentDto);
        Payment created = paymentService.create(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMapper.toDto(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getById(@PathVariable Long id) {
        return paymentService.getById(id)
                .map(payment -> ResponseEntity.ok(paymentMapper.toDto(payment)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PaymentDto>> getAll() {
        List<PaymentDto> payments = paymentService.getAll().stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/contract/{contractId}")
    public ResponseEntity<List<PaymentDto>> getByContractId(@PathVariable Long contractId) {
        List<PaymentDto> payments = paymentService.getByContractId(contractId).stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(payments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDto> update(@PathVariable Long id, @RequestBody PaymentDto paymentDto) {
        Payment payment = paymentMapper.toEntity(paymentDto);
        Payment updated = paymentService.update(id, payment);
        return ResponseEntity.ok(paymentMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}




