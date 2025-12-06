package emsi.ma.utilisateurservice.service;

import emsi.ma.utilisateurservice.domain.entity.Profile;
import java.util.List;
import java.util.Optional;

public interface IProfileService {
    Profile create(Profile profile);
    Optional<Profile> getById(Long id);
    Optional<Profile> getByUserId(Long userId);
    List<Profile> getAll();
    Profile update(Long id, Profile profile);
    void delete(Long id);
}






