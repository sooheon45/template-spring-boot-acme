package labshoppubsub.domain;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import labshoppubsub.OrderApplication;
import labshoppubsub.domain.OrderPlaced;
import lombok.Data;
import org.springframework.context.ApplicationContext;

@Entity
@Table(name = "Order_table")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String productId;

    private Integer qty;

    private String customerId;

    private Double amount;

    @PostPersist
    public void onPostPersist() {
        /** TODO: Get request to Inventory
        labshoppubsub.external.GetStockQuery getStockQuery = new labshoppubsub.external.GetStockQuery();

        labshoppubsub.external.InventoryService inventoryService = applicationContext().getBean(labshoppubsub.external.InventoryService.class);


        labshoppubsub.external.Inventory inventory = 
            inventoryService.getStock( {TODO: please put the id} );

        */

        OrderPlaced orderPlaced = new OrderPlaced(this);
        orderPlaced.publishAfterCommit();
        /** TODO:  REST API Call to Inventory
        labshoppubsub.external.UpdateStockCommand updateStockCommand = new labshoppubsub.external.UpdateStockCommand();
        
        applicationContext().getBean(labshoppubsub.external.InventoryService.class)
           .updateStock({TODO: please put the id}, updateStockCommand);
        */

    }

    public static OrderRepository repository() {
        OrderRepository orderRepository = applicationContext()
            .getBean(OrderRepository.class);
        return orderRepository;
    }

    public static ApplicationContext applicationContext() {
        return OrderApplication.applicationContext;
    }
}
