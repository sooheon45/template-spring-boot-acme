forEach: BoundedContext
fileName: PolicyHandler.java

representativeFor: Policy
path: {{name}}/{{{options.packagePath}}}/infra
mergeType: template
---
package {{options.package}}.infra;

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
    {{#outgoing "ReadModel" .}}
    @Autowired
    {{../../options.package}}.external.{{aggregate.namePascalCase}}Service {{aggregate.nameCamelCase}}Service;

    {{/outgoing}}

    {{#incoming "Event" .}}
    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='{{namePascalCase}}'")
    public void whenever{{namePascalCase}}_{{../namePascalCase}}(@Payload {{namePascalCase}} {{nameCamelCase}}, 
                                @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment,
                                @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) byte[] messageKey){

        {{namePascalCase}} event = {{nameCamelCase}};
        System.out.println("\n\n##### listener {{../namePascalCase}} : " + {{nameCamelCase}} + "\n\n");

        {{#../aggregateList}}
        {{namePascalCase}}.{{../../nameCamelCase}}(event);        
        {{/../aggregateList}}

        {{#outgoing "Command" ..}}
        {{#isExtendedVerb}}
        {{namePascalCase}}Command {{nameCamelCase}}Command = new {{namePascalCase}}Command();
        // implement:  Map command properties from event

        {{aggregate.nameCamelCase}}Repository.findById(
                // implement: Set the {{aggregate.namePascalCase}} Id from one of {{../namePascalCase}} event's corresponding property
                
            ).ifPresent({{aggregate.nameCamelCase}}->{
             {{aggregate.nameCamelCase}}.{{nameCamelCase}}({{nameCamelCase}}Command); 
        });
        {{else}}
        {{aggregate.namePascalCase}} {{aggregate.nameCamelCase}} = new {{aggregate.namePascalCase}}();
        {{aggregate.nameCamelCase}}Repository.save({{aggregate.nameCamelCase}});
        {{/isExtendedVerb}}
        {{/outgoing}}


        {{#todo ../description}}{{/todo}}

        // Manual Offset Commit //
        acknowledgment.acknowledge();

    }
    {{/incoming}}

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
