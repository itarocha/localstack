package br.com.itarocha.localstack.domain.entities;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class ArquivoArmazenado implements Serializable{
    private String bucketName;
    private String key;
    private long size;
    private LocalDateTime lastModified;
}
