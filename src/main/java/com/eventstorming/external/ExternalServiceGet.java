forEach: Relation
fileName: {{target.aggregate.namePascalCase}}Service.java
path: {{source.boundedContext.name}}/{{options.packagePath}}/external
except: {{contexts.except}}
ifDuplicated: merge
---

package {{options.package}}.external;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;


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
    public {{target.aggregate.namePascalCase}} {{camelCase target.name}}({{pascalCase target.name}}Query query);
{{/ifEquals}}
{{/ifContains}}
}



<function>
 
this.contexts.except = !((this.source._type.endsWith("Command") || this.source._type.endsWith("Policy")) && (this.target._type.endsWith("View") || this.target._type.endsWith("Aggregate")))
 
if(!this.contexts.except){
 
}
 
</function>