package main.java.com.lukyanovich.cloudbox.model;

import java.time.LocalDateTime;

public class StoredFile {

    private long id;
    private String fileName;
    private String path;
    private LocalDateTime uploadDate;
    private long userId;

    public StoredFile() {
    }

    public StoredFile(String fileName, String path, LocalDateTime uploadDate, long userId) {
        setFileName(fileName);
        setPath(path);
        setUploadDate(uploadDate);
        setUserId(userId);
    }

    public StoredFile(long id, String fileName, String path, LocalDateTime uploadDate, long userId) {
        this.id = id;
        setFileName(fileName);
        setPath(path);
        setUploadDate(uploadDate);
        setUserId(userId);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate != null ? uploadDate : LocalDateTime.now();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StoredFile storedFile = (StoredFile) o;
        return id == storedFile.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "StoredFile{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", path='" + path + '\'' +
                ", uploadDate=" + uploadDate +
                ", userId=" + userId +
                '}';
    }
}
