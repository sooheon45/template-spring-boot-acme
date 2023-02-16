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
{{#ifEquals target.dataProjection "query-for-aggregate"}}
@Service
public class {{target.aggregate.namePascalCase}}ServiceImpl implements {{target.aggregate.namePascalCase}}Service {
    public List<{{target.aggregate.namePascalCase}}> {{target.nameCamelCase}}({{target.namePascalCase}}Query query){
        {{target.aggregate.namePascalCase}} {{target.aggregate.nameCamelCase}} = new {{target.aggregate.namePascalCase}}();
        
        List<{{target.aggregate.namePascalCase}}> list = new ArrayList<{{target.aggregate.namePascalCase}}>();
        list.add({{target.aggregate.nameCamelCase}});

        return list;
    }
{{/ifEquals}}
{{/ifContains}}
}



<function> 
 
this.contexts.except = !((this.source._type.endsWith("Command") || this.source._type.endsWith("Policy")) && (this.target._type.endsWith("View") || this.target._type.endsWith("Aggregate")))
 
if(!this.contexts.except){
 
}
 
</function>