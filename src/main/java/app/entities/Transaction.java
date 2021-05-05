package app.entities;

import java.time.LocalDateTime;

import org.json.JSONObject;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mongodb.lang.Nullable;

import app.constants.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
@Document(collection = "Transaction")
public class Transaction {

	@Id
	private String id;
	private String rOrderId;
	
	private String razorPayKey;
	private String razorPayPaymentId;
	private String razorPaySignature;
	private String razorPayMethod;
	private String razorPayCardId;
//	@JsonIgnoreProperties
//	@Nullable
//	private JSONObject razorpayPayment;
	
	private double amt;
	
	private TransactionType type;
	private String paidByPhone; 
	private String paidByEmail; 
	private String paidTo; 
	private String currency;
	private String gst;
	private String pan;
	
	@Builder.Default
	private boolean success = false;
	
	@CreatedDate
	private LocalDateTime createdOn;
	@LastModifiedDate
	private LocalDateTime updatedOn;
	
	private LocalDateTime paymentSuccessOn;
	
}
