package cashflow.controller;

import cashflow.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;

/**
 * @author Cail Gainey
 * @since 2025/1/25 14:59
 **/
@Slf4j
@RestController
@RequestMapping("/files")
public class FileController {

    @jakarta.annotation.Resource
    private FileService fileService;

    @GetMapping("/{folder}/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String folder, @PathVariable String filename) {
        try {
            Resource resource = fileService.loadFileAsResource(folder, filename);
            String contentType = Files.probeContentType(resource.getFile().toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            log.error("文件获取失败: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
