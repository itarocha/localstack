package br.com.itarocha.localstack.adapters.sqs.services;

import com.amazonaws.services.s3.event.S3EventNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AwsSQSEventService {

    @SqsListener(value = "${cloud.aws.sqs.queue-event-s3-name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void receiveMessage(String message) {
        log.info("SQS EVENT Message Received : {}", message);
        String msg = getS3UrlFromSQSEvent(message);
    }

    private String getS3UrlFromSQSEvent(String event) {
        ObjectMapper objectMapper = new ObjectMapper();
        S3EventNotification s3Event = null;
        try {
            s3Event = objectMapper.readValue(event, S3EventNotification.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return null;
        }

        List<S3EventNotification.S3EventNotificationRecord> records = s3Event.getRecords();
        //Assume event has only one record
        if(records.isEmpty()) return "";
        S3EventNotification.S3EventNotificationRecord r = records.get(0);
        String region = r.getAwsRegion(); //Assume region is not default, the S3 URL is different for us-east-1
        String bucketName = r.getS3().getBucket().getName();
        String fileName = r.getS3().getObject().getKey();
        StringBuilder builder = new StringBuilder();
        builder.append("https://")
                .append(bucketName)
                .append(".s3.")
                .append(region)
                .append(".amazonaws.com/")
                .append(fileName);

        log.info("MENSAGEM DECODIFICADA: {}", builder.toString());

        return builder.toString();
    }

}
