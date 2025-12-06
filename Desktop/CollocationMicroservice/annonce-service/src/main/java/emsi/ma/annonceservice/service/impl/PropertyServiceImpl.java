package emsi.ma.annonceservice.service.impl;

import emsi.ma.annonceservice.domain.entity.Property;
import emsi.ma.annonceservice.repository.PropertyRepository;
import emsi.ma.annonceservice.service.IPropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements IPropertyService {

    private final PropertyRepository propertyRepository;

    @Override
    public Property create(Property property) {
        return propertyRepository.save(property);
    }

    @Override
    public Optional<Property> getById(Long id) {
        return propertyRepository.findById(id);
    }

    @Override
    public List<Property> getAll() {
        return propertyRepository.findAll();
    }

    @Override
    public List<Property> getByCity(String city) {
        return propertyRepository.findByCity(city);
    }

    @Override
    public List<Property> getByOwnerId(Long ownerId) {
        return propertyRepository.findByOwnerId(ownerId);
    }

    @Override
    public Property update(Long id, Property property) {
        property.setId(id);
        return propertyRepository.save(property);
    }

    @Override
    public void delete(Long id) {
        propertyRepository.deleteById(id);
    }
}






