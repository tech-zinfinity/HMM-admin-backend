package app.utilities;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OTPUtility {

	 private static final Integer EXPIRE_MINS = 5;
	 private LoadingCache<String, Integer> otpCache;
	 
	 public OTPUtility(){
	 super();
	 otpCache = CacheBuilder.newBuilder().
	     expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
	      public Integer load(String key) {
	             return 0;
	       }
	   });
	 }
	 
	 public boolean verifyOTP(String key, int reqotp) {
		 log.info("Verify OTP called by", key);
		 try {
			 if(getOtp(key) == reqotp) {
				 return true;
			 }else {
				 return false;
			 }
		 }catch (Exception e) {
			return false;
		}
	 }
	 
	 public int generateOTP(String key){
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		otpCache.put(key, otp);
		return otp;
	 }
	 
	 //This method is used to return the OTP number against Key->Key values is username
	 public int getOtp(String key){ 
		try{
		 return otpCache.get(key); 
		}catch (Exception e){
		 return 0; 
		}
	 }
	 
	//This method is used to clear the OTP cached already
	 public void clearOTP(String key){ 
		 otpCache.invalidate(key);
	 }
}
