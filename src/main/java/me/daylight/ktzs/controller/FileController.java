package me.daylight.ktzs.controller;

import me.daylight.ktzs.annotation.ApiDoc;
import me.daylight.ktzs.authority.SessionUtil;
import me.daylight.ktzs.model.dto.BaseResponse;
import me.daylight.ktzs.model.entity.UploadFile;
import me.daylight.ktzs.model.entity.User;
import me.daylight.ktzs.service.FileService;
import me.daylight.ktzs.utils.FileUtil;
import me.daylight.ktzs.utils.RetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Daylight
 * @date 2019/03/09 07:13
 */
@Controller
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService fileService;

    @ApiDoc(description = "文件上传")
    @PostMapping("/upload")
    @ResponseBody
    public BaseResponse upload(Long homeworkId,@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty())
            return RetResponse.error();

        User loginUser= SessionUtil.getInstance().getUser();

        String uuidName= UUID.randomUUID().toString().replace("-","").toLowerCase()+FileUtil.getSuffix(Objects.requireNonNull(file.getOriginalFilename()));
        String fileName=file.getOriginalFilename();
        StringBuilder path= new StringBuilder().append(homeworkId).append("/");

        UploadFile myFile=new UploadFile(loginUser,uuidName,fileName,path.toString());
        FileUtil.upload(file,path.toString(),uuidName);

        myFile=fileService.save(myFile);
        return RetResponse.success(myFile.getId());
    }

    @ApiDoc(description = "MD5校验")
    @GetMapping("/md5")
    @ResponseBody
    public BaseResponse md5Check(String md5){
        return RetResponse.success(md5);
    }

    @ApiDoc(description = "删除文件")
    @GetMapping("/delete")
    @ResponseBody
    public BaseResponse deleteFile(long id){
        if (fileService.isFileExist(id)) {
            UploadFile file = fileService.getFile(id);
            if (FileUtil.delete(file.getPath()+ file.getUUIDName())) {
                fileService.deleteFile(id);
                return RetResponse.success();
            }
        }
        return RetResponse.error("文件不存在！");
    }

    @RequestMapping("/{id:.+}")
    public ResponseEntity<FileSystemResource> download(@PathVariable long id) {
        if (fileService.isFileExist(id)) {
            UploadFile myFile = fileService.getFile(id);

            String uuidName = myFile.getUUIDName();
            String filename = myFile.getFileName();
            String path=myFile.getPath();
            FileUtil.createDirIfNotExists(path);
            java.io.File file = new java.io.File(FileUtil.absolutePath, FileUtil.staticDir+path+ uuidName);
            if (file.exists()) {
                return ResponseEntity
                        .ok()
                        .header("Content-Disposition", "attachment;fileName=" + new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1))
                        .contentLength(file.length())
                        .contentType(MediaType.parseMediaType("application/octet-stream"))
                        .body(new FileSystemResource(file));
            }
        }
        return ResponseEntity.notFound().build();
    }
}
