package app.payment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import org.springframework.beans.factory.annotation.Value;

import lombok.Getter;

@Component
@PropertySource("classpath:payment-constant.properties")
@Getter 
@Configuration
public class RazorPayComponent {

	@Value("${apikey}")
	private String apikey;
	
	@Value("${apisecret}")
	private String apisecret;
	
	@Bean
	public RazorpayClient creatPayClient() {
		RazorpayClient client = null;
		try {
			client =  new RazorpayClient(apikey, apisecret);
		} catch (RazorpayException e) {
			e.printStackTrace();
		}
		return client;
	}
}
