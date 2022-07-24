package br.com.itarocha.localstack.application.controllers;

//import org.apache.commons.

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class DocumentoController {

    private final AmazonS3 amazonS3;

    private static final String BUCKET_NAME = "my-other-bucket";
    private static final String FILE_EXTENSION = "fileExtension";

    public DocumentoController(AmazonS3 client){
        this.amazonS3 = client;
    }

    @GetMapping("/test")
    public ResponseEntity teste(){
        testarBucket();
        return new ResponseEntity("Sucesso", HttpStatus.OK);
    }

    @PostMapping(value = "/upload", produces = "application/json")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file){
        String key = uploadFile(file);
        return new ResponseEntity<>(key, HttpStatus.OK);
    }

    //@SneakyThrows
    private String uploadFile(MultipartFile file) {
        String key = RandomStringUtils.randomAlphanumeric(50);
        try {
            amazonS3.putObject(BUCKET_NAME, key, file.getInputStream(), extractObjectMetadata(file));
            return key;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectMetadata extractObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.getUserMetadata().put(FILE_EXTENSION, FilenameUtils.getExtension(file.getOriginalFilename()));
        return objectMetadata;
    }

    private void testarBucket(){
        List<Bucket> bucketList = amazonS3.listBuckets();
        bucketList.forEach( bucket -> System.out.println(bucket.getName()));

        initializeBucket(BUCKET_NAME);

        ObjectListing objectListing = amazonS3.listObjects(BUCKET_NAME);
        for (S3ObjectSummary os: objectListing.getObjectSummaries()){
            System.out.println("*** "+os.getKey());
        }
    }

    private void initializeBucket(String bucketName) {
        if (!amazonS3.doesBucketExistV2(bucketName)){
            amazonS3.createBucket(bucketName);
        } else {
            System.out.println("Bucket "+bucketName+" existe!");
        }
    }
}
