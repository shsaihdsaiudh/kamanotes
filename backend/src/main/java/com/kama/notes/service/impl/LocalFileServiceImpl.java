package com.kama.notes.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kama.notes.service.FileService;

/**
 * LocalFileServiceImpl
 *
 * 本地文件存储实现（用于开发或简单部署场景）。
 *
 * 说明：
 * - 将上传的文件保存到本地磁盘目录（配置项 upload.path），并返回可访问的 URL 前缀 + 文件名（配置项 upload.url-prefix）。
 * - 对图片上传做了简单的后缀与大小校验（ALLOWED_IMAGE_EXTENSIONS / MAX_IMAGE_SIZE）。
 * - 该实现适用于小规模部署；生产环境建议使用对象存储服务（如 OSS/S3）或经过 Nginx/CDN 转发的稳定存储方案。
 *
 * 安全与可靠性建议：
 * - 校验并规范 uploadBasePath，避免相对路径导致的目录穿越或覆盖系统敏感文件；
 * - 文件名使用 UUID 生成，防止冲突与信息泄露；
 * - 考虑为上传目录设置最小权限并定期清理过期/异常文件；
 * - 若对接公网访问，建议使用 CDN 或代理（urlPrefix 指向 CDN 地址），并对上传文件做病毒/内容扫描。
 */
@Service
public class LocalFileServiceImpl implements FileService {

    /**
     * 基础上传路径（本地存储的绝对或相对路径）
     */
    @Value("${upload.path}")
    private String uploadBasePath;

    /**
     * 返回给前端的地址前缀 (可配合CDN/Nginx等)
     */
    @Value("${upload.url-prefix}")
    private String urlPrefix;

    /**
     * 允许上传的图片后缀名（小写形式）
     */
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS
            = Arrays.asList(".jpg", ".jpeg", ".png", ".webp");

    /**
     * 单个图片最大尺寸 (10MB)
     */
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;

    /**
     * 图片上传入口（带格式与大小校验）。
     *
     * 校验要点：
     * - 文件不能为空；
     * - 大小不得超过 MAX_IMAGE_SIZE；
     * - 后缀名必须在 ALLOWED_IMAGE_EXTENSIONS 列表中（小写比较）。
     *
     * @param file 前端上传的 MultipartFile
     * @return 可供访问的完整 URL（urlPrefix + "/" + fileName）
     * @throws IllegalArgumentException 校验失败时抛出
     * @throws IllegalStateException    保存文件失败时抛出
     */
    @Override
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传的图片文件为空");
        }

        // 校验文件大小
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("图片大小不能超过 10MB");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("图片文件名无效");
        }

        // 校验后缀（小写判断）
        String lowerCaseExtension = originalFilename
                .substring(originalFilename.lastIndexOf("."))
                .toLowerCase();

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(lowerCaseExtension)) {
            throw new IllegalArgumentException(
                    "只支持 " + ALLOWED_IMAGE_EXTENSIONS + " 等格式图片");
        }
        return doUpload(file);
    }

    /**
     * 普通文件上传（不做后缀/大小限制）。
     *
     * @param file 上传文件
     * @return 上传后可访问的 URL
     * @throws IllegalArgumentException 上传文件为空或文件名不合法时抛出
     * @throws IllegalStateException    保存文件失败时抛出
     */
    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件为空");
        }
        return doUpload(file);
    }

    /**
     * 实际执行文件上传的公共方法。
     *
     * 实现要点：
     * - 使用 UUID 生成新的文件名以避免冲突并防止泄露原始文件名信息；
     * - 确保上传目录存在，若不存在则尝试创建；
     * - 使用 MultipartFile.transferTo 保存到磁盘，捕获并包装 IOException；
     * - 返回 urlPrefix + "/" + newFileName 供前端或 CDN 访问。
     *
     * 注意：
     * - uploadBasePath 与 urlPrefix 两个配置应在应用配置中明确，urlPrefix 可为 CDN/域名映射路径；
     * - 若需支持子目录分片（按日期/用户存储），可在此方法中扩展目录结构并定期归档。
     *
     * @param file MultipartFile
     * @return 上传后可访问的URL
     */
    private String doUpload(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("文件名不合法");
        }

        // 统一生成新文件名
        String fileExtension = originalFilename
                .substring(originalFilename.lastIndexOf("."))
                .toLowerCase();
        String newFileName = UUID.randomUUID() + fileExtension;

        // 确保目录存在
        File uploadDir = new File(uploadBasePath);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IllegalStateException("无法创建上传目录: " + uploadBasePath);
        }

        // 保存文件
        File destFile = new File(uploadDir, newFileName);
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            throw new IllegalStateException("文件保存失败: " + e.getMessage(), e);
        }
        // 返回访问URL
        return urlPrefix + "/" + newFileName;
    }
}
