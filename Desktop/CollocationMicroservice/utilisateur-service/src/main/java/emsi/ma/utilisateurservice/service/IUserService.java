package emsi.ma.utilisateurservice.service;

import emsi.ma.utilisateurservice.domain.entity.User;
import java.util.List;
import java.util.Optional;

public interface IUserService {
    User create(User user);
    Optional<User> getById(Long id);
    List<User> getAll();
    Optional<User> getByEmail(String email);
    User update(Long id, User user);
    void delete(Long id);
}




