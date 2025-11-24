package emsi.ma.utilisateurservice.service;

import emsi.ma.utilisateurservice.domain.entity.Preference;
import java.util.List;
import java.util.Optional;

public interface IPreferenceService {
    Preference create(Preference preference);
    Optional<Preference> getById(Long id);
    Optional<Preference> getByUserId(Long userId);
    List<Preference> getAll();
    Preference update(Long id, Preference preference);
    void delete(Long id);
}

