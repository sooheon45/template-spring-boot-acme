forEach: Relation
fileName: {{target.aggregate.namePascalCase}}Service.java
path: {{source.boundedContext.name}}/{{options.packagePath}}/external
except: {{contexts.except}}
ifDuplicated: merge
---

package {{options.package}}.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.List;


{{#if value.fallback}}
@FeignClient(name = "{{target.boundedContext.name}}", url = "{{apiVariable target.boundedContext.name}}", fallback = {{target.aggregate.namePascalCase}}ServiceImpl.class)
{{else}}
@FeignClient(name = "{{target.boundedContext.name}}", url = "{{apiVariable target.boundedContext.name}}")
{{/if}}
 
{{#ifContains "$.target._type" "View"}}
{{#ifEquals target.dataProjection "query-for-aggregate"}}
public interface {{target.aggregate.namePascalCase}}Service {
    @GetMapping(path="/{{target.aggregate.namePlural}}")
    public List<{{target.aggregate.namePascalCase}}> {{target.nameCamelCase}}({{target.namePascalCase}}Query query);
{{/ifEquals}}
{{/ifContains}}
}



<function>
 

    let isGetInvocation = ((this.source._type.endsWith("Command") || this.source._type.endsWith("Policy")) && (this.target._type.endsWith("View") || this.target._type.endsWith("Aggregate")))
    let isPostInvcation = ((this.source._type.endsWith("Event") || this.source._type.endsWith("Policy")) && this.target._type.endsWith("Command"))
    let isExternalInvocation = (this.source.boundedContext.name != this.target.boundedContext.name)

    this.contexts.except = !(isExternalInvocation && isGetInvocation)
 
if(!this.contexts.except){
 
}
 
</function>