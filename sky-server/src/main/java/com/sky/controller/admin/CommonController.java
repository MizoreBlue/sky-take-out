package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {
    private final AliOssUtil aliOssUtil;

    public CommonController(AliOssUtil aliOssUtil) {
        this.aliOssUtil = aliOssUtil;
    }

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传: {}", file);
        try {

//        原始文件名
            String originalFilename = file.getOriginalFilename();
//        截取原始文件名后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        通过UUID构建一个新的唯一文件名称
            String ObjectName = UUID.randomUUID().toString() + extension;

            String filePath = aliOssUtil.upload(file.getBytes(), ObjectName);
//            返回oss对应的http路径给前端用于请求数据
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e);
        }
//        返回文件上传失败信息
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
