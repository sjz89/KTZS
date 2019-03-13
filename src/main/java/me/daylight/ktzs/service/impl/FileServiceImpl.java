package me.daylight.ktzs.service.impl;

import me.daylight.ktzs.model.dao.FileRepository;
import me.daylight.ktzs.model.entity.UploadFile;
import me.daylight.ktzs.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Daylight
 * @date 2019/03/09 07:15
 */
@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private FileRepository fileRepository;

    @Override
    public UploadFile save(UploadFile file) {
        return fileRepository.saveAndFlush(file);
    }

    @Override
    public boolean isFileExist(Long id) {
        return fileRepository.existsUploadFileById(id);
    }

    @Override
    public UploadFile getFile(Long id) {
        return fileRepository.getOne(id);
    }

    @Override
    public void deleteFile(Long id) {
        fileRepository.deleteById(id);
    }
}
