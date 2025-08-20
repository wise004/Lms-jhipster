package com.edupress.web.rest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Simple file upload controller that stores files on the local filesystem and returns a public URL.
 */
@RestController
@RequestMapping("/api/uploads")
public class UploadResource {

    private static final Logger LOG = LoggerFactory.getLogger(UploadResource.class);

    @Value("${application.uploads.dir:uploads}")
    private String uploadsDir;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> upload(@RequestPart("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No file provided"));
        }
        String nameMaybe = file.getOriginalFilename();
        String original = nameMaybe != null ? nameMaybe.replace("\\", "/") : "file";
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
        String day = LocalDate.now().toString();
        String filename = UUID.randomUUID() + ext;
        Path targetDir = Path.of(uploadsDir, day).toAbsolutePath().normalize();
        Files.createDirectories(targetDir);
        Path target = targetDir.resolve(filename);
        file.transferTo(target);
        // Public URL will be served at /uploads/{day}/{filename}
        String publicUrl = "/uploads/" + day + "/" + filename;
        LOG.info("Uploaded file saved to {} exposed as {}", target, publicUrl);
        return ResponseEntity.created(URI.create(publicUrl)).body(Map.of("url", publicUrl, "filename", original));
    }
}
