package pe.edu.sst.backend.modules.identity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.edu.sst.backend.config.constants.ApiPaths;
import pe.edu.sst.backend.modules.identity.dto.JwtResponse;
import pe.edu.sst.backend.modules.identity.dto.LoginRequest;
import pe.edu.sst.backend.modules.identity.service.AuthService;
import pe.edu.sst.backend.shared.response.ApiResponse;

@RestController
@RequestMapping(ApiPaths.AUTH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<JwtResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        JwtResponse response =
                authService.login(request);

        return ApiResponse.<JwtResponse>builder()
                .success(true)
                .message("Inicio de sesión exitoso")
                .data(response)
                .build();

    }

}