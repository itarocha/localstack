package br.com.itarocha.localstack.infrastructure.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
//@Profile("local")
public class AWSS3LocalConfig {

    @Bean
    public AmazonS3 amazonS3(){
        return AmazonS3ClientBuilder.standard()
                .withCredentials(getCredentialsProvider())
                .withEndpointConfiguration(getEndpointConfiguration("http://localhost:4566"))
                .withPathStyleAccessEnabled(true)
                .build();
    }

    private AWSStaticCredentialsProvider getCredentialsProvider(){
        return new AWSStaticCredentialsProvider(getBasicAWSCredentials());
    }

    private BasicAWSCredentials getBasicAWSCredentials(){
        return new BasicAWSCredentials("asdf", "asdf");
    }

    private AwsClientBuilder.EndpointConfiguration getEndpointConfiguration(String url){
        return new AwsClientBuilder.EndpointConfiguration(url, "us-east-1");
    }
}
