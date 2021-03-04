package app.entities;

import lombok.Builder;
import lombok.Data;

@Builder @Data
public class Table {

	private String hotelId;
	private String tableId;
	private String tableNo;
	private String qrLink;
	private String[] links;
}
