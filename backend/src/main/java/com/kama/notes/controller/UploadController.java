package com.kama.notes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kama.notes.model.base.ApiResponse;
import com.kama.notes.model.vo.upload.ImageVO;
import com.kama.notes.service.UploadService;

/**
 * UploadController
 *
 * 文件上传控制器，负责接收前端上传的文件并委托 UploadService 处理存储与返回元信息。
 *
 * 说明：
 * - 当前仅提供图片上传接口 /api/upload/image；
 * - 返回统一使用 ApiResponse<ImageVO>，ImageVO 包含图片访问地址等信息；
 * - 强烈建议在 UploadService 中实现文件类型与大小校验、文件名去重或哈希存储、
 *   并将存储路径/外部访问域名通过配置管理（application.yml/properties）。
 *
 * 安全与性能建议：
 * - 在网关或过滤器层对上传频率与大小做限流与校验，防止滥用；
 * - 生产环境建议将图片存储到对象存储或 CDN，并使用异步上传/回调优化性能。
 */
@RestController
@RequestMapping("/api")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 上传图片
     *
     * 请求：
     * - POST /api/upload/image
     * - 参数：form-data，key 为 "file"，值为图片文件（MultipartFile）
     *
     * 行为：
     * - 将文件交由 UploadService 处理（保存、生成访问 URL 等），并返回封装后的 ImageVO；
     * - 出错时由 Service 层抛出异常或返回错误 ApiResponse，由全局异常处理或调用方处理。
     *
     * 注意：
     * - 请确保前端与服务端在 Content-Type 与编码上保持一致；
     * - 如需支持多文件上传，可在此基础上增加 List<MultipartFile> 的处理方法。
     *
     * @param file 上传的图片文件
     * @return ApiResponse<ImageVO> 图片元信息（例如访问 URL）
     */
    @PostMapping("/upload/image")
    public ApiResponse<ImageVO> uploadImage(@RequestParam("file") MultipartFile file) {
        return uploadService.uploadImage(file);
    }
}
