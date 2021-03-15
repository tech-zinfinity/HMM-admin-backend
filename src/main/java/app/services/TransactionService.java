package app.services;

import java.time.LocalDate;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import app.constants.TransactionType;
import app.entities.Transaction;
import app.exceptions.EntityNotFoundException;
import app.payment.RazorPayComponent;
import app.repositories.TransactionRepository;
import reactor.core.publisher.Mono;

@Service
public class TransactionService {

	@Autowired private RazorPayComponent razorpaycomponent;
	@Autowired private RazorpayClient rClient;
	@Autowired private TransactionRepository trsansactionRepo;
	
	private Order createROrder(double amount, String currency, String invoiceId) throws RazorpayException {
		JSONObject options = new JSONObject();
		options.put("amount", amount);
		options.put("currency", currency);
		options.put("receipt", invoiceId);
		options.put("payment_capture", false);
		return rClient.Orders.create(options);
	}
	
	public Mono<Transaction> startPayment(TransactionType type, String to, String from, String currency, double amt) throws RazorpayException {
		return Mono.create(sink ->{
			Order od;
			try {
				od = this.createROrder(amt, currency, String.valueOf(LocalDate.now().getDayOfYear())+"-"+type+"-"+from);
				System.out.println(od);
				var pay = Transaction.builder()
						.type(type)
						.paidTo(to)
						.paidBy(from)
						.currency(currency)
						.rOrderId(od.get("id"))
						.razorPayKey(razorpaycomponent.getApikey())
						.amt(amt)
						.build();
				this.trsansactionRepo.insert(pay).switchIfEmpty(Mono.fromRunnable( ()->{
					sink.error(new EntityNotFoundException());
				}))
				.subscribe(data ->{
					sink.success(data);
				}, err->{
					sink.error(err);
				});
			} catch (RazorpayException e) {
				e.printStackTrace();
				sink.error(e);
			}
		});
		
	}
	
}
