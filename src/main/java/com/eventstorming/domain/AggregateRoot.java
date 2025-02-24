forEach: Aggregate
representativeFor: Aggregate
fileName: {{namePascalCase}}.java
path: {{boundedContext.name}}/{{{options.packagePath}}}/domain
---
package {{options.package}}.domain;

{{#lifeCycles}}
{{#events}}
import {{../../options.package}}.domain.{{namePascalCase}};
{{/events}}
{{/lifeCycles}}
import {{options.package}}.{{boundedContext.namePascalCase}}Application;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;
import org.springframework.context.ApplicationContext;

@Entity
@Table(name="{{namePascalCase}}_table")
@Data
{{#setDiscriminator aggregateRoot.entities.relations nameCamelCase}}{{/setDiscriminator}}
//<<< DDD / Aggregate Root
public class {{namePascalCase}} {{#checkExtends aggregateRoot.entities.relations namePascalCase}}{{/checkExtends}} {


    {{#aggregateRoot.fieldDescriptors}}
    {{^isVO}}{{#isKey}}
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    {{/isKey}}{{/isVO}}{{#isLob}}@Lob{{/isLob}}
    {{#if (isPrimitive className)}}{{#isList}}{{/isList}}{{/if}}
    {{#checkRelations ../aggregateRoot.entities.relations className isVO referenceClass}}{{/checkRelations}}
    {{#checkAttribute ../aggregateRoot.entities.relations ../name className isVO}}{{/checkAttribute}}
    private {{{className}}} {{nameCamelCase}};{{/aggregateRoot.fieldDescriptors}}

{{#contexts.eventsPerLifecycle}}
    @{{annotation}}
    public void on{{annotation}}(){
    {{#events}}
       
    {{#incoming "Command" this }}
        {{#outgoing "ReadModel" this}}
        /** TODO: Get request to {{aggregate.namePascalCase}}
        {{@root.options.package}}.external.{{namePascalCase}}Query {{nameCamelCase}}Query = new {{@root.options.package}}.external.{{namePascalCase}}Query();
        {{@root.options.package}}.external.{{aggregate.namePascalCase}}Service {{aggregate.nameCamelCase}}Service = applicationContext().getBean({{@root.options.package}}.external.{{aggregate.namePascalCase}}Service.class);
        {{#queryOption.multipleResult}}
        List<{{@root.options.package}}.external.{{aggregate.namePascalCase}}> {{boundedContext.nameCamelCase}}List = 
            {{aggregate.nameCamelCase}}Service.{{nameCamelCase}}({{nameCamelCase}}Query);
        {{else}}
        {{@root.options.package}}.external.{{aggregate.namePascalCase}} {{boundedContext.nameCamelCase}} = 
            {{aggregate.nameCamelCase}}Service.{{nameCamelCase}}( {TODO: please put the id} );
        {{/queryOption.multipleResult}}
        **/
       {{/outgoing}}
    {{/incoming}}

        {{namePascalCase}} {{nameCamelCase}} = new {{namePascalCase}}(this);
        {{nameCamelCase}}.publishAfterCommit();


    {{#outgoing "Command" this}}
        /** TODO:  REST API Call to {{aggregate.namePascalCase}}
        {{@root.options.package}}.external.{{namePascalCase}}Command {{nameCamelCase}}Command = new {{@root.options.package}}.external.{{namePascalCase}}Command();
        
        // TODO: fill the command properties to invoke below
        
        applicationContext().getBean({{@root.options.package}}.external.{{aggregate.namePascalCase}}Service.class)
           .{{nameCamelCase}}({TODO: please put the id}, {{nameCamelCase}}Command);
        **/
    {{/outgoing}}
    {{/events}}
    
    }
{{/contexts.eventsPerLifecycle}}



    public static {{namePascalCase}}Repository repository(){
        {{namePascalCase}}Repository {{nameCamelCase}}Repository = applicationContext().getBean({{namePascalCase}}Repository.class);
        return {{nameCamelCase}}Repository;
    }

    public static ApplicationContext applicationContext(){        
        return {{boundedContext.namePascalCase}}Application.applicationContext;
    }

{{#aggregateRoot.operations}}
    {{#setOperations ../commands name}}
    {{#isOverride}}
    @Override
    {{/isOverride}}
    {{^isRootMethod}}
    public {{returnType}} {{name}}(){
        //
    }
    {{/isRootMethod}}
    {{/setOperations}}
{{/aggregateRoot.operations}}


    {{#commands}}
    {{^isRestRepository}}
//<<< Clean Arch / Port Method
    public void {{nameCamelCase}}({{namePascalCase}}Command {{nameCamelCase}}Command){
        // implement the business logics here:

        {{#triggerByCommand}}
        {{eventValue.namePascalCase}} {{eventValue.nameCamelCase}} = new {{eventValue.namePascalCase}}(this);
        {{#correlationGetSet .. eventValue}} 
        {{../eventValue.nameCamelCase}}.set{{target.namePascalCase}}({{../../nameCamelCase}}Command.get{{source.namePascalCase}}());
        {{/correlationGetSet}}
        /** Logic **/
        
        {{eventValue.nameCamelCase}}.publishAfterCommit();

        {{#relationCommandInfo}}
        {{#commandValue}}
        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        {{../../../../options.package}}.external.{{aggregate.namePascalCase}} {{aggregate.nameCamelCase}} = new {{../../../../options.package}}.external.{{aggregate.namePascalCase}}();
        // mappings goes here
        
        {{../boundedContext.namePascalCase}}Application.applicationContext.getBean({{../../../../options.package}}.external.{{aggregate.namePascalCase}}Service.class)
            .{{nameCamelCase}}({{aggregate.nameCamelCase}});

        {{/commandValue}}
        {{/relationCommandInfo}}
        {{/triggerByCommand}}
    }
//>>> Clean Arch / Port Method
    {{/isRestRepository}}
    {{/commands}}

    {{#policyList}}
    {{#relationEventInfo}}
//<<< Clean Arch / Port Method
    public static void {{../nameCamelCase}}({{eventValue.namePascalCase}} {{eventValue.nameCamelCase}}){

        /** Example 1:  new item 
        {{../../namePascalCase}} {{../../nameCamelCase}} = new {{../../namePascalCase}}();
        repository().save({{../../nameCamelCase}});
        {{#../relationExampleEventInfo}}
        {{eventValue.namePascalCase}} {{eventValue.nameCamelCase}} = new {{eventValue.namePascalCase}}({{../../../nameCamelCase}});
        {{eventValue.nameCamelCase}}.publishAfterCommit();
        {{/../relationExampleEventInfo}}
        **/

        /** Example 2:  finding and process
        
        repository().findById({{eventValue.nameCamelCase}}.get???()).ifPresent({{../../nameCamelCase}}->{
            
            {{../../nameCamelCase}} // do something
            repository().save({{../../nameCamelCase}});
            {{#../relationExampleEventInfo}}
            {{eventValue.namePascalCase}} {{eventValue.nameCamelCase}} = new {{eventValue.namePascalCase}}({{../../../nameCamelCase}});
            {{eventValue.nameCamelCase}}.publishAfterCommit();
            {{/../relationExampleEventInfo}}
         });
        **/

        
    }
//>>> Clean Arch / Port Method
    {{/relationEventInfo}}
    {{/policyList}}


}
//>>> DDD / Aggregate Root

<function>

var eventsPerLifecycle = []

this.events.forEach(event=>{
    if(event.trigger){
        var theLifecycle = eventsPerLifecycle.find(l => l.annotation === event.trigger)
        if(!theLifecycle){
            theLifecycle = {annotation: event.trigger.startsWith("@") ? event.trigger.split("@")[1]: event.trigger, events: []};
            eventsPerLifecycle.push(theLifecycle)

        }
        
        theLifecycle.events.push(event)
    }
})

this.contexts.eventsPerLifecycle = eventsPerLifecycle


window.$HandleBars.registerHelper('checkDateType', function (fieldDescriptors) {
    for(var i = 0; i < fieldDescriptors.length; i ++ ){
        if(fieldDescriptors[i] && fieldDescriptors[i].className == 'Date'){
        return "import java.util.Date; \n"
        }
    }
});

window.$HandleBars.registerHelper('checkBigDecimal', function (fieldDescriptors) {
    for(var i = 0; i < fieldDescriptors.length; i ++ ){
        if(fieldDescriptors[i] && fieldDescriptors[i].className.includes('BigDecimal')){
            return "import java.math.BigDecimal;";
        }
    }
});

window.$HandleBars.registerHelper('checkAttribute', function (relations, source, target, isVO) {
   try {
       if(typeof relations === "undefined"){
        return;
        }

        if(!isVO){
            return;
        }

        var sourceObj = [];
        var targetObj = [];
        var sourceTmp = {};
        var targetName = null;
        for(var i = 0 ; i<relations.length; i++){
            if(relations[i] != null){
                if(relations[i].sourceElement.name == source){
                    sourceTmp = relations[i].sourceElement;
                    sourceObj = relations[i].sourceElement.fieldDescriptors;
                }
                if(relations[i].targetElement.name == target){
                    targetObj = relations[i].targetElement.fieldDescriptors;
                    targetName = relations[i].targetElement.nameCamelCase;
                }
            }
        }

        var samePascal = [];
        var sameCamel = [];
        for(var i = 0; i<sourceObj.length; i++){
            for(var j =0; j<targetObj.length; j++){
                if(sourceObj[i].name == targetObj[j].name){
                    samePascal.push(sourceObj[i].namePascalCase);
                    sameCamel.push(sourceObj[i].nameCamelCase);
                }
            }
        }

        var attributeOverrides = "";
        for(var i =0; i<samePascal.length; i++){
            var camel = sameCamel[i];
            var pascal = samePascal[i];
            var overrides = `@AttributeOverride(name="${camel}", column= @Column(name="${targetName}${pascal}", nullable=true))\n`;
            attributeOverrides += overrides;
        }

        return attributeOverrides;
    } catch (e) {
       console.log(e)
    }


});

window.$HandleBars.registerHelper('isPrimitive', function (className) {
    if(className.includes("String") || className.includes("Integer") || className.includes("Long") || className.includes("Double") || className.includes("Float")
            || className.includes("Boolean") || className.includes("Date")){
        return true;
    } else {
        return false;
    }
});

window.$HandleBars.registerHelper('checkRelations', function (relations, className, isVO, referenceClass) {
    try {
        if(typeof relations === "undefined") {
            return 
        } else {
            // primitive type
            if(className.includes("String") || className.includes("Integer") || className.includes("Long") || className.includes("Double") || className.includes("Float")
                    || className.includes("Boolean") || className.includes("Date")) {
                if(className.includes("List")) {
                    return "@ElementCollection"
                }
            } else {
                // ValueObject
                if(isVO) {
                    if(className.includes("List")) {
                        return "@ElementCollection"
                    } else {
                        return "@Embedded"
                    }
                } else {
                    for(var i = 0; i < relations.length; i ++ ) {
                        if(relations[i] != null) {
                            if(className.includes(relations[i].targetElement.name) && !relations[i].relationType.includes("Generalization")) {
                                // Enumeration
                                if(relations[i].targetElement._type.endsWith('enum') || relations[i].targetElement._type.endsWith('Exception')) {
                                    return
                                }
                                // complex type
                                if(relations[i].sourceMultiplicity == "1" &&
                                        (relations[i].targetMultiplicity == "1..n" || relations[i].targetMultiplicity == "0..n") || className.includes("List")
                                ) {
                                    return "@OneToMany"

                                } else if((relations[i].sourceMultiplicity == "1..n" || relations[i].sourceMultiplicity == "0..n") && relations[i].targetMultiplicity == "1"){
                                    return "@ManyToOne"
                                
                                } else if(relations[i].sourceMultiplicity == "1" && relations[i].targetMultiplicity == "1"){
                                    return "@OneToOne"
                                
                                } else if((relations[i].sourceMultiplicity == "1..n" || relations[i].sourceMultiplicity == "0..n") &&
                                        (relations[i].targetMultiplicity == "1..n" || relations[i].targetMultiplicity == "0..n") || className.includes("List")
                                ) {
                                    return "@ManyToMany"
                                }
                            }
                        }
                    }
                    if(referenceClass) {
                        return "@OneToOne"
                    }
                }
            }
        }
    } catch (e) {
        console.log(e)
    }
});

window.$HandleBars.registerHelper('checkExtends', function (relations, name) {
    try {
        if(typeof relations === "undefined" || name === "undefined"){
            return;
        } else {
            for(var i = 0; i < relations.length; i ++ ){
                if(relations[i] != null){
                    if(relations[i].sourceElement.name == name && relations[i].relationType.includes("Generalization")){
                        var text = "extends " + relations[i].targetElement.name
                        return text
                    }
                }
            }
        }
    } catch(e) {
        console.log(e)
    }
});

window.$HandleBars.registerHelper('setDiscriminator', function (relations, name) {
    try {
        if (typeof relations == "undefined") {
            return 
        } else {
            for (var i = 0; i < relations.length; i ++ ) {
                if (relations[i] != null) {
                    var text = ''
                    if (relations[i].targetElement != "undefined") {
                        if(relations[i].targetElement.name.toLowerCase() == name && relations[i].relationType.includes("Generalization")) {
                            text = '@DiscriminatorColumn(\n' + 
                                '    discriminatorType = DiscriminatorType.STRING,\n' +
                                '    name = "' + name + '_type",\n' +
                                '    columnDefinition = "CHAR(5)"\n' +
                                ')'
                            return text
                        }
                    } else {
                        if(relations[i].toName.toLowerCase() == name && relations[i].relationType.includes("Generalization")) {
                            text = '@DiscriminatorColumn(\n' + 
                                '    discriminatorType = DiscriminatorType.STRING,\n' +
                                '    name = "' + name + '_type",\n' +
                                '    columnDefinition = "CHAR(5)"\n' +
                                ')'
                            return text
                        }
                    }
                    if (relations[i].sourceElement != "undefined") {
                        if (relations[i].sourceElement.name.toLowerCase() == name && relations[i].relationType.includes("Generalization")) {
                            return '@DiscriminatorValue("' + name + '")'
                        }
                    } else {
                        if (relations[i].fromName.toLowerCase() == name && relations[i].relationType.includes("Generalization")) {
                            return '@DiscriminatorValue("' + name + '")'
                        }
                    }
                }
            }
        }
    } catch(e) {
        console.log(e)
    }
});

window.$HandleBars.registerHelper('setOperations', function (commands, name, options) {
    try {
        if(commands == "undefined") {
            return options.fn(this);
        }
        var isCmd = false;
        for (var i = 0; i < commands.length; i ++ ) {
            if(commands[i] != null) {
                if (commands[i].name == name && commands[i].isRestRepository != true) {
                    isCmd = true
                }
            }
        }
        if(isCmd) {
            return options.inverse(this);
        } else {
            return options.fn(this);
        }
    } catch(e) {
        console.log(e)
    }
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

window.$HandleBars.registerHelper('has', function (members) {
    try {
        return (members.length > 0);
    } catch(e) {
        console.log(e)
    }
});


</function>

