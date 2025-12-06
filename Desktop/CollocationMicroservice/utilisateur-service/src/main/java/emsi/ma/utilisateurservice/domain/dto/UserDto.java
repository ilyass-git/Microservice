package emsi.ma.utilisateurservice.domain.dto;

import lombok.Value;

import java.io.Serializable;

@Value
public class UserDto implements Serializable {
    Long id;
    String email;
    String password;
    String nom;
    String prenom;
    String telephone;
}






