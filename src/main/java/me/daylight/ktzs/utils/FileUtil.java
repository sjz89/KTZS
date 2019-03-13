package me.daylight.ktzs.utils;

import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

/**
 * @author Daylight
 * @date 2018/12/5 10:56
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileUtil {

    /** 绝对路径 **/
    public static String absolutePath = "";

    /** 静态目录 **/
    public static String staticDir = "static/upload/";

    public static void upload(MultipartFile file, String targetPath, String fileName) throws IOException {
        //第一次会创建文件夹
        createDirIfNotExists(targetPath);

        //存文件
        File uploadFile = new File(absolutePath, staticDir + targetPath+fileName);
        file.transferTo(uploadFile);
    }

    /**
     * 创建文件夹路径
     */
    public static void createDirIfNotExists(@Nullable String path) {
        if (absolutePath.isEmpty()) {

            //获取跟目录
            File file;
            try {
                file = new File(ResourceUtils.getURL("classpath:").getPath());
            } catch (FileNotFoundException e) {
                throw new RuntimeException("获取根目录失败，无法创建上传目录！");
            }
            if (!file.exists()) {
                file = new File("");
            }

            absolutePath = file.getAbsolutePath();
        }

        if (path!=null) {
            File upload = new File(absolutePath, staticDir + path);
            if (!upload.exists()) {
                upload.mkdirs();
            }
        }
    }

    public static boolean delete(String sourcePath) {
        createDirIfNotExists(null);
        File file = new File(absolutePath, staticDir+sourcePath);
        return file.exists() && file.delete();
    }

    public static String getSuffix(String fileName) {
        if(!fileName.contains(".")) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf(".");
        return fileName.substring(dotIndex, fileName.length());
    }

    public static void delFolder(String folderPath) {
        try {
            createDirIfNotExists(null);
            delAllFile(folderPath); //删除完里面所有内容
            File myFilePath = new File(absolutePath,staticDir+ folderPath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void delAllFile(String path) {
        File file = new File(absolutePath,staticDir+path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp;
        for (String aTempList : Objects.requireNonNull(tempList)) {
            if (path.endsWith(File.separator)) {
                temp = new File(absolutePath,staticDir+path + aTempList);
            } else {
                temp = new File(absolutePath,staticDir+path + File.separator + aTempList);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + aTempList);//先删除文件夹里面的文件
                delFolder(path + "/" + aTempList);//再删除空文件夹
            }
        }
    }
}
