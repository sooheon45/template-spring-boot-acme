forEach: Relation
fileName: {{pascalCase target.name}}Query.java
path: {{source.boundedContext.name}}/{{options.packagePath}}/external
except: {{contexts.except}}
ifDuplicated: merge
---

package {{options.package}}.external;

import lombok.Data;
import java.util.Date;
{{#target}}
@Data
public class {{namePascalCase}}Query {

    {{#fieldDescriptors}}
    private {{safeTypeOf className}} {{nameCamelCase}};
    {{/fieldDescriptors}}
}
{{/target}}


<function>
 
this.contexts.except = !(
    (this.source._type.endsWith("Command") || this.source._type.endsWith("Policy")) && 
    (this.target._type.endsWith("View") && target.dataProjection=="query-for-aggregate")
)
 
if(!this.contexts.except){
 
}
 
</function>