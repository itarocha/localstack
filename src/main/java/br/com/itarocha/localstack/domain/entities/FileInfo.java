package br.com.itarocha.localstack.domain.entities;

import lombok.Data;

@Data
public class FileInfo {
    private String fileName;
    private String fileUrl;
    private boolean isUploadSuccessFull;

    public FileInfo(String fileName, String fileUrl, boolean isUploadSuccessFull) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.isUploadSuccessFull = isUploadSuccessFull;
    }
}
