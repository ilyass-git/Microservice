package emsi.ma.contratservice.controller;

import emsi.ma.contratservice.domain.dto.ContractDto;
import emsi.ma.contratservice.domain.entity.Contract;
import emsi.ma.contratservice.mapper.ContractMapper;
import emsi.ma.contratservice.service.IContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final IContractService contractService;
    private final ContractMapper contractMapper;

    @PostMapping
    public ResponseEntity<ContractDto> create(@RequestBody ContractDto contractDto) {
        Contract contract = contractMapper.toEntity(contractDto);
        Contract created = contractService.create(contract);
        return ResponseEntity.status(HttpStatus.CREATED).body(contractMapper.toDto(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractDto> getById(@PathVariable Long id) {
        return contractService.getById(id)
                .map(contract -> ResponseEntity.ok(contractMapper.toDto(contract)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ContractDto>> getAll() {
        List<ContractDto> contracts = contractService.getAll().stream()
                .map(contractMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(contracts);
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<ContractDto>> getByPropertyId(@PathVariable Long propertyId) {
        List<ContractDto> contracts = contractService.getByPropertyId(propertyId).stream()
                .map(contractMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(contracts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContractDto> update(@PathVariable Long id, @RequestBody ContractDto contractDto) {
        Contract contract = contractMapper.toEntity(contractDto);
        Contract updated = contractService.update(id, contract);
        return ResponseEntity.ok(contractMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contractService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

