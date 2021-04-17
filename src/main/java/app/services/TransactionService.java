package app.services;

import java.time.LocalDate;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.threeten.bp.LocalDateTime;

import com.razorpay.Order;
import com.razorpay.Payment;
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
	
	private Mono<Order> createROrder(double amount, String currency, String invoiceId) throws RazorpayException {
		return Mono.fromCallable(() ->{
			JSONObject options = new JSONObject();
			options.put("amount", amount);
			options.put("currency", currency);
			options.put("receipt", invoiceId);
			options.put("payment_capture", false);
			return rClient.Orders.create(options);
		});
	}
	
	public Mono<Transaction> startPayment(TransactionType type, String to, String phone, String email, String currency, double amt) throws RazorpayException {
		return Mono.create(sink ->{
			try {
				this.createROrder(amt, currency, String.valueOf(LocalDate.now().getDayOfYear())+"-"+type+"-"+phone).subscribe(od ->{
					var pay = Transaction.builder()
							.type(type)
							.paidTo(to)
							.paidByPhone(phone)
							.paidByEmail(email)
							.currency(currency)
							.rOrderId(od.get("id"))
							.razorPayKey(razorpaycomponent.getApikey())
							.amt(amt)
							.build();
					trsansactionRepo.insert(pay)
					.switchIfEmpty(Mono.fromRunnable( ()->{
						sink.error(new EntityNotFoundException());
					}))
					.subscribe(data ->{
						sink.success(data);
					}, err->{
						sink.error(err);
					});
				}, err->{
					sink.error(err);
				});

			} catch (RazorpayException e) {
				e.printStackTrace();
				sink.error(e);
			}
		});
		
	}
	
	public Mono<Boolean> revertPayment(String trId) {
		return Mono.fromCallable(() ->{
			boolean[] bool = {true};
			try {
				this.trsansactionRepo.deleteById(trId).subscribe(e ->{
					
				}, err->{
					bool[0] = false;
				});
			}catch (Exception e) {
				bool[0]=false;
			}
			return bool[0];
		});
		
	}
	
	public Mono<Transaction> capturePayment(Transaction t) {
		return Mono.create(sink ->{
			try {
				captureRPayment(t).subscribe(p ->{
					t.setRazorPayMethod(p.get("method"));
					t.setRazorPayCardId(p.get("card_id"));
					t.setRazorpayPayment(p.toString());
					t.setSuccess(true);
					t.setPaymentSuccessOn(java.time.LocalDateTime.now());
					
					trsansactionRepo.save(t).subscribe(tr ->{
						sink.success(t);
					}, err->{
						sink.error(err);
					});
				}, err->{
					sink.error(err);
				});
			} catch (RazorpayException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		});
	}
	
	private Mono<Payment> captureRPayment(Transaction t) throws RazorpayException {
		return Mono.fromCallable(() ->{
			JSONObject options = new JSONObject();
			options.put("amount", t.getAmt());
			options.put("currency", t.getCurrency());
			Payment p = rClient.Payments.capture(t.getRazorPayPaymentId(), options);
			return p;
		});
	}

	
}
