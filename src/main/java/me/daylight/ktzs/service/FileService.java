package me.daylight.ktzs.service;

import me.daylight.ktzs.model.entity.UploadFile;

/**
 * @author Daylight
 * @date 2019/03/09 07:14
 */
public interface FileService {
    UploadFile save(UploadFile file);

    boolean isFileExist(Long id);

    UploadFile getFile(Long id);

    void deleteFile(Long id);
}
