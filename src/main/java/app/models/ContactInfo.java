package app.models;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ContactInfo {

	private int stdcode;
	private long phone1;
	private long phone2;
}
