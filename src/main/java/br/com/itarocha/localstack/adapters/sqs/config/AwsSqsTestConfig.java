package br.com.itarocha.localstack.adapters.sqs.config;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.messaging.MessagingAutoConfiguration;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableAutoConfiguration(exclude = {
        MessagingAutoConfiguration.class,
        ContextStackAutoConfiguration.class
})
@Profile("test")
public class AwsSqsTestConfig {

    @Primary
    @Bean(name = "simpleMessageListenerContainer")
    public SimpleMessageListenerContainer simpleMessageListenerContainer(){
        final SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setAutoStartup(false);
        factory.setAmazonSqs(amazonSQS());
        final SimpleMessageListenerContainer simpleMessageListenerContainer = factory
                .createSimpleMessageListenerContainer();
        simpleMessageListenerContainer.setMessageHandler(messageHandler());
        return simpleMessageListenerContainer;
    }

    @Bean("messageHandler")
    public QueueMessageHandler messageHandler() {
        return Mockito.mock(QueueMessageHandler.class);
    }

    @Bean("amazonSQS")
    public AmazonSQSAsync amazonSQS(){
        return Mockito.mock(AmazonSQSAsync.class);
    }

}
