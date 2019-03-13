package me.daylight.ktzs.model.dao;

import me.daylight.ktzs.model.entity.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Daylight
 * @date 2019/03/09 07:15
 */
public interface FileRepository extends JpaRepository<UploadFile,Long> {
    boolean existsUploadFileById(Long id);
}
