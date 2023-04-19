forEach: Policy
fileName: {{namePascalCase}}Saga.java
path: {{boundedContext.name}}/{{{options.packagePath}}}/saga
except: {{#isSaga}}false{{else}}true{{/isSaga}}
---
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
        {{command.aggregate.namePascalCase}}Service {{command.aggregate.nameCamelCase}}Service; 
    {{/contexts.sagaEvents}}

    
    {{#contexts.sagaEvents}}
    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='{{event.namePascalCase}}'")
    public void whenever{{namePascalCase}}_{{../namePascalCase}}(@Payload {{event.namePascalCase}} {{event.nameCamelCase}}, 
                                @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment,
                                @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) byte[] messageKey){

        {{event.namePascalCase}} event = {{event.nameCamelCase}};
        System.out.println("\n\n##### listener {{../namePascalCase}} : " + {{nameCamelCase}} + "\n\n");

        {{#../aggregateList}}
        {{namePascalCase}}.{{../../nameCamelCase}}(event);        
        {{/../aggregateList}}

        {{#command.isExtendedVerb}}
        {{command.namePascalCase}}Command {{command.nameCamelCase}}Command = new {{command.namePascalCase}}Command();
        // implement:  Map command properties from event

        {{command.aggregate.nameCamelCase}}Repository.findById(
                // implement: Set the {{command.aggregate.namePascalCase}} Id from one of {{../namePascalCase}} event's corresponding property
                
            ).ifPresent({{aggregate.nameCamelCase}}->{
             {{aggregate.nameCamelCase}}.{{nameCamelCase}}({{nameCamelCase}}Command); 
        });
        {{else}}
            {{#compensateCommand}}
        try {
            {{aggregate.namePascalCase}} {{aggregate.nameCamelCase}} = new {{command.aggregate.namePascalCase}}();
      
        } catch (Exception e) {
            
        }
            {{else}}
        {{command.aggregate.namePascalCase}} {{command.aggregate.nameCamelCase}} = new {{command.aggregate.namePascalCase}}();
        {{command.aggregate.nameCamelCase}}Repository.save({{command.aggregate.nameCamelCase}});
            {{/compensateCommand}}
        {{/command.isExtendedVerb}}

        {{#todo ../description}}{{/todo}}

        // Manual Offset Commit //
        acknowledgment.acknowledge();
    }

    {{/contexts.sagaEvents}}
    
}

<function>
if(this.isSaga){
    var eventByNames = []
    var commandByNames = {}
    this.outgoingCommandRefs.forEach(
        commandRef => {
            commandByNames[commandRef.name] = commandRef.value
        }
    )

    var me = this;
    var i = 1;
    var maxSeq = 0;
    this.incomingEventRefs.forEach(
        eventRef => {
            var nameNumberPart = eventRef.name.replace(/\D/g, "");
    //        alert(nameNumberPart);
            var sequence = parseInt(nameNumberPart); // i
            if(sequence > maxSeq) maxSeq = sequence;

            var commandSequence = sequence + 1;
            eventByNames[sequence] = {
                event: eventRef.value,
                command: commandByNames[commandSequence],
                compensateCommand: commandByNames[commandSequence+"'"],
                isStartSaga: sequence ==1,
                isEndSaga: false
            };
        }
    )

    eventByNames[maxSeq].isEndSaga = true;
    //alert('x')
    //alert(JSON.stringify(commandByNames))

    this.contexts.sagaEvents = eventByNames; 


}

window.$HandleBars.registerHelper('except', function (fieldDescriptors) {
    return (fieldDescriptors && fieldDescriptors.length == 0);
});
</function>
