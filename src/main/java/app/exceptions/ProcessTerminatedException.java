package app.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor @Builder @Data
@EqualsAndHashCode(callSuper=false)
public class ProcessTerminatedException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1534957828729007624L;
	
	private String message;
	
	
}
