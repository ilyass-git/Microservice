package emsi.ma.contratservice.repository;

import emsi.ma.contratservice.domain.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
}




