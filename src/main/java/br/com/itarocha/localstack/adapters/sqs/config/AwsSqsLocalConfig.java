package br.com.itarocha.localstack.adapters.sqs.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.support.NotificationMessageArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;

import java.util.Collections;

@Configuration
//@Profile("local")
public class AwsSqsLocalConfig {
    @Value("${config.aws.region}")
    private String region;

    @Value("${config.aws.url}")
    private String sqsEndpointUrl;

    @Value("${config.aws.access-key}")
    private String accessKey;

    @Value("${config.aws.secret-key}")
    private String secretKey;

    @Value("${config.aws.sqs.queue-name}")
    private String queueName;

    @Bean
    @Primary
    public AmazonSQSAsync amazonSQS() {
        return AmazonSQSAsyncClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(sqsEndpointUrl, region))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate() {
        return new QueueMessagingTemplate(amazonSQS());
    }

    @Bean
    protected MessageConverter messageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setSerializedPayloadClass(String.class);
        converter.setStrictContentTypeMatch(false);
        return converter;
    }








//    @Primary
//    @Bean(name = "amazonSQS")
//    public AmazonSQSAsync amazonSQS() {
//        return AmazonSQSAsyncClientBuilder.standard()
//                .withCredentials(getCredentialsProvider())
//                .withEndpointConfiguration(getEndpointConfiguration(sqsEndpointUrl, region))
//                .build();
//    }
//
//    @Bean(name = "queueMessagingTemplate")
//    public QueueMessagingTemplate queueMessagingTemplate(@Autowired AmazonSQSAsync amazonSQS) {
//        return new QueueMessagingTemplate(amazonSQS);
//    }
//
//    @Bean
//    protected MessageConverter messageConverter(ObjectMapper objectMapper) {
//        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//        converter.setObjectMapper(objectMapper);
//        converter.setSerializedPayloadClass(String.class);
//        converter.setStrictContentTypeMatch(false);
//        return converter;
//    }
//    @Bean(name = "queueMessageHandlerFactory")
//    public QueueMessageHandlerFactory queueMessageHandlerFactory(
//            MessageConverter messageConverter, AmazonSQSAsync amazonSQSAsync) {
//        QueueMessageHandlerFactory queueMessageHandlerFactory = new QueueMessageHandlerFactory();
//        NotificationMessageArgumentResolver notificationMessageArgumentResolver =
//                new NotificationMessageArgumentResolver(messageConverter);
//        queueMessageHandlerFactory.setAmazonSqs(amazonSQSAsync);
//        queueMessageHandlerFactory.setArgumentResolvers(
//                Collections.singletonList(notificationMessageArgumentResolver));
//        return queueMessageHandlerFactory;
//    }

//    private AwsClientBuilder.EndpointConfiguration getEndpointConfiguration(String url, String region) {
//        return new AwsClientBuilder.EndpointConfiguration(url, region);
//    }
//
//    private AWSStaticCredentialsProvider getCredentialsProvider() {
//        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
//    }
}
