forEach: Policy
fileName: {{namePascalCase}}Saga.java
path: {{boundedContext.name}}/{{{options.packagePath}}}/saga
except: {{#isSaga}}false{{else}}true{{/isSaga}}
---
package {{options.package}}.saga;

import {{options.package}}.config.kafka.KafkaProcessor;
import {{options.package}}.domain.*;
import {{options.package}}.external.*;

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
    {{#externalService boundedContext.aggregates contexts.sagaEvents}}{{/externalService}}

    
    {{#contexts.sagaEvents}}
    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='{{event.namePascalCase}}'")
    public void whenever{{event.namePascalCase}}_{{../namePascalCase}}(@Payload {{event.namePascalCase}} {{event.nameCamelCase}}, 
                                @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment,
                                @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) byte[] messageKey){

        {{event.namePascalCase}} event = {{event.nameCamelCase}};
        System.out.println("\n\n##### listener {{../namePascalCase}} : " + {{event.nameCamelCase}} + "\n\n");

    {{#compensateCommand}}
        try {
            {{../command.namePascalCase}}Command {{../command.nameCamelCase}}Command = new {{../command.namePascalCase}}Command();
           /* Logic */
            {{#todo ../description}}{{/todo}}
            {{../command.aggregate.nameCamelCase}}Service.{{../command.nameCamelCase}}({{../command.nameCamelCase}}Command);
        } catch (Exception e) {           
            {{#if outgoingRelations}}
            {{namePascalCase}}Command {{nameCamelCase}}Commad = new {{namePascalCase}}Command();

            {{aggregate.nameCamelCase}}Repository.findById(
                // implement: Set the {{aggregate.namePascalCase}} Id from one of {{../event.namePascalCase}} event's corresponding property
                event.getId()
            )
            .ifPresent({{aggregate.nameCamelCase}} -> {
                {{aggregate.nameCamelCase}}.{{nameCamelCase}}({{nameCamelCase}}Commad);
            });
            {{else}}
                {{aggregate.nameCamelCase}}Service.{{nameCamelCase}}(event.getId());
            {{/if}}

        }
    {{else}}
        /* Logic */
        {{#todo ../description}}{{/todo}}
        {{#command.outgoingRelations}}
            {{#ifEquals source.aggregate.elementView.id target.aggregate.elementView.id}}
            {{../command.aggregate.nameCamelCase}}Repository
            .findById(
                // implement: Set the {{aggregate.namePascalCase}} Id from one of {{../namePascalCase}} event's corresponding property
                event.getId()
            )
            .ifPresent({{../command.aggregate.nameCamelCase}} -> {
                {{../command.namePascalCase}}Command {{../command.nameCamelCase}}Command = new {{../command.namePascalCase}}Command();
                {{../command.aggregate.nameCamelCase}}.{{../command.nameCamelCase}}({{../command.nameCamelCase}}Command);
            });
            {{else}}
            {{command.namePascalCase}}Command {{../command.nameCamelCase}}Command = new {{../command.namePascalCase}}Command();
            
            {{command.nameCamelCase}}Command.setId(event.getId());
            {{event.aggregate.nameCamelCase}}.{{command.nameCamelCase}}(updateStatusCommand);
            {{/ifEquals}}
        {{/command.outgoingRelations}}
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


window.$HandleBars.registerHelper('externalService', function (aggregatesForBc, aggregates) {
   var lists = [];
   var str = ''
   aggregatesForBc.forEach(function(selfAggregate){
    aggregates.forEach(function(agg){
       if(agg && agg.command){
           if(agg.command.aggregate.name != selfAggregate.name){
             lists.push(agg.command.aggregate);
           }
       }
      });
    });
    lists.forEach(function(agg){
        str = str +`${agg.namePascalCase}Service ${agg.nameCamelCase}Service;`
    })
    return str;
});

window.$HandleBars.registerHelper('except', function (fieldDescriptors) {
    return (fieldDescriptors && fieldDescriptors.length == 0);
});
</function>
