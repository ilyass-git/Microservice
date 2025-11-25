package emsi.ma.contratservice.repository;

import emsi.ma.contratservice.domain.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {
}




