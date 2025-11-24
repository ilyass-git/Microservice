package emsi.ma.contratservice.client.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO pour représenter un utilisateur depuis le service Utilisateur
 * 
 * Cette classe doit correspondre exactement au UserResponseDto du Utilisateur Service
 */
@Value
public class UserResponseDto implements Serializable {
    Long id;
    String email;
    String nom;
    String prenom;
    String telephone;
}

