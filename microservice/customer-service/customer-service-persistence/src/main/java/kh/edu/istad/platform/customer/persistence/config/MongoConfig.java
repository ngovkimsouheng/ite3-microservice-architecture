package kh.edu.istad.platform.customer.persistence.config;



import kh.edu.istad.platform.customer.persistence.convert.CustomerIdReadConverter;
import kh.edu.istad.platform.customer.persistence.convert.CustomerIdWriteConverter;
import org.bson.UuidRepresentation;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;


@Configuration
@EnableReactiveMongoAuditing
public class MongoConfig {



    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer() {
        return settingsBuilder ->
                settingsBuilder.uuidRepresentation(UuidRepresentation.STANDARD);
    }

    @Bean
    public MongoCustomConversions bean(){
        return MongoCustomConversions.create(
                adapter->{
                    adapter.registerConverter(new CustomerIdReadConverter());
                    adapter.registerConverter(new CustomerIdWriteConverter());
                }
        );
    }
}