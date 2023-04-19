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
    {{event}}
//     @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='{{event.namePascalCase}}'")
//     public void whenever{{namePascalCase}}_{{../namePascalCase}}(@Payload {{namePascalCase}} {{nameCamelCase}}, 
//                                 @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment,
//                                 @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) byte[] messageKey){

//         {{namePascalCase}} event = {{nameCamelCase}};
//         System.out.println("\n\n##### listener {{../namePascalCase}} : " + {{nameCamelCase}} + "\n\n");

//         {{#../aggregateList}}
//         {{namePascalCase}}.{{../../nameCamelCase}}(event);        
//         {{/../aggregateList}}

//         {{#outgoing "Command" ..}}
//         {{#isExtendedVerb}}
//         {{namePascalCase}}Command {{nameCamelCase}}Command = new {{namePascalCase}}Command();
//         // implement:  Map command properties from event

//         {{aggregate.nameCamelCase}}Repository.findById(
//                 // implement: Set the {{aggregate.namePascalCase}} Id from one of {{../namePascalCase}} event's corresponding property
                
//             ).ifPresent({{aggregate.nameCamelCase}}->{
//              {{aggregate.nameCamelCase}}.{{nameCamelCase}}({{nameCamelCase}}Command); 
//         });
//         {{else}}
//         {{aggregate.namePascalCase}} {{aggregate.nameCamelCase}} = new {{aggregate.namePascalCase}}();
//         {{aggregate.nameCamelCase}}Repository.save({{aggregate.nameCamelCase}});
//         {{/isExtendedVerb}}
//         {{/outgoing}}


//         {{#todo ../description}}{{/todo}}

//         // Manual Offset Commit //
//         acknowledgment.acknowledge();

//     }
    {{/contexts.sagaEvents}}
    
    
    // 1. start
    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='OrderPlaced'"
    )
    public void wheneverOrderPlaced_OrderSaga(
        @Payload OrderPlaced orderPlaced,
        @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment,
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) byte[] messageKey
    ) {
        OrderPlaced event = orderPlaced;
        System.out.println(
            "\n\n##### listener OrderSaga : " + orderPlaced + "\n\n"
        );
        
        try {
            // 2
            StartDeliveryCommand startDeliveryCommand = new StartDeliveryCommand();

            /* Logic */
            startDeliveryCommand.setId(event.getId());


            deliveryService.startDelivery(startDeliveryCommand);
        } catch (Exception e){
            // 2'
            OrderCancelCommand orderCancelCommand = new OrderCancelCommand();
             /* Logic */
            orderRepository
            .findById(
                // implement: Set the Delivery Id from one of OrderPlaced event's corresponding property
                event.getId()
            )
            .ifPresent(order -> {
                order.orderCancel(orderCancelCommand);
            });

        }
        
        // Manual Offset Commit //
        acknowledgment.acknowledge();
    }
    

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
