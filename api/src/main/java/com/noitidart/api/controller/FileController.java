package com.noitidart.api.controller;

import cn.hutool.core.io.FileUtil;
import com.noitidart.api.common.Result;
import com.noitidart.api.exception.CustomException;
import com.noitidart.api.utils.AssertUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件相关接口
 */
@RestController // 标识是 controller 接口
@RequestMapping("/files") // 设置接口的请求路径前缀，取名files避免与java中的file类冲突
@CrossOrigin
public class FileController {

    // System.getProperty("user.dir") 获取到你当前这个项目的根路径
    // 文件上传目录的路径
    private static final String basePath = System.getProperty("user.dir") + "/files/";

    /**
     * 头像上传
     *
     * @param file 文件流的形式接收前端发送过来的头像
     * @return Result 响应结果
     */
    @PostMapping("/upload/avatars")
    public Result uploadAvatar(MultipartFile file) {
        return upload(file, "avatars");
    }

    /**
     * 头像下载
     *
     * @param fileName 要下载的头像文件名
     * @param response HttpServletResponse 对象，用于向前端写入文件流
     */
    @GetMapping("/download/avatars/{fileName}")
    public void downloadAvatar(@PathVariable String fileName, HttpServletResponse response) {
        download(fileName, "avatars", response);
    }

    /**
     * 图标上传
     *
     * @param file 文件流的形式接收前端发送过来的图标
     * @return Result 响应结果
     */
    @PostMapping("/upload/icons")
    public Result uploadIcon(MultipartFile file) {
        return upload(file, "icons");
    }

    /**
     * 图标下载
     *
     * @param fileName 要下载的图标文件名
     * @param response HttpServletResponse 对象，用于向前端写入文件流
     */
    @GetMapping("/download/icons/{fileName}")
    public void downloadIcon(@PathVariable String fileName, HttpServletResponse response) {
        download(fileName, "icons", response);
    }

    /**
     * 通用文件上传
     *
     * @param file 文件流的形式接收前端发送过来的文件
     * @param subDir 子目录名称，用于分类存储（如 "avatars" 或 "icons"）
     * @return Result 响应结果
     */
    private Result upload(MultipartFile file, String subDir) {
        String originalFilename = file.getOriginalFilename();   // 拿到文件的原始名称,例如xxx.png
        AssertUtils.notNull(originalFilename, "文件名不能为空");
        String filePath = basePath + subDir + "/";
        if (!FileUtil.isDirectory(filePath)) {  // 如果目录不存在，需要先创建目录
            FileUtil.mkdir(filePath);   // 创建一个files目录
        }
        // 提供文件存储的完整的路径
        // 给文件名加一个唯一标识
        String fileName = System.currentTimeMillis() + "_" + originalFilename; // 时间戳+文件名，1597232322_xxx.png
        String realPath = filePath + fileName;  // 完整的文件路径
        try {
            FileUtil.writeBytes(file.getBytes(), realPath);  // 第一个参数获取文件的所有字节数组，第二个参数完整的文件路径，通过该方法将文件写入磁盘里去
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("500", "文件上传失败");
        }

        // 返回一个网络链接，类似http:localhost:9090/files/download/xxxx.jpg
        String url = "http://localhost:9090/files/download/" + subDir + "/" + fileName;
        return Result.success(url);
    }

    /**
     * 通用文件下载
     *
     * @param fileName 要下载的文件名
     * @param subDir 子目录名称，表示文件所属类别（如 "avatars" 或 "icons"）
     * @param response HttpServletResponse 对象，用于将文件以二进制流形式输出给前端
     */
    private void download(@PathVariable String fileName, String subDir, HttpServletResponse response) { // response帮我们把文件通过流的形式写出到客户端，这是 Servlet API 提供的对象，用于向客户端（浏览器）发送 HTTP 响应，包括响应头、状态码和响应体（如文件内容）
        try {
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, String.valueOf(StandardCharsets.UTF_8))); // 把文件名编码设置为统一的UTF-8
            response.setContentType("application/octet-stream");    // 添加文件响应头，让浏览器以附件的形式下载
            ServletOutputStream os = response.getOutputStream();    // 获取一个Servlet输出流，这个输出流用于将二进制数据（如文件内容）写入 HTTP 响应体，从而发送给客户端，实现文件下载功能。
            String realPath = basePath + subDir + "/" + fileName;  // 完整的文件路径
            // 安全校验，防止路径穿越（如 fileName="../../etc/passwd"
            if (!realPath.startsWith(basePath)) {
                throw new CustomException("500", "非法文件路径");
            }
            // 获取到文件的字节数组
            byte[] bytes = FileUtil.readBytes(realPath);
            os.write(bytes);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("500", "文件下载失败");
        }
    }
}