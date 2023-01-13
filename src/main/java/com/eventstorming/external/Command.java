forEach: Relation
fileName: {{target.aggregate.namePascalCase}}Service.java
path: {{source.boundedContext.name}}/{{{options.packagePath}}}/external
except: {{contexts.except}}
---
package {{options.package}}.external;

import javax.persistence.*;
import java.util.List;
import java.util.Date;
import lombok.Data;

@Data
public class {{target.namePascalCase}}Command {

{{#target.fieldDescriptors}}
    {{#isKey}}
    @Id
    {{/isKey}}
    private {{{className}}} {{nameCamelCase}};
{{/target.fieldDescriptors}}
}

<function>
  this.contexts.except = !((this.source._type.endsWith("Event") || this.source._type.endsWith("Policy")) && this.target._type.endsWith("Command"))
</function>
