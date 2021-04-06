package app.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.services.TransactionService;

@Service
public class PaymentService {

	@Autowired private TransactionService transactionservice;
	
	
}
