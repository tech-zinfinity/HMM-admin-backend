package app.models;

import java.util.List;

import app.entities.Menu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class CategoryMenuModel {

	private List<Menu> menus;
	private String category;
}
