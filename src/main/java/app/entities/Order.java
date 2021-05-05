package app.entities;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import app.constants.OrderStages;
import app.constants.OrderStatus;
import app.models.HotelOrderModel;
import app.models.SellUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
@Document(collection = "Order")
public class Order implements Comparator<Order> {

	private String id;
	private String custId;
	private double totalPrice;
	private OrderStatus status;
	private boolean active;
	private OrderStages stage;
	private Transaction transaction;
	private List<SellUnit> items;
	
	private String hotelId;
	private String tableId;
	private String tableName;
	
	//hotel relates info
	private HotelOrderModel hotel;
	
	@CreatedDate
	private LocalDateTime createdOn;
	@LastModifiedDate
	private LocalDateTime updatedOn;
	
	@Override
	public int compare(Order o1, Order o2) {
		return o1.getCreatedOn().compareTo(o2.getCreatedOn());
	}
	
}
