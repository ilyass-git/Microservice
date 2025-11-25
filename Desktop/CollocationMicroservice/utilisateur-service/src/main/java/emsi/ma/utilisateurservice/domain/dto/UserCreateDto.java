package emsi.ma.utilisateurservice.domain.dto;

import lombok.Value;

import java.io.Serializable;

@Value
public class UserCreateDto implements Serializable {
    String email;
    String password;
    String nom;
    String prenom;
    String telephone;
}




