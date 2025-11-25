package emsi.ma.annonceservice.client.dto;

import lombok.Value;

import java.io.Serializable;

@Value
public class UserResponseDto implements Serializable {
    Long id;
    String email;
    String nom;
    String prenom;
    String telephone;
}




