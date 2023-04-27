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
    {{externalService boundedContext.aggregates contexts.sagaEvents}}

    
    {{#contexts.sagaEvents}}
    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='{{event.namePascalCase}}'")
    public void whenever{{event.namePascalCase}}_{{../namePascalCase}}(@Payload {{event.namePascalCase}} {{event.nameCamelCase}}, 
                                @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment,
                                @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) byte[] messageKey){

        {{event.namePascalCase}} event = {{event.nameCamelCase}};
        System.out.println("\n\n##### listener {{../namePascalCase}} : " + {{event.nameCamelCase}} + "\n\n");

    {{#compensateCommand}}
        try {
            
            {{#if ../command.isRestRepository}}
                {{../command.aggregate.namePascalCase}} {{../command.aggregate.nameCamelCase}} = new  {{../command.aggregate.namePascalCase}}();
                {{../command.aggregate.nameCamelCase}}Service.{{../command.nameCamelCase}}({{../command.aggregate.nameCamelCase}});
            {{else}}
                {{../command.namePascalCase}}Command {{../command.nameCamelCase}}Command = new {{../command.namePascalCase}}Command();

                 /* Logic */
                {{#correlationGetSet ../event ../command}}{{/correlationGetSet}}
                {{../command.aggregate.nameCamelCase}}Service.{{../command.nameCamelCase}}({{../command.nameCamelCase}}Command);
            {{/if}}
        } catch (Exception e) {           
            {{#if outgoingRelations}}
            {{#correlationGetSet . ../event}}{{/correlationGetSet}}
            {{aggregate.nameCamelCase}}Repository.findById(
                // implement: Set the {{../command.aggregate.nameCamelCase}} Id from one of {{event.nameCamelCase}} event's corresponding property
                {{#correlationKey ../event}}
                    event.get{{namePascalCase}}()
                {{/correlationKey}}
            )
            .ifPresent({{aggregate.nameCamelCase}} -> {
                 {{namePascalCase}}Command {{nameCamelCase}}Commad = new {{namePascalCase}}Command();
                
                /* Logic */
                {{#correlationGetSet ../event .}}
                  {{source.name}}
                {{/correlationGetSet}}
                
                {{aggregate.nameCamelCase}}.{{nameCamelCase}}({{nameCamelCase}}Commad);
            });
            {{else}}
                {{namePascalCase}}Command {{nameCamelCase}}Commad = new {{namePascalCase}}Command();
                /* Logic */
                {{#correlationGetSet ../event .}}
                    {{source.name}}
                {{/correlationGetSet}}
            
                {{aggregate.nameCamelCase}}Service.{{nameCamelCase}}({{nameCamelCase}}Commad);
            {{/if}}
        }
    {{else}}  
       {{#command.outgoingRelations}}
            {{#ifEquals source.aggregate.elementView.id target.aggregate.elementView.id}}
 
            {{../command.aggregate.nameCamelCase}}Repository
            .findById(
                // implement: Set the {{../command.aggregate.nameCamelCase}} Id from one of {{event.nameCamelCase}} event's corresponding property
                {{#correlationKey ../event}}
                    event.get{{namePascalCase}}()
                {{/correlationKey}}
            )
            .ifPresent({{../command.aggregate.nameCamelCase}} -> {
                {{../command.namePascalCase}}Command {{../command.nameCamelCase}}Command = new {{../command.namePascalCase}}Command();
                /* Logic */
                {{#correlationGetSet ../command ../event}}{{/correlationGetSet}}
                
                {{../command.aggregate.nameCamelCase}}.{{../command.nameCamelCase}}({{../command.nameCamelCase}}Command);
            });
            {{else}}
            {{command.namePascalCase}}Command {{../command.nameCamelCase}}Command = new {{../command.namePascalCase}}Command();
         
            /* Logic */ 333
            {{#correlationGetSet ../event ../command}}
                {{source.name}}
            {{/correlationGetSet}}
        
            {{event.aggregate.nameCamelCase}}.{{command.nameCamelCase}}({{../command.nameCamelCase}}Command);
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
    let str = '';
    let obj = {
        source: null;
        target: null;
    };
    
    if(setter && setter.fieldDescriptors){
        let srcObj = setter.fieldDescriptors.find(x=> x.isCorrelationKey);
        obj.source = srcObj
        let tarObj = null;
        let tar = '';
        
         if(getter && getter.fieldDescriptors){
            tarObj = getter.fieldDescriptors.find(x => x.isCorrelationKey);
             obj.target = tarObj;
            tar = tarObj ? `${getter.nameCamelCase}.get${tarObj.namePascalCase}()` : '';
        }
        
        if(srcObj){
            str = `${setter.nameCamelCase}.set${srcObj.namePascalCase}(${tar});\n`;
        }
    }
    return options.fn(obj);
});
// window.$HandleBars.registerHelper('correlationGetSet', function (setter, getter) {
//     let str = '';
    
//     if(setter && setter.fieldDescriptors){
//         let srcObj = setter.fieldDescriptors.find(x=> x.isCorrelationKey);
//         let tarObj = null;
//         let tar = '';
        
//          if(getter && getter.fieldDescriptors){
//             tarObj = getter.fieldDescriptors.find(x => x.isCorrelationKey);
//             tar = tarObj ? `event.get${tarObj.namePascalCase}()` : '';
//         }
        
//         if(srcObj){
//             str = `${getter.nameCamelCase}Command.set${srcObj.namePascalCase}(${tar});\n`;
//         }
//     }
//     return str
// });

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
        str = str + `@Autowired\n`;
        str = str +`${agg.namePascalCase}Service ${agg.nameCamelCase}Service;\n`
    })
    return str;
});

window.$HandleBars.registerHelper('except', function (fieldDescriptors) {
    return (fieldDescriptors && fieldDescriptors.length == 0);
});
</function>
