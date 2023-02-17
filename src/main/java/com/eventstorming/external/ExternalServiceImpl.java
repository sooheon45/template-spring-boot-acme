forEach: Relation
fileName: {{target.aggregate.namePascalCase}}ServiceImpl.java
path: {{source.boundedContext.name}}/{{options.packagePath}}/external
except: {{contexts.except}}
ifDuplicated: merge
---

package {{options.package}}.external;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


{{#ifContains "$.target._type" "View"}}
{{#target}}
{{#ifEquals dataProjection "query-for-aggregate"}}
@Service
public class {{aggregate.namePascalCase}}ServiceImpl implements {{aggregate.namePascalCase}}Service {
    {{#queryOption.multipleResult}}
    public List<{{aggregate.namePascalCase}}> {{nameCamelCase}}({{namePascalCase}}Query query){
        {{aggregate.namePascalCase}} {{aggregate.nameCamelCase}} = new {{aggregate.namePascalCase}}();
        
        List<{{aggregate.namePascalCase}}> list = new ArrayList<{{aggregate.namePascalCase}}>();
        list.add({{aggregate.nameCamelCase}});

        return list;
    }
    {{else}}
    public {{aggregate.namePascalCase}} {{nameCamelCase}}({{aggregate.keyFieldDescriptor.className}} id){
        {{aggregate.namePascalCase}} {{aggregate.nameCamelCase}} = new {{aggregate.namePascalCase}}();
        
        return {{aggregate.nameCamelCase}};
    }
    {{/queryOption.multipleResult}}
{{/ifEquals}}
{{/target}}
{{/ifContains}}
}



<function> 
 
var commandToReadModelOrPolicy = ((this.source._type.endsWith("Command") || this.source._type.endsWith("Policy")) && (this.target._type.endsWith("View") || this.target._type.endsWith("Aggregate")))
 this.contexts.except = !(commandToReadModelOrPolicy && this.value.fallback)


if(!this.contexts.except){
 
}
 
</function>