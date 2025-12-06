package emsi.ma.contratservice.service;

import emsi.ma.contratservice.domain.entity.Contract;
import java.util.List;
import java.util.Optional;

public interface IContractService {
    Contract create(Contract contract);
    Optional<Contract> getById(Long id);
    List<Contract> getAll();
    List<Contract> getByPropertyId(Long propertyId);
    Contract update(Long id, Contract contract);
    void delete(Long id);
}






