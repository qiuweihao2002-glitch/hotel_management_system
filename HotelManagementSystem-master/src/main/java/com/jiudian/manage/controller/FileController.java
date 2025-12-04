package com.jiudian.manage.controller;

import com.jiudian.manage.mapper.UserMapper;
import com.jiudian.manage.model.User;
import com.jiudian.manage.service.UserService;
import com.jiudian.manage.until.FileUtil;
import com.jiudian.manage.until.State;
import com.jiudian.manage.until.StateSignal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
@RestController
@RequestMapping(value = "/upFile")
public class FileController {
    @Autowired
    UserService userService;
    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/upFilePhoto.do")
    public Map upFilePhoto(@RequestParam MultipartFile file, @RequestParam int userid) {
        // 1. 处理文件名（不变）
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix;

        // 2. 关键修复：动态获取 static/File 目录的绝对路径
//        String filePath = "";
//        try {
//            // 获取项目中 static 目录的绝对路径（编译后在 classes/static 下）
//            String staticPath = ResourceUtils.getURL("classpath:static").getPath();
//            // 拼接 File 目录（确保路径正确）
//            filePath = staticPath + File.separator + "File" + File.separator;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        String filePath = "/app/upload/File/";//文件在docker中的的真实路径：app/upload/File/xxxx.jpg


        String RealfilePath = "File/" + fileName; // 数据库路径不变（前端访问用），数据库中存的是相对地址：File/xxxx.jpg

//        以前在本机运行时，你把图片保存到了 target/classes/static/File/，Spring Boot 会自动把 classpath:/static/ 映射成静态资源，所以 /File/xxxx.jpg 能访问到。
//        现在你把文件写到 /app/upload/File/（容器里的普通目录），但是 没有配置静态资源映射，所以浏览器访问 /File/xxxx.jpg 时，Spring 找不到这个文件，自然就显示不了头像。
        // 3. 保存文件（确保目录存在）
        boolean b = false;
        try {
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs(); // 自动创建多级目录（包括不存在的父目录）
            }
            File targetFile = new File(filePath + fileName);
            file.transferTo(targetFile); // 保存文件
            b = true;

            // 打印实际保存路径，方便验证
            System.out.println("文件保存成功，路径：" + targetFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4. 更新数据库（不变）
        User user = new User();
        user.setUserid(userid);
        user.setPhotourl(RealfilePath);//把相对地址存进数据库里了
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        boolean photo = updateCount > 0;

        // 5. 返回结果（不变）
        StateSignal signal = new StateSignal();
        if (b && photo) {
            signal.put(State.SuccessCode);
            signal.put(State.SuccessMessage);
        } else {
            signal.put(State.ErrorCode);
            signal.put(State.ErrorMessage);
        }
        return signal.getResult();
    }
}