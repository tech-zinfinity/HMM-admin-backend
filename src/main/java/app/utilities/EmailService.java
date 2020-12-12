package app.utilities;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import app.constants.MailContants;
import reactor.core.publisher.Mono;

@Service
public class EmailService {
	
	@Autowired private JavaMailSender emailsender;
	
	public Mono<Boolean> sendSimpleMsg(String[] to,String subject,String text) throws MailParseException, 
	MailAuthenticationException, MailPreparationException, MailSendException{
		return Mono.fromCallable(() ->{
			SimpleMailMessage message = new SimpleMailMessage();
			try {
				message.setFrom(MailContants.mail_from_earthenflavours_info);
				message.setTo(to);
				message.setSubject(subject);
				message.setText(text);
				emailsender.send(message);
				return true;
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}	
		});
	}
	
	public Mono<Boolean> sendOTP(String to,String subject,String text) throws MailParseException, 
	MailAuthenticationException, MailPreparationException, MailSendException{
		return Mono.fromCallable(() ->{
			JavaMailSenderImpl sender = new JavaMailSenderImpl();
			sender.setHost("cp-wc09.lon01.ds.network");
			sender.setPort(587);
			sender.setUsername("info@earthenflavours.com");
			sender.setPassword("info@987");
			MimeMessage message = sender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			try {
				helper.setFrom(MailContants.mail_from_earthenflavours_info);
				helper.setTo(to);
				helper.setSubject(subject);
				helper.setText("<html> <head> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"> <style> * "
						+ "{ box-sizing: border-box; } .menu { width: 20%; } .menuitem { padding: 8px; margin-top: 7px;"
						+ " border-bottom: 1px solid #f1f1f1; } .main { width: 60%; padding: 0 20px; overflow: hidden; } "
						+ ".right { background-color: white; color: black; width: 20%; padding: 10px 15px; margin-top: 7px; } "
						+ "@media only screen and (max-width:800px) { /* For tablets: */ .main { width: 80%; padding: 0; } .right { width: 100%; } } "
						+ "@media only screen and (max-width:500px) { /* For mobile phones: */ .menu, .main, .right { width: 100%; } } </style> </head> "
						+ "<body style=\"font-family:Verdana;\"> <div style=\"display: flex; justify-content: flex-start; flex-direction: column; "
						+ "background-color: white; color: black;padding:15px;\"> <div> <img src='cid:EFLogo' style=\"height: 100px; width: 180px;\"> </div> "
						+ "<div style=\"display: flex; justify-content: flex-start; flex-direction: column;\"> <!-- <h1>Earthen Flavours</h1> --> "
						+ "</div> </div> <div style=\"overflow:auto\"> <div class=\"right\" > <p>Dear Customer<br><br> "
						+ "The Verification Code For Your Earthen Flavour's Order is : <strong>"+text
						+"</strong><br><br> This OTP is Only Valid upto <strong> 5 minutes</strong></p><br><br> Thanks,"
						+ "<br><br> Regards<br><br> </div> </div> <div style=\"background-color: white; color: black;"
						+ "text-align:center;padding:10px;margin-top:7px;"
						+ "font-size:12px;\"> "
						+ "earthenflavours.com<br><br> Copyright © 2020, All Right Reserved </div> </body> </html>", true);
				
//				FileSystemResource res = new FileSystemResource(ResourceUtils.getFile("classpath:images/EF.png"));
//				helper.addInline("EFLogo", res);
				sender.send(message);
				return true;
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}	
		});
	}
	
	public Mono<Boolean> sendApprovalMsg(String[] to,String subject,String text) throws MailParseException, 
	MailAuthenticationException, MailPreparationException, MailSendException{
		return Mono.fromCallable(() ->{
			SimpleMailMessage message = new SimpleMailMessage();
			try {
				message.setFrom(MailContants.mail_from_earthenflavours_info);
				message.setTo(to);
				message.setSubject(subject);
				message.setText(text);
				emailsender.send(message);
				return true;
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}	
		});
	}
	
}
