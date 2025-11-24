package emsi.ma.utilisateurservice.repository;

import emsi.ma.utilisateurservice.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

