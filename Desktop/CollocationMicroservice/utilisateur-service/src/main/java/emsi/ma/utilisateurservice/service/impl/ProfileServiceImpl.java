package emsi.ma.utilisateurservice.service.impl;

import emsi.ma.utilisateurservice.domain.entity.Profile;
import emsi.ma.utilisateurservice.repository.ProfileRepository;
import emsi.ma.utilisateurservice.service.IProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements IProfileService {

    private final ProfileRepository profileRepository;

    @Override
    public Profile create(Profile profile) {
        return profileRepository.save(profile);
    }

    @Override
    public Optional<Profile> getById(Long id) {
        return profileRepository.findById(id);
    }

    @Override
    public Optional<Profile> getByUserId(Long userId) {
        return profileRepository.findAll().stream()
                .filter(p -> p.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<Profile> getAll() {
        return profileRepository.findAll();
    }

    @Override
    public Profile update(Long id, Profile profile) {
        profile.setId(id);
        return profileRepository.save(profile);
    }

    @Override
    public void delete(Long id) {
        profileRepository.deleteById(id);
    }
}






