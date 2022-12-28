forEach: BoundedContext
fileName: PolicyHandler.java

representativeFor: Policy
path: {{name}}/{{{options.packagePath}}}/infra
mergeType: template
---
package {{options.package}}.infra;

import javax.naming.NameParser;

import javax.naming.NameParser;
import org.springframework.transaction.annotation.Transactional;
import {{options.package}}.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import {{options.package}}.domain.*;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional(rollbackFor = Exception.class)
public class PolicyHandler{
    {{#aggregates}}
    @Autowired {{namePascalCase}}Repository {{nameCamelCase}}Repository;
    {{/aggregates}}
    
    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString, 
                                @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment,
                                @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) byte[] messageKey){
          /*
          // Call port method with received messageKey to publish msg to the same partition. //
          DomainClass.portMethod(eventString, new String(messageKey));
          
          // ,or //
          new EventRaised(domain Obj).publishAfterCommit(new String(messageKey));
          // manual Offset Commit. //
          acknowledgment.acknowledge();  
          */
    }

    {{#policies}}
    {{#relationAggregateInfo}}
    @Autowired
    {{../../options.package}}.external.{{aggregateValue.namePascalCase}}Service {{aggregateValue.nameCamelCase}}Service;

    {{/relationAggregateInfo}}
        {{#relationEventInfo}}
    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='{{eventValue.namePascalCase}}'")
    public void whenever{{eventValue.namePascalCase}}_{{../namePascalCase}}(@Payload {{eventValue.namePascalCase}} {{eventValue.nameCamelCase}}, 
                                @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment,
                                @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) byte[] messageKey){

        {{eventValue.namePascalCase}} event = {{eventValue.nameCamelCase}};
        System.out.println("\n\n##### listener {{../namePascalCase}} : " + {{eventValue.nameCamelCase}} + "\n\n");

        {{#../relationAggregateInfo}}
        // REST Request Sample
        
        // {{aggregateValue.nameCamelCase}}Service.get{{aggregateValue.namePascalCase}}(/** mapping value needed */);

        {{/../relationAggregateInfo}}

        {{#todo ../description}}{{/todo}}

        // Sample Logic //
        {{#../aggregateList}}
        {{namePascalCase}}.{{../../nameCamelCase}}(event);
        
        {{/../aggregateList}}

        // Manual Offset Commit //
        acknowledgment.acknowledge();

    }
        {{/relationEventInfo}}

    {{/policies}}
}

//>>> Clean Arch / Inbound Adaptor


<function>
window.$HandleBars.registerHelper('todo', function (description) {

    if(description){
        description = description.replaceAll('\n','\n\t\t// ')
        return description = '// Comments // \n\t\t//' + description;
    }
     return null;
});
</function>
