forEach: Relation
fileName: {{target.namePascalCase}}Service_.java
path: {{source.boundedContext.name}}/{{options.packagePath}}/external
except: {{contexts.except}}
---

package {{options.package}}.external;

{{#if value.fallback}}
//<<< Resilency / Fallback
@FeignClient(name = "{{target.boundedContext.name}}", url = "{{apiVariable target.boundedContext.name}}", fallback = {{target.namePascalCase}}ServiceImpl.class)
//>>> Resilency / Fallback
{{else}}
@FeignClient(name = "{{target.boundedContext.name}}", url = "{{apiVariable target.boundedContext.name}}")
{{/if}}
 
{{#ifContains "$.target._type" "View"}}
{{#ifEquals target.dataProjection "query-for-aggregate"}}
public interface {{target.aggregate.namePascalCase}}Service {
    @GetMapping(path="/{{target.aggregate.namePlural}}/{{url target.name}}")
    public {{target.aggregate.namePascalCase}} {{camelCase target.name}}({{pascalCase target.name}} query);
{{/ifEquals}}
{{/ifContains}}
}



<function>
 
this.contexts.except = !((this.source._type.endsWith("Command") || this.source._type.endsWith("Policy")) && (this.target._type.endsWith("View") || this.target._type.endsWith("Aggregate")))
 
if(!this.contexts.except){
 
}
 
</function>