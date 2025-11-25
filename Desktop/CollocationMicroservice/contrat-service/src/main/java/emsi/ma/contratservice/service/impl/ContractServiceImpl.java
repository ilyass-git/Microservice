package emsi.ma.contratservice.service.impl;

import emsi.ma.contratservice.domain.entity.Contract;
import emsi.ma.contratservice.repository.ContractRepository;
import emsi.ma.contratservice.service.IContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements IContractService {

    private final ContractRepository contractRepository;

    @Override
    public Contract create(Contract contract) {
        return contractRepository.save(contract);
    }

    @Override
    public Optional<Contract> getById(Long id) {
        return contractRepository.findById(id);
    }

    @Override
    public List<Contract> getAll() {
        return contractRepository.findAll();
    }

    @Override
    public List<Contract> getByPropertyId(Long propertyId) {
        return contractRepository.findAll().stream()
                .filter(c -> c.getPropertyId().equals(propertyId))
                .toList();
    }

    @Override
    public Contract update(Long id, Contract contract) {
        contract.setId(id);
        return contractRepository.save(contract);
    }

    @Override
    public void delete(Long id) {
        contractRepository.deleteById(id);
    }
}




