package emsi.ma.utilisateurservice.domain.dto;

import lombok.Value;

import java.io.Serializable;

@Value
public class ProfileDto implements Serializable {
    Long id;
    Long userId;
    String bio;
    Integer age;
    String avatarUrl;
}




