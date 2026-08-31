package pe.edu.sst.backend.shared.storage.s3;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.sst.backend.shared.storage.FileStorageService;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.UUID;

@Service
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "storage.type", havingValue = "s3")
public class S3FileStorageService implements FileStorageService {

    private S3Client s3;

    @Value("${s3.endpoint:}")
    private String endpoint;

    @Value("${s3.region:us-east-1}")
    private String region;

    @Value("${s3.bucket:sst-bucket}")
    private String bucket;

    @Value("${s3.access-key:}")
    private String accessKey;

    @Value("${s3.secret-key:}")
    private String secretKey;

    @Value("${s3.path-style-access:true}")
    private boolean pathStyleAccess;

    @PostConstruct
    public void init() {
        ensureClient();
        ensureBucket();
    }

    private synchronized void ensureClient() {
        if (s3 != null) return;

        S3ClientBuilder builder = S3Client.builder()
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .region(Region.of(region))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(pathStyleAccess).build());

        if (StringUtils.hasText(accessKey) && StringUtils.hasText(secretKey)) {
            AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);
            builder.credentialsProvider(StaticCredentialsProvider.create(creds));
        }

        if (StringUtils.hasText(endpoint)) {
            builder.endpointOverride(URI.create(endpoint));
        }

        s3 = builder.build();
    }

    private void ensureBucket() {
        try {
            s3.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (S3Exception ex) {
            if (ex.statusCode() == 404 || ex.statusCode() == 403) {
                s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            } else {
                throw ex;
            }
        }
    }

    @Override
    public String store(MultipartFile file, String folderName) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo es obligatorio.");
        }

        ensureClient();
        ensureBucket();

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }

            String uniqueName = UUID.randomUUID() + extension;
            String key = Paths.get(folderName, uniqueName).toString().replace('\\', '/');

            PutObjectRequest por = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3.putObject(por, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return key;
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo guardar el archivo en S3.", ex);
        }
    }

    @Override
    public void delete(String filePath) {
        if (filePath == null || filePath.isBlank()) return;

        ensureClient();

        DeleteObjectRequest dor = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(filePath)
                .build();

        try {
            s3.deleteObject(dor);
        } catch (Exception ignored) {
            // No crítico
        }
    }

    @Override
    public String generateSignedUrl(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return "";
        }

        ensureClient();

        try (S3Presigner presigner = createPresigner()) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(filePath)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .getObjectRequest(getObjectRequest)
                    .build();

            return presigner.presignGetObject(presignRequest).url().toString();
        }
    }

    private S3Presigner createPresigner() {
        S3Presigner.Builder builder = S3Presigner.builder()
                .region(Region.of(region))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(pathStyleAccess).build());

        if (StringUtils.hasText(accessKey) && StringUtils.hasText(secretKey)) {
            AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);
            builder.credentialsProvider(StaticCredentialsProvider.create(creds));
        }

        if (StringUtils.hasText(endpoint)) {
            builder.endpointOverride(URI.create(endpoint));
        }

        return builder.build();
    }
}
