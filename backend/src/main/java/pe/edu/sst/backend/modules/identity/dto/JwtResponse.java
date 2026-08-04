package pe.edu.sst.backend.modules.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JwtResponse {

    private String accessToken;

    private String tokenType;

    private Long expiresIn;

}