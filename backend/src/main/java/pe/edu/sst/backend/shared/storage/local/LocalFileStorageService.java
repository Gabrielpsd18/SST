package pe.edu.sst.backend.shared.storage.local;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.sst.backend.shared.storage.FileStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageService implements FileStorageService {

    private static final String STORAGE_ROOT = System.getProperty("user.dir") + "/uploads";

    @Override
    public String store(MultipartFile file, String folderName) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo es obligatorio.");
        }

        try {
            Path root = Paths.get(STORAGE_ROOT, folderName);
            Files.createDirectories(root);

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }

            String uniqueName = UUID.randomUUID() + extension;
            Path destination = root.resolve(uniqueName);
            Files.copy(file.getInputStream(), destination);

            return Paths.get(folderName, uniqueName).toString().replace('\\', '/');
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo guardar el archivo.", ex);
        }
    }

    @Override
    public void delete(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return;
        }

        try {
            String normalized = filePath.replace('/', java.io.File.separatorChar);
            Path file = Paths.get(STORAGE_ROOT, normalized);
            if (Files.exists(file)) {
                Files.deleteIfExists(file);
            }
        } catch (IOException ignored) {
            // Archivo no crítico para la operación; se conserva el registro si falla la eliminación física.
        }
    }

    @Override
    public String generateSignedUrl(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return "";
        }
        String normalized = filePath.replace('\\', '/');
        return "/uploads/" + normalized;
    }
}
