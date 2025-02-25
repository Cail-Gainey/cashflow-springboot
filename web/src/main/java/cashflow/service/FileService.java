package cashflow.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 *
 * @author Cail Gainey
 * @since 2025-1-26 18:10
 **/
public interface FileService {
    /**
     * 保存文件
     *
     * @param file   文件
     * @param folder 文件夹名称
     * @return 文件相对路径
     */
    String saveFile(MultipartFile file, String folder);

    /**
     * 删除文件
     *
     * @param path 文件相对路径
     */
    void removeFile(String path);

    /**
     * 获取文件资源
     *
     * @param folder   文件夹名称
     * @param filename 文件名
     * @return 文件资源
     */
    Resource loadFileAsResource(String folder, String filename);

    /**
     * 获取完整的文件URL
     *
     * @param relativePath 相对路径
     * @return 完整URL
     */
    String getFullFileUrl(String relativePath);

    /**
     * 处理文件路径（确保是相对路径）
     *
     * @param path 文件路径
     * @return 处理后的相对路径
     */
    String processFilePath(String path);
}
