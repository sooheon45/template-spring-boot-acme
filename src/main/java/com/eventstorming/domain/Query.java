forEach: View
representativeFor: View
fileName: {{namePascalCase}}Query.java
path: {{boundedContext.name}}/{{{options.packagePath}}}/domain
except: {{#ifEquals dataProjection "query-for-aggregate"}}false{{else}}true{{/ifEquals}}
---
package {{options.package}}.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Date;
import lombok.Data;
{{#checkBigDecimal fieldDescriptors}}{{/checkBigDecimal}}

@Data
public class {{namePascalCase}}Query {

{{#queryParameters}}
    {{className}} {{nameCamelCase}};
{{/queryParameters}}


}

<function>
window.$HandleBars.registerHelper('checkBigDecimal', function (fieldDescriptors) {
    for(var i = 0; i < fieldDescriptors.length; i ++ ){
        if(fieldDescriptors[i] && fieldDescriptors[i].className.includes('BigDecimal')){
            return "import java.math.BigDecimal;";
        }
    }
});
</function>