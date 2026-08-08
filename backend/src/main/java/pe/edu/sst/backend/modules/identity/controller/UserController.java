package pe.edu.sst.backend.modules.identity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pe.edu.sst.backend.config.constants.ApiPaths;
import pe.edu.sst.backend.modules.identity.dto.UpdateProfileRequest;
import pe.edu.sst.backend.modules.identity.dto.UserProfileResponse;
import pe.edu.sst.backend.modules.identity.service.UserService;
import pe.edu.sst.backend.shared.response.ApiResponse;

@RestController
@RequestMapping(ApiPaths.USERS)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {
        String email = getCurrentUserEmail();
        UserProfileResponse profile = userService.getProfile(email);
        return ResponseEntity.ok(
                ApiResponse.<UserProfileResponse>builder()
                        .success(true)
                        .message("Perfil de usuario obtenido correctamente")
                        .data(profile)
                        .build()
        );
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        String email = getCurrentUserEmail();
        UserProfileResponse updatedProfile = userService.updateProfile(email, request);
        return ResponseEntity.ok(
                ApiResponse.<UserProfileResponse>builder()
                        .success(true)
                        .message("Perfil actualizado correctamente")
                        .data(updatedProfile)
                        .build()
        );
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
