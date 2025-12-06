package emsi.ma.contratservice.service;

import emsi.ma.contratservice.domain.entity.Tenant;
import java.util.List;
import java.util.Optional;

public interface ITenantService {
    Tenant create(Tenant tenant);
    Optional<Tenant> getById(Long id);
    List<Tenant> getAll();
    List<Tenant> getByContractId(Long contractId);
    List<Tenant> getByUserId(Long userId);
    Tenant update(Long id, Tenant tenant);
    void delete(Long id);
}






