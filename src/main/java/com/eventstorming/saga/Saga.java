package {{options.package}}.saga;

import {{options.package}}.config.kafka.KafkaProcessor;
import {{options.package}}.domain.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;


public class {{namePascalCase}}Saga {

    @Autowired
    {{#boundedContext.aggregates}}
        {{namePascalCase}}Repository {{nameCamelCase}}Repository;
    {{/boundedContext.aggregates}}
    
    {{#contexts.sagaEvents}}
   
    {{/contexts.sagaEvents}}

}
