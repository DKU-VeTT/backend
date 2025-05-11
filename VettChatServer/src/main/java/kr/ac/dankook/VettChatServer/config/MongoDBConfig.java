package kr.ac.dankook.VettChatServer.config;

import kr.ac.dankook.VettChatServer.config.converter.DateToLocalDateTimeKstConverter;
import kr.ac.dankook.VettChatServer.config.converter.LocalDateTimeToDateKstConverter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class MongoDBConfig {

    @Bean
    public MongoCustomConversions customConversions(
            LocalDateTimeToDateKstConverter localDateTimeToDateKstConverter,
            DateToLocalDateTimeKstConverter dateToLocalDateTimeKstConverter) {
        return new MongoCustomConversions(
                Arrays.asList(localDateTimeToDateKstConverter, dateToLocalDateTimeKstConverter)
        );
    }

    @Bean
    public static BeanPostProcessor mappingMongoConverterPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            @NonNull
            public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) {
                if (bean instanceof MappingMongoConverter converter) {
                    converter.setTypeMapper(new DefaultMongoTypeMapper(null));
                }
                return bean;
            }
        };
    }
}