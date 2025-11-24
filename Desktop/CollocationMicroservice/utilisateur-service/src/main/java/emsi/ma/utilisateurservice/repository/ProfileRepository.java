package emsi.ma.utilisateurservice.repository;

import emsi.ma.utilisateurservice.domain.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}

