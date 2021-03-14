package app.models;

import app.entities.Menu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class SellUnit {

	private Menu menu;
	private double price;
	private int quantity;
}
