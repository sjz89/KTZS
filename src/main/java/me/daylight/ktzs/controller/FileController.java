package me.daylight.ktzs.controller;

import me.daylight.ktzs.annotation.ApiDoc;
import me.daylight.ktzs.authority.SessionUtil;
import me.daylight.ktzs.authority.Unlimited;
import me.daylight.ktzs.model.dto.BaseResponse;
import me.daylight.ktzs.model.entity.UploadFile;
import me.daylight.ktzs.model.entity.User;
import me.daylight.ktzs.model.enums.RoleList;
import me.daylight.ktzs.service.FileService;
import me.daylight.ktzs.service.HomeworkService;
import me.daylight.ktzs.service.UserService;
import me.daylight.ktzs.utils.FileUtil;
import me.daylight.ktzs.utils.RetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * @author Daylight
 * @date 2019/03/09 07:13
 */
@Controller
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService fileService;

    @Autowired
    private HomeworkService homeworkService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @ApiDoc(description = "文件上传",role = RoleList.Student)
    @PostMapping("/upload")
    @ResponseBody
    public BaseResponse upload(Long userId,Long homeworkId,@RequestParam MultipartFile file) throws IOException {
        if (!homeworkService.isHomeworkExist(homeworkId))
            return RetResponse.error("作业不存在");

        User loginUser;
        if (userId!=null)
            loginUser=userService.findUserById(userId);
        else if (SessionUtil.getInstance().isUserLogin())
            loginUser=SessionUtil.getInstance().getUser();
        else
            return RetResponse.error();

        if (file.isEmpty())
            return RetResponse.error("文件出错");

        Map<String,Object> homework=homeworkService.findById(homeworkId);
        String uuidName= loginUser.getIdNumber()+"-"+homework.get("name")+FileUtil.getSuffix(Objects.requireNonNull(file.getOriginalFilename()));
        String fileName= URLDecoder.decode(file.getOriginalFilename(), "UTF-8");
        StringBuilder path= new StringBuilder().append(homeworkId).append("/");

        UploadFile myFile;
        if (homeworkService.isStudentUploaded(loginUser.getId(),homeworkId)) {
            myFile = homeworkService.findFileByUploaderAndHomework(loginUser.getId(), homeworkId);
            FileUtil.delete(myFile.getPath()+myFile.getUUIDName());
            myFile.setFileName(fileName);
        }else
            myFile=new UploadFile(loginUser,fileName,uuidName,path.toString());
        FileUtil.upload(file,path.toString(),uuidName);

        myFile=fileService.save(myFile);
        if (!homeworkService.isStudentUploaded(loginUser.getId(),homeworkId))
            homeworkService.addFileToHomework(homeworkId,myFile.getId());
        return RetResponse.success(myFile.getId());
    }

    @SuppressWarnings("ConstantConditions")
    @ApiDoc(description = "根据上传码上传文件",role = RoleList.Unlimited)
    @PostMapping("/uploadWithRandomCode")
    @ResponseBody
    @Unlimited
    public BaseResponse uploadWithRandomCode(String randomCode,@RequestParam MultipartFile file) throws IOException{
        if (!redisTemplate.hasKey(randomCode))
            return RetResponse.error("文件码不存在");
        Map map=redisTemplate.opsForHash().entries(randomCode);
        Long userId=((Integer)map.get("userId")).longValue();
        Long homeworkId=((Integer)map.get("homeworkId")).longValue();
        return upload(userId,homeworkId,file);
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

    @ApiDoc(description = "下载文件",role = RoleList.Teacher)
    @RequestMapping("/{id:.+}")
    public ResponseEntity<FileSystemResource> download(@PathVariable long id) {
        if (fileService.isFileExist(id)) {
            UploadFile myFile = fileService.getFile(id);

            String uuidName = myFile.getUUIDName();
            String filename = myFile.getFileName();
            String path=myFile.getPath();
            FileUtil.createDirIfNotExists(path);
            File file = new File(FileUtil.absolutePath, FileUtil.staticDir+path+ uuidName);
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

    @ApiDoc(description = "将作业文件打包下载",role = RoleList.Unlimited)
    @Unlimited
    @RequestMapping("/homework/{id}")
    public ResponseEntity<FileSystemResource> downloadZip(@PathVariable long id) throws IOException {
        if (homeworkService.isHomeworkExist(id)){
            String name=homeworkService.findById(id).get("name")+".zip";
            FileUtil.compress(id+"/",name);
            File file = new File(FileUtil.absolutePath, FileUtil.staticDir+name);
            if (file.exists()) {
                return ResponseEntity
                        .ok()
                        .header("Content-Disposition", "attachment;fileName="+ new String((name).getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) )
                        .contentLength(file.length())
                        .contentType(MediaType.parseMediaType("application/octet-stream"))
                        .body(new FileSystemResource(file));
            }
        }
        return ResponseEntity.notFound().build();
    }
}
