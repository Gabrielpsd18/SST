package pe.edu.sst.backend.shared.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String store(MultipartFile file, String folderName);
    void delete(String filePath);

    default String generateSignedUrl(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return "";
        }
        return filePath;
    }
}
