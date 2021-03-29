package app.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class Menu {

	private String id;
	private String title;
	private String description;
	private double cost;
	private String[] picsUrls;
	private boolean veg;
	private String category;
	private boolean available;
	private boolean active;
}
