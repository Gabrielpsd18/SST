package pe.edu.sst.backend.modules.identity.service;

import pe.edu.sst.backend.modules.identity.dto.UpdateProfileRequest;
import pe.edu.sst.backend.modules.identity.dto.UserProfileResponse;

public interface UserService {
    UserProfileResponse getProfile(String userEmail);
    UserProfileResponse updateProfile(String userEmail, UpdateProfileRequest request);
}
