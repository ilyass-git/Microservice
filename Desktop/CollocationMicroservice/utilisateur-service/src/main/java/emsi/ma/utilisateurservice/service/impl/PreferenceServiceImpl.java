package emsi.ma.utilisateurservice.service.impl;

import emsi.ma.utilisateurservice.domain.entity.Preference;
import emsi.ma.utilisateurservice.repository.PreferenceRepository;
import emsi.ma.utilisateurservice.service.IPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PreferenceServiceImpl implements IPreferenceService {

    private final PreferenceRepository preferenceRepository;

    @Override
    public Preference create(Preference preference) {
        return preferenceRepository.save(preference);
    }

    @Override
    public Optional<Preference> getById(Long id) {
        return preferenceRepository.findById(id);
    }

    @Override
    public Optional<Preference> getByUserId(Long userId) {
        return preferenceRepository.findAll().stream()
                .filter(p -> p.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<Preference> getAll() {
        return preferenceRepository.findAll();
    }

    @Override
    public Preference update(Long id, Preference preference) {
        preference.setId(id);
        return preferenceRepository.save(preference);
    }

    @Override
    public void delete(Long id) {
        preferenceRepository.deleteById(id);
    }
}




