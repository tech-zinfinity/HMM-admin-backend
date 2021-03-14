package app.utilities;

import java.sql.Timestamp;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class IDGeneratorWithTimeStamp {

	public String generateID() {
		return String.valueOf(new Timestamp(System.currentTimeMillis()).getTime()) + '-' + UUID.randomUUID().toString();
	}
}
