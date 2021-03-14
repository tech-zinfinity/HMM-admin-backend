package app.entities;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import app.models.SellUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
@Document(collection = "Order")
public class Order implements Comparator<Order> {

	private String id;
	
	@CreatedDate
	private LocalDateTime createdOn;
	@LastModifiedDate
	private LocalDateTime updatedOn;
	
	private String custId;
	private double totalPrice;
	private String status;
	private boolean active;
	private String stage;
	private Transaction transaction;
	private List<SellUnit> items;
	
	@Override
	public int compare(Order o1, Order o2) {
		return o1.getCreatedOn().compareTo(o2.getCreatedOn());
	}
	
}
