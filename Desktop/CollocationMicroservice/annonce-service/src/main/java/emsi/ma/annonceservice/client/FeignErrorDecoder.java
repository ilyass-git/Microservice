package emsi.ma.annonceservice.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * Décodage personnalisé des erreurs Feign
 */
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        
        if (status == HttpStatus.NOT_FOUND) {
            log.error("❌ [FEIGN ERROR] Ressource non trouvée (404) pour la méthode: {}", methodKey);
            return new RuntimeException("Ressource non trouvée: " + methodKey);
        }
        
        if (status == HttpStatus.SERVICE_UNAVAILABLE || status == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error("❌ [FEIGN ERROR] Service indisponible ({}), méthode: {}", status.value(), methodKey);
            return new RuntimeException("Service indisponible: " + methodKey);
        }
        
        return defaultErrorDecoder.decode(methodKey, response);
    }
}

