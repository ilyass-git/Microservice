package emsi.ma.utilisateurservice.repository;

import emsi.ma.utilisateurservice.domain.entity.Preference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {
}




