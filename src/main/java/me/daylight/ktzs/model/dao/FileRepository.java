package me.daylight.ktzs.model.dao;

import me.daylight.ktzs.model.entity.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Daylight
 * @date 2019/03/09 07:15
 */
public interface FileRepository extends JpaRepository<UploadFile,Long> {
    boolean existsUploadFileById(Long id);

    @Query(value = "select * from upload_file where id in " +
            "(select files_id from homework_files where homework_id=?1) order by upload_time;",nativeQuery = true)
    List<UploadFile> findUploadFilesOfHomework(Long homeworkId);

    @Query(value = "select * from upload_file where uploader_id=?1 and id in " +
            "(select files_id from homework_files where homework_id=?2)",nativeQuery = true)
    UploadFile findFileByUploaderAndHomework(Long uploaderId,Long homeworkId);
}
