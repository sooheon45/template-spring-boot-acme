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
import org.springframework.stereotype.Service;

@Service
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
            {{../command.aggregate.namePascalCase}} {{../command.aggregate.nameCamelCase}} = new {{../command.aggregate.namePascalCase}}();
            /* Logic */
        {{#correlationGetSet ../command.aggregate ../event}}
            {{#if target}}
            {{../../command.aggregate.nameCamelCase}}.set{{source.namePascalCase}}(event.get{{target.namePascalCase}}());
            {{else}}
            // A correlation key is required.
            //{{../../command.aggregate.nameCamelCase}}.set{{source.namePascalCase}}( ... );
            {{/if}}
        {{/correlationGetSet}}
            
            {{../command.aggregate.nameCamelCase}}Service.{{../command.nameCamelCase}}({{../command.aggregate.nameCamelCase}});
    {{else}}
            {{../command.namePascalCase}}Command {{../command.nameCamelCase}}Command = new {{../command.namePascalCase}}Command();
            /* Logic */
        {{#correlationGetSet ../event ../command}}
            {{#if target}}
            {{../../command.nameCamelCase}}Command.set{{target.namePascalCase}}(event.get{{source.namePascalCase}}());
            {{else}}
            // A correlation key is required.
            //{{../../command.nameCamelCase}}Command.set{{target.namePascalCase}}( ... );
            {{/if}}         
        {{/correlationGetSet}}

        {{#correlationKey ../event}}
            {{../../command.aggregate.nameCamelCase}}Service.{{../../command.nameCamelCase}}(event.get{{namePascalCase}}(), {{../../command.nameCamelCase}}Command);
        {{else}}
            {{../../command.aggregate.nameCamelCase}}Service.{{../../command.nameCamelCase}}(event.getId(), {{../../command.nameCamelCase}}Command);
        {{/correlationKey}}
    {{/if}}
        } catch (Exception e) {
           {{namePascalCase}}Command {{nameCamelCase}}Command = new {{namePascalCase}}Command();
            /* Logic */
{{#correlationGetSet ../event .}}
    {{#if target}}
           {{../nameCamelCase}}Command.set{{target.namePascalCase}}(event.get{{source.namePascalCase}}());
    {{else}}
           // A correlation key is required.
           //{{../nameCamelCase}}Command.set{{target.namePascalCase}}( .. );
    {{/if}}
{{/correlationGetSet}}

{{#correlationKey ../event}}
           {{../aggregate.nameCamelCase}}Service.{{../nameCamelCase}}(event.get{{namePascalCase}}(),{{../nameCamelCase}}Command);
    {{else}}
           {{../aggregate.nameCamelCase}}Service.{{../nameCamelCase}}(event.getId(),{{../nameCamelCase}}Command);
{{/correlationKey}}
        }
    {{else}}
{{#if command}}
    {{#command.outgoingRelations}}
        {{#if ../command.isRestRepository}}
            {{../command.aggregate.namePascalCase}} {{../command.aggregate.nameCamelCase}} = new {{../command.aggregate.namePascalCase}}();
             /* Logic */ 
            {{#correlationGetSet ../command.aggregate ../event}}
              {{#if target}}
              {{../../command.aggregate.nameCamelCase}}.set{{source.namePascalCase}}(event.get{{target.namePascalCase}}());
              {{else}}
             // A correlation key is required.
             //{{../../command.aggregate.nameCamelCase}}.set{{source.namePascalCase}}(...);
              {{/if}}
            {{/correlationGetSet}}
          
            {{../command.aggregate.nameCamelCase}}Service.{{../command.nameCamelCase}}({{../command.aggregate.nameCamelCase}});
        {{else}}
            {{../command.namePascalCase}}Command {{../command.nameCamelCase}}Command = new {{../command.namePascalCase}}Command();
            /* Logic */ 
            {{#correlationGetSet ../event ../command}}
              {{#if target}}
            {{../../command.nameCamelCase}}Command.set{{target.namePascalCase}}(event.get{{source.namePascalCase}}());
              {{else}}
             // A correlation key is required.
             //{{../../command.nameCamelCase}}Command.set{{target.namePascalCase}}(...);
              {{/if}}
            {{/correlationGetSet}}
        
        {{#correlationKey ../event}}
             {{../../command.aggregate.nameCamelCase}}Service.{{../../command.nameCamelCase}}(event.get{{namePascalCase}}(), {{../../command.nameCamelCase}}Command);
        {{else}}
            {{../../command.aggregate.nameCamelCase}}Service.{{../../command.nameCamelCase}}(event.getId(), {{../../command.nameCamelCase}}Command);
        {{/correlationKey}}
        {{/if}}
    {{else}}
             {{command.aggregate.namePascalCase}} {{command.aggregate.nameCamelCase}} = new {{command.aggregate.namePascalCase}}();
             /* Logic */ 
            {{#correlationGetSet command.aggregate event}}
              {{#if target}}
              {{../command.aggregate.nameCamelCase}}.set{{source.namePascalCase}}(event.get{{target.namePascalCase}}());
              {{else}}
             // A correlation key is required.
             //{{../command.aggregate.nameCamelCase}}.set{{source.namePascalCase}}(...);
              {{/if}}
            {{/correlationGetSet}}
            {{command.aggregate.nameCamelCase}}Service.{{command.nameCamelCase}}({{command.aggregate.nameCamelCase}});
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
    let setField = null;
    let getField = null;
    
    
    if(setter && setter._type.endsWith('Aggregate')){
        setField = setter.aggregateRoot.fieldDescriptors;
    } else {
        setField = setter ? setter.fieldDescriptors : null
    }
    
    if(getter && getter._type.endsWith('Aggregate')){
        getField = getter.aggregateRoot.fieldDescriptors
    } else {
        getField = getter ? getter.fieldDescriptors : null
    }
    
    if(setField){
        obj.source = setField.find(x=> x.isCorrelationKey);
    }
    if(getField){
        obj.target = getField.find(x=> x.isCorrelationKey);
    }
    
    return options.fn(obj);
});


window.$HandleBars.registerHelper('externalService', function (aggregatesForBc, aggregates, options) {
   var str = '';
   aggregatesForBc.forEach(function(selfAggregate){
       if(aggregates) {
          aggregates.forEach(function(agg){
            if(agg && agg.command && !str.includes(options.fn(agg.command.aggregate))){
                str = str + options.fn(agg.command.aggregate);
            }
          });    
       }
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
