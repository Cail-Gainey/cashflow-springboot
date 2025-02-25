package cashflow.service.impl;

import cashflow.enums.MessageEnums;
import cashflow.exception.FilesException;
import cashflow.service.FileService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * 文件服务实现类
 *
 * @author Cail Gainey
 * @since 2025-1-26 18:12
 **/
@Slf4j
@Service
public class FileServiceImpl implements FileService {
    private static final int MAX_FILENAME_LENGTH = 255;
    private final Path rootPath = Paths.get(System.getProperty("user.dir"));
    private final Path webPath = rootPath.resolve("Web");
    private final Path filesPath = webPath.resolve("files");
    @Value("${server.address:localhost}")
    private String serverAddress;
    @Value("${server.port:8080}")
    private String serverPort;

    @PostConstruct
    public void init() {
        try {
            // 创建必要的目录结构
            if (!Files.exists(webPath)) {
                Files.createDirectories(webPath);
            }
            if (!Files.exists(filesPath)) {
                Files.createDirectories(filesPath);
            }
            // 创建头像目录
            Path avatarPath = filesPath.resolve("avatar");
            Path ledgerImgPath = filesPath.resolve("ledger");
            if (!Files.exists(avatarPath) || !Files.exists(ledgerImgPath)) {
                Files.createDirectories(avatarPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create file storage directories", e);
        }
    }

    @Override
    public String saveFile(MultipartFile file, String folder) {
        try {
            Path folderPath = filesPath.resolve(folder).normalize();
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            String sanitizedFilename = sanitizeFilename(folder);
            Path filePath = folderPath.resolve(sanitizedFilename).normalize();

            // 生成相对路径
            String relativePath = "/files/" + folder + "/" + sanitizedFilename;

            // 如果文件已存在，先删除
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            Files.copy(file.getInputStream(), filePath);
            log.info("文件保存成功: {}", filePath);
            return relativePath;

        } catch (IOException e) {
            log.error("文件保存失败: {}", e.getMessage());
            throw new RuntimeException("文件保存失败", e);
        }
    }

    @Override
    public void removeFile(String path) {
        try {
            // 检查路径是否为空
            if (path == null || path.isEmpty()) {
                throw new FilesException("路径为空", path);
            }

            // 将相对路径转换为文件的绝对路径
            Path filePath = filesPath.resolve(path.replaceFirst("/files/", "")).normalize();

            // 检查文件是否存在
            if (Files.exists(filePath)) {
                // 删除文件
                Files.delete(filePath);
                log.info("文件删除成功: {}", filePath);
            } else {
                log.warn("文件不存在: {}", filePath);
            }
        } catch (IOException e) {
            log.error("文件删除失败: {}", e.getMessage());
            throw new RuntimeException("文件删除失败", e);
        }
    }

    @Override
    public Resource loadFileAsResource(String folder, String filename) {
        try {
            Path filePath = filesPath.resolve(folder).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                log.error("文件未找到：{}", folder + "/" + filename);
                throw new FilesException(MessageEnums.FILES_NOT_EXIST.getMessage(), filename);
            }
        } catch (IOException e) {
            log.error("文件未找到：{}， error：{}", filename, e.getMessage());
            throw new FilesException(MessageEnums.FILES_NOT_EXIST.getMessage(), filename);
        }
    }

    @Override
    public String getFullFileUrl(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }

        if (relativePath.startsWith("http")) {
            return relativePath;
        }

        return String.format("http://%s:%s%s", serverAddress, serverPort, relativePath);
    }

    @Override
    public String processFilePath(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        if (path.startsWith("http")) {
            String[] parts = path.split("/files/");
            if (parts.length > 1) {
                return "/files/" + parts[1];
            }
        }

        return path;
    }


    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "default.png";
        }

        // 生成唯一文件名
        String extension = Optional.of(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf(".")))
                .orElse(".png");

        // 清理文件名并限制长度
        String baseName = filename.replaceAll("[^a-zA-Z0-9._-]", "")
                .replaceAll("\\.[^.]*$", "");

        // 生成唯一文件名并限制长度
        String sanitizedFilename = System.currentTimeMillis() + "_" + baseName;
        if (sanitizedFilename.length() > MAX_FILENAME_LENGTH - extension.length()) {
            sanitizedFilename = sanitizedFilename.substring(0, MAX_FILENAME_LENGTH - extension.length());
        }

        return sanitizedFilename + extension;
    }
}
