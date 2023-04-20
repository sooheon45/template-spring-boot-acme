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
    public void whenever{{event.namePascalCase}}_{{../namePascalCase}}(@Payload {{event.namePascalCase}} {{event.nameCamelCase}}, 
                                @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment,
                                @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) byte[] messageKey){

        {{event.namePascalCase}} event = {{event.nameCamelCase}};
        System.out.println("\n\n##### listener {{../namePascalCase}} : " + {{event.nameCamelCase}} + "\n\n");

        {{#../aggregateList}}
        {{namePascalCase}}.{{../../nameCamelCase}}(event);        
        {{/../aggregateList}}

    {{#compensateCommand}}
        try {
            {{../command.namePascalCase}}Command {{../command.nameCamelCase}}Command = new {{../command.namePascalCase}}Command();
            /* Logic 
                ...
            */
            {{#todo ../description}}{{/todo}}
            {{../command.aggregate.nameCamelCase}}Service.{{../command.nameCamelCase}}({{../command.nameCamelCase}}Command);
        } catch (Exception e) {
            {{namePascalCase}}Command {{nameCamelCase}}Command = {{namePascalCase}}Command();
           {{#ifEquals 'test' 'test'}}test{{/ifEquals}}
           
        }
    {{else}}
        /* Logic */
        {{#todo ../description}}{{/todo}}
        {{command.namePascalCase}}Command {{command.nameCamelCase}}Command = new {{command.namePascalCase}}Command();
             
        {{#ifEquals event.aggregate.elementView.id command.aggregate.elementView.id}}
        {{event.aggregate.nameCamelCase}}Repository
            .findById(
                // implement: Set the Delivery Id from one of OrderPlaced event's corresponding property
                event.getId()
            )
            .ifPresent({{event.aggregate.nameCamelCase}} -> {
               
                {{event.aggregate.nameCamelCase}}.{{command.nameCamelCase}}({{command.nameCamelCase}}Command);
            });
        {{else}}
        {{command.nameCamelCase}}Command.setId(event.getId());
        {{event.aggregate.nameCamelCase}}.{{command.nameCamelCase}}(updateStatusCommand);
        {{/ifEquals}}
        {{namePascalCase}}Command {{nameCamelCase}}Command = new {{namePascalCase}}Command();
        {{../command.aggregate.nameCamelCase}}Repository.save({{../command.aggregate.nameCamelCase}});
    {{/compensateCommand}}

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
