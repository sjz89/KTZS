package me.daylight.ktzs.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Daylight
 * @date 2019/03/09 06:37
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class UploadFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User uploader;

    private String fileName;

    private String UUIDName;

    private String path;

    @CreatedDate
    @LastModifiedDate
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

    public UploadFile(){

    }

    public UploadFile(User uploader, String fileName, String UUIDName, String path) {
        this.uploader = uploader;
        this.fileName = fileName;
        this.UUIDName = UUIDName;
        this.path = path;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUUIDName() {
        return UUIDName;
    }

    public void setUUIDName(String UUIDName) {
        this.UUIDName = UUIDName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public User getUploader() {
        return uploader;
    }

    public void setUploader(User uploader) {
        this.uploader = uploader;
    }
}
