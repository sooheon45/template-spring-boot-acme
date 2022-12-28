forEach: RelationCommandInfo
fileName: {{commandValue.aggregate.namePascalCase}}Service.java
path: {{boundedContext.name}}/{{{options.packagePath}}}/external
except: {{#ifEquals method "GET"}}false{{else}}true{{/ifEquals}}
---
package {{options.package}}.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

//<<< Resilency / Circuit Breaker
{{#if boundedContext.fallback}}
//<<< Resilency / Fallback
@FeignClient(name = "{{targetBoundedContext.name}}", url = "{{apiVariable targetBoundedContext.name}}", fallback = {{targetAggregate.namePascalCase}}ServiceImpl.class)
//>>> Resilency / Fallback
{{else}}
@FeignClient(name = "{{targetBoundedContext.name}}", url = "{{apiVariable targetBoundedContext.name}}")
{{/if}}
public interface {{targetAggregate.namePascalCase}}Service {
    @RequestMapping(method= RequestMethod.GET, path="/{{targetAggregate.namePlural}}/{{wrap targetAggregate.keyFieldDescriptor.name}}")
    public {{targetAggregate.namePascalCase}} get{{targetAggregate.namePascalCase}}(@PathVariable("{{targetAggregate.keyFieldDescriptor.name}}") {{commandValue.aggregate.keyFieldDescriptor.className}} {{targetAggregate.keyFieldDescriptor.name}});
}
//>>> Resilency / Circuit Breaker

<function>

  window.$HandleBars.registerHelper('apiVariable', function (bc) {
    return '${api.url.'+bc+'}';
  })

</function>