package emsi.ma.annonceservice.service;

import emsi.ma.annonceservice.domain.entity.Property;

import java.util.List;
import java.util.Optional;

public interface IPropertyService {
    Property create(Property property);
    Optional<Property> getById(Long id);
    List<Property> getAll();
    List<Property> getByCity(String city);
    List<Property> getByOwnerId(Long ownerId);
    Property update(Long id, Property property);
    void delete(Long id);
}






