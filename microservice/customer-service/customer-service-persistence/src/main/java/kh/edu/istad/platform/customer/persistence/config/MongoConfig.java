package kh.edu.istad.platform.customer.persistence.config;



import org.bson.UuidRepresentation;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MongoConfig {



    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer() {
        return settingsBuilder ->
                settingsBuilder.uuidRepresentation(UuidRepresentation.STANDARD);
    }
}