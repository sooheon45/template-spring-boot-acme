forEach: Relation
fileName: {{target.namePascalCase}}Service_.java
path: {{source.boundedContext.name}}/{{options.packagePath}}/external
except: {{contexts.except}}
---

package {{options.package}}

{{#if fallback}}
//<<< Resilency / Fallback
@FeignClient(name = "{{target.boundedContext.name}}", url = "{{apiVariable target.boundedContext.name}}", fallback = {{target.namePascalCase}}ServiceImpl.class)
//>>> Resilency / Fallback
{{else}}
@FeignClient(name = "{{target.boundedContext.name}}", url = "{{apiVariable target.boundedContext.name}}")
{{/if}}
public interface {{target.namePascalCase}}Service {
 
    {{#ifContains "$.target._type" "View"}}
    @RequestMapping(method= RequestMethod.GET, path="/{{target.namePlural}}/{{wrap commandValue.aggregate.keyFieldDescriptor.name}}")
    public {{commandValue.aggregate.namePascalCase}} get{{commandValue.aggregate.namePascalCase}}(@PathVariable("{{commandValue.aggregate.keyFieldDescriptor.name}}") {{commandValue.aggregate.keyFieldDescriptor.className}} {{commandValue.aggregate.keyFieldDescriptor.name}});
    {{/ifContains}}
}



<function>
this.contexts.except = !((this.source._type.endsWith("Command") || this.source._type.endsWith("Policy")) && (this.target._type.endsWith("View") || this.target._type.endsWith("Aggregate")))
 
if(!this.contexts.except){

}
 
</function>