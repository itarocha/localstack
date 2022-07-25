package br.com.itarocha.localstack.adapters.s3.services;

import br.com.itarocha.localstack.domain.entities.ArquivoArmazenado;
import br.com.itarocha.localstack.domain.usecases.ProcessadorArquivoUseCase;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AwsS3Service implements ProcessadorArquivoUseCase {

    private final AmazonS3 amazonS3;

    @Value("${config.aws.s3.bucket-name}")
    private String bucketName;

    //private static final String BUCKET_NAME = "my-other-bucket";
    private static final String FILE_EXTENSION = "fileExtension";

    public AwsS3Service(AmazonS3 client){
        this.amazonS3 = client;
    }

    public void testar(){
        List<Bucket> bucketList = amazonS3.listBuckets();
        bucketList.forEach( bucket -> log.info("Bucnet name = {}", bucket.getName()));

        initializeBucket(bucketName);

        ObjectListing objectListing = amazonS3.listObjects(bucketName);
        for (S3ObjectSummary os: objectListing.getObjectSummaries()){
            log.info("Arquivo do bucket {}: {}", bucketName, os.getKey());
        }
    }

    public String uploadArquivo(MultipartFile arquivo) {
        String key = RandomStringUtils.randomAlphanumeric(50);
        try {
            amazonS3.putObject(bucketName, key, arquivo.getInputStream(), extractObjectMetadata(arquivo));
            return key;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public FileInfo uploadObjectToS3(String fileName, byte[] fileData) {
//        log.info("Uploading file '{}' to bucket: '{}' ", fileName, bucketName);
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileData);
//        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setContentLength(fileData.length);
//        String fileUrl =
//                awsS3Config.getS3EndpointUrl() + "/" + awsS3Config.getBucketName() + "/" + fileName;
//        PutObjectResult putObjectResult =
//                amazonS3.putObject(
//                        awsS3Config.getBucketName(), fileName, byteArrayInputStream, objectMetadata);
//        return new FileInfo(fileName, fileUrl, Objects.nonNull(putObjectResult));
//    }

    public S3ObjectInputStream downloadFileFromS3Bucket(final String fileName) {
        log.info("Downloading file '{}' from bucket: '{}' ", fileName, bucketName);
        final S3Object s3Object = amazonS3.getObject(bucketName, fileName);
        return s3Object.getObjectContent();
    }

    public List<ArquivoArmazenado> listarArquivos() {
        log.info("Retrieving object summaries for bucket '{}'",bucketName);
        ObjectListing objectListing = amazonS3.listObjects(bucketName);
        return objectListing.getObjectSummaries().stream().map(objeto -> ArquivoArmazenado.builder()
                    .bucketName(objeto.getBucketName())
                    .key(objeto.getKey())
                    .size(objeto.getSize())
                    .lastModified(objeto.getLastModified().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .build()
        ).collect(Collectors.toList());
    }
    private ObjectMetadata extractObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.getUserMetadata().put(FILE_EXTENSION, FilenameUtils.getExtension(file.getOriginalFilename()));
        return objectMetadata;
    }

    private void initializeBucket(String bucketName) {
        if (!amazonS3.doesBucketExistV2(bucketName)){
            amazonS3.createBucket(bucketName);
        } else {
            System.out.println("Bucket "+bucketName+" existe!");
        }
    }

}
