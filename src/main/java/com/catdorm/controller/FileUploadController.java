package com.catdorm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Controller
@RequestMapping("/file")
public class FileUploadController {

    // 图片保存路径 - 用户上传的猫咪照片
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/cats/";

    /**
     * 上传猫咪图片
     */
    @PostMapping("/upload-image")
    @ResponseBody
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // 验证文件是否为空
            if (file.isEmpty()) {
                response.put("status", "error");
                response.put("message", "请选择要上传的图片");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("status", "error");
                response.put("message", "只能上传图片文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 验证文件大小(限制5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("status", "error");
                response.put("message", "图片大小不能超过5MB");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 生成唯一文件名(使用UUID)
            String newFilename = UUID.randomUUID().toString() + extension;
            
            // 确保上传目录存在
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 保存文件
            Path filePath = Paths.get(UPLOAD_DIR + newFilename);
            Files.write(filePath, file.getBytes());
            
            // 返回图片访问URL
            String imageUrl = "/uploads/cats/" + newFilename;
            response.put("status", "success");
            response.put("url", imageUrl);
            response.put("message", "图片上传成功");
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "图片上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
