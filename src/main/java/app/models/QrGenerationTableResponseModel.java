package app.models;

import lombok.Builder;
import lombok.Data;

@Builder @Data
public class QrGenerationTableResponseModel {

	private String tableId;
	private Object file;
}
