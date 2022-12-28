forEach: BoundedContext
fileName: NOFILE
path: {{boundedContext.name}}/{{{options.packagePath}}}/external
---


{{#contexts.externalAggregates}}

---
fileName: {{aggregate.namePascalCase}}Service.java
---
public interface {{aggregate.namePascalCase}}{

  {{#invocations}}
    get{{aggregate}}
  {{/invocations}}
}

{{/contexts.externalAggregates}}

<function>

let externalInvocationsByAggregate = {};

this.aggregates?.forEach(aggregate=>{
  aggregate.commands?.forEach(command=>{
    command.outgoingReadModelRefs?.forEach(readModelRef => {
      let readModel = readModelRef.value;

      if(!externalInvocationsByAggregate[readModel.aggregate.name])
        externalInvocationsByAggregate[readModel.aggregate.name] = {
          aggregate: readModel.aggregate,
          invocations: []
        };

      externalInvocationsByAggregate[readModel.aggregate.name].invocations.push({
        target: readModel,
        targetAggregate: readModel.aggregate,
        method: "GET"
      });
    })
  })
});

this.contexts.externalAggregates = Object.values(externalInvocationsByAggregate);


</function>