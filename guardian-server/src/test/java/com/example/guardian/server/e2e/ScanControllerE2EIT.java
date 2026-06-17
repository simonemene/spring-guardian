package com.example.guardian.server.e2e;

import com.example.guardian.core.model.ArchitectureReviewReport;
import com.example.guardian.core.model.FindingGroup;
import com.example.guardian.server.GuardianServerApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(classes = GuardianServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ScanControllerE2EIT {

    @LocalServerPort
    int port;

    @TempDir
    Path tempDir;

    @Test
    void uploadZipScansProjectAndReturnsEnglishReport() throws Exception {
        Path zip = tempDir.resolve("project.zip");
        createZipProject(zip);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(zip));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<ArchitectureReviewReport> response = new TestRestTemplate().exchange(
                "http://localhost:" + port + "/api/v1/scans/upload?language=en",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                ArchitectureReviewReport.class
        );

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Set<String> titles = response.getBody().findings().stream()
                .map(FindingGroup::title)
                .collect(Collectors.toSet());

        assertTrue(titles.contains("Field injection"));
    }

    private void createZipProject(Path zipPath) throws Exception {
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            add(zip, "demo/src/main/java/com/acme/UserService.java", """
                    package com.acme;

                    import org.springframework.beans.factory.annotation.Autowired;
                    import org.springframework.stereotype.Service;

                    @Service
                    public class UserService {
                        @Autowired
                        private Object dependency;
                    }
                    """);
            add(zip, "demo/src/test/java/com/acme/UserServiceTest.java", """
                    package com.acme;

                    import org.junit.jupiter.api.Test;

                    class UserServiceTest {
                        @Test
                        void ok() {
                            org.junit.jupiter.api.Assertions.assertTrue(true);
                        }
                    }
                    """);
        }
    }

    private void add(ZipOutputStream zip, String name, String content) throws Exception {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes());
        zip.closeEntry();
    }
}
