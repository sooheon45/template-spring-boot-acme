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

    {{#externalService boundedContext.aggregates contexts.sagaEvents}}
    @Autowired
    {{namePascalCase}}Service {{nameCamelCase}}Service;
    {{/externalService}}

    
{{#contexts.sagaEvents}}
    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='{{event.namePascalCase}}'")
    public void whenever{{event.namePascalCase}}_{{../namePascalCase}}
        (@Payload {{event.namePascalCase}} {{event.nameCamelCase}}, 
         @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment,
         @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) byte[] messageKey){

        {{event.namePascalCase}} event = {{event.nameCamelCase}};
        System.out.println("\n\n##### listener {{../namePascalCase}} : " + {{event.nameCamelCase}} + "\n\n");

    {{#compensateCommand}}
        try {
        {{#if ../command.isRestRepository}}
            {{../command.aggregate.namePascalCase}} {{../command.aggregate.nameCamelCase}} = new  {{../command.aggregate.namePascalCase}}();
            {{#correlationGetSet ../command.aggregate ../../event}}
             /* Logic */
            {{../../command.aggregate.nameCamelCase}}.set{{source.namePascalCase}}(event.get{{target.namePascalCase}}());
            {{/correlationGetSet}}
            {{../command.aggregate.nameCamelCase}}Service.{{../command.nameCamelCase}}({{../command.aggregate.nameCamelCase}});
        {{else}}
            {{../command.namePascalCase}}Command {{../command.nameCamelCase}}Command = new {{../command.namePascalCase}}Command();
            /* Logic */
            {{#correlationGetSet ../event ../command}}
            {{../../command.nameCamelCase}}Command.set{{source.namePascalCase}}(event.get{{target.namePascalCase}}());
            {{/correlationGetSet}}

            {{#correlationKey ../command}}
            {{../../command.aggregate.nameCamelCase}}Service.{{../../command.nameCamelCase}}(event.get{{namePascalCase}}(), {{../../command.nameCamelCase}}Command);
            {{else}}
            {{../../command.aggregate.nameCamelCase}}Service.{{../../command.nameCamelCase}}(event.getId(), {{../../command.nameCamelCase}}Command);
            {{/correlationKey}}
        {{/if}}
        } catch (Exception e) {
            {{#isEqualsAggregateOfSaga ../../contexts.sagaEvents aggregate.elementView.id}}
            {{../aggregate.nameCamelCase}}Repository.findById(
            // implement: Set the {{../command.aggregate.nameCamelCase}} Id from one of {{event.nameCamelCase}} event's corresponding property
            {{#correlationKey ../../event}}
                event.get{{namePascalCase}}()
            {{/correlationKey}}
            ).ifPresent({{aggregate.nameCamelCase}} -> {
                {{../namePascalCase}}Command {{../nameCamelCase}}Commad = new {{../namePascalCase}}Command();
                /* Logic */
                {{#correlationGetSet ../../event ..}}
                {{#if target}}
                {{../../nameCamelCase}}Command.set{{source.namePascalCase}}(event.get{{target.namePascalCase}}());
                {{else}}
                // A correlation key is required.
                //{{../../nameCamelCase}}Command.set{{source.namePascalCase}}();
                {{/if}}
                {{/correlationGetSet}}

                {{../aggregate.nameCamelCase}}.{{../nameCamelCase}}({{../nameCamelCase}}Commad);
               });
            {{else}}
            {{../namePascalCase}}Command {{../nameCamelCase}}Commad = new {{../namePascalCase}}Command();
            /* Logic */
            {{#correlationGetSet ../../event ..}}
             {{#if target}}
            {{../../nameCamelCase}}Command.set{{source.namePascalCase}}(event.get{{target.namePascalCase}}());
            {{else}}
            // A correlation key is required.
            //{{../../nameCamelCase}}Command.set{{source.namePascalCase}}();
            {{/if}}
            {{/correlationGetSet}}

            {{#correlationKey ..}}
            {{../../aggregate.nameCamelCase}}Service.{{../../nameCamelCase}}(event.get{{namePascalCase}}(),{{../../nameCamelCase}}Commad);
            {{else}}
            {{../../aggregate.nameCamelCase}}Service.{{../../nameCamelCase}}(event.getId(),{{../../nameCamelCase}}Commad);
            {{/correlationKey}}
            {{/isEqualsAggregateOfSaga}}
        }
    {{else}}
            {{#if command}}
            {{#command.outgoingRelations}}
        {{#isEqualsAggregateOfSaga ../../contexts.sagaEvents source.aggregate.elementView.id}}
        {{#if ../../command.isRestRepository}}
         {{../../command.aggregate.namePascalCase}} {{../../command.aggregate.nameCamelCase}} = new  {{../../command.aggregate.namePascalCase}}();
         {{../../command.aggregate.nameCamelCase}}Service.{{../../command.nameCamelCase}}({{../../command.aggregate.nameCamelCase}});
        {{else}}
        {{../../command.aggregate.nameCamelCase}}Repository.findById(
            // implement: Set the {{../../command.aggregate.nameCamelCase}} Id from one of {{../event.nameCamelCase}} event's corresponding property
        {{#correlationKey ../../event}}
            event.get{{namePascalCase}}()
        {{/correlationKey}}
        ).ifPresent({{../../command.aggregate.nameCamelCase}} -> {
            {{../../command.namePascalCase}}Command {{../../command.nameCamelCase}}Command = new {{../../command.namePascalCase}}Command();
            /* Logic */
            {{#correlationGetSet ../../command ../../event}}
            {{#if target}}
            {{../../../command.nameCamelCase}}Command.set{{source.namePascalCase}}(event.get{{target.namePascalCase}}());
                {{else}}
            // A correlation key is required.
            //{{../../nameCamelCase}}Command.set{{source.namePascalCase}}();
                {{/if}}
            {{/correlationGetSet}}
            
            {{../../command.aggregate.nameCamelCase}}.{{../../command.nameCamelCase}}({{../../command.nameCamelCase}}Command);
        }); 
        {{/if}}
        {{else}}
        {{#if ../../command.isRestRepository}}
        {{../../command.aggregate.namePascalCase}} {{../../command.aggregate.nameCamelCase}} = new {{../../command.aggregate.namePascalCase}}();
        {{../../command.aggregate.nameCamelCase}}Service.{{../../command.nameCamelCase}}({{../../command.aggregate.nameCamelCase}});
        {{else}}
        {{../../command.nameCamelCase}}Command {{../../command.nameCamelCase}}Commad = new {{../../command.nameCamelCase}}Command();
        /* Logic */ 
        {{#correlationGetSet ../../event ../../command}}
        {{../../../command.nameCamelCase}}Command.set{{source.namePascalCase}}(event.get{{target.namePascalCase}}());
        {{/correlationGetSet}}
    
        {{../../event.aggregate.nameCamelCase}}.{{../../command.nameCamelCase}}({{../../command.nameCamelCase}}Command);
        {{../../command.aggregate.nameCamelCase}}Service.{{../nameCamelCase}}({{../nameCamelCase}}Commad);
        {{/if}}   
            {{/isEqualsAggregateOfSaga}}    
      {{/command.outgoingRelations}}
      {{else}}
        /* Logic */
      {{/if}}
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
    this.contexts.sagaEvents = eventByNames; 
}

window.$HandleBars.registerHelper('correlationKey', function (source, options) {
    let str = '';
    
    if(source && source.fieldDescriptors){
        let srcObj = source.fieldDescriptors.find(x=> x.isCorrelationKey);
        return options.fn(srcObj);
    }
    return options.fn(str);
});

window.$HandleBars.registerHelper('correlationGetSet', function (setter, getter,options) {
    let obj = {
        source: null,
        target: null
    };
   
    if(setter && setter.fieldDescriptors){
        obj.source = setter.fieldDescriptors.find(x=> x.isCorrelationKey);
    }
    if(getter && getter.fieldDescriptors){
        obj.target = getter.fieldDescriptors.find(x => x.isCorrelationKey);
    }
    
    return options.fn(obj);
});


window.$HandleBars.registerHelper('externalService', function (aggregatesForBc, aggregates, options) {
   var str = '';
   aggregatesForBc.forEach(function(selfAggregate){
    aggregates.forEach(function(agg){
       if(agg && agg.command){
           str = str + options.fn(agg.command.aggregate);
       }
      });
    });
    return str
});

window.$HandleBars.registerHelper('isEqualsAggregateOfSaga', function (saga, aggregateId, options) {
   let isEquals = false;
   let startSaga = saga.find(x=> x && x.isStartSaga);
   if(startSaga && startSaga.event && startSaga.event.aggregate && aggregateId){
        isEquals = startSaga.event.aggregate.elementView.id == aggregateId
    }
    if(isEquals){
        return options.fn(isEquals);
    }
    return options.inverse(isEquals);
});

window.$HandleBars.registerHelper('except', function (fieldDescriptors) {
    return (fieldDescriptors && fieldDescriptors.length == 0);
});
</function>
