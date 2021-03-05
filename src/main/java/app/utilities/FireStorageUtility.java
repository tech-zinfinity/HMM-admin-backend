package app.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Bucket.BlobWriteOption;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;

import app.constants.FireCreds;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class FireStorageUtility {

	@Autowired public FireCreds crds;
    private StorageOptions storageOptions;
    private FirebaseOptions foptions;
    private String bucketName;
    private String projectId;


	    @PostConstruct
	    private void initializeFirebase() throws Exception {
	        bucketName = "dev";
	        projectId = crds.getProject_id();

	        FireCreds creds = new FireCreds();
	        ObjectMapper mapper = new ObjectMapper();
	        this.storageOptions = StorageOptions.newBuilder()
	                .setProjectId(projectId)
	                .setCredentials(GoogleCredentials.fromStream(IOUtils.toInputStream(mapper.writeValueAsString(creds), "UTF-8"))).build();
	        this.foptions = new FirebaseOptions.Builder()
	        		.setCredentials(GoogleCredentials.fromStream(IOUtils.toInputStream(mapper.writeValueAsString(creds), "UTF-8")))
	        		.setStorageBucket("hmmfire-prod.appspot.com")
	        		.build();
//	        if(!Objects.nonNull(FirebaseApp.getInstance())) {
//		        FirebaseApp.initializeApp(this.foptions);
//	        }
	        
	        log.info("FireBase project got connected !", storageOptions.toString());
	    }

	    public String[] uploadFile(MultipartFile multipartFile) throws IOException {
	        File file = convertMultiPartToFile(multipartFile);
	        Path filePath = file.toPath();
	        String objectName = generateFileName(multipartFile);
//	        FirebaseApp.initializeApp(this.storageOptions);
	        Bucket bucket = StorageClient.getInstance().bucket();
	        Blob blob = bucket.create(objectName+".png", multipartFile.getInputStream());
	        
//	        Storage storage = storageOptions.getService();
//	        System.out.println("storrag"+storage.toString());
//	        BlobId blobId = BlobId.of(bucketName, objectName);
//	        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
//	        Blob blob = storage.create(blobInfo, Files.readAllBytes(filePath));

	        log.info("File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);
	        return new String[]{"fileUrl", objectName};
	    }
	    
	    public Mono<String[]> uploadFile(InputStream file, String id, Path path) {
	    	return Mono.fromCallable(()->{
		        Bucket bucket = StorageClient.getInstance().bucket();
		        System.out.println("file available ?"+file.available());
		        System.out.println("file available ?"+ file.read());
		        System.out.println(file);
		        Blob blob = bucket.create(path.getFileName().toString(), Files.readAllBytes(path), "image/png");
		        log.info("File " + " uploaded to bucket " + bucketName + " as " + id);
		        return new String[]{id, blob.getName(), blob.getBlobId().toString(), blob.getSelfLink()};
	    	});
	    }

//
//	    @Override
//	    public ResponseEntity<Object> downloadFile(String fileName, HttpServletRequest request) throws Exception {
//	        Storage storage = storageOptions.getService();
//
//	        Blob blob = storage.get(BlobId.of(bucketName, fileName));
//	        ReadChannel reader = blob.reader();
//	        InputStream inputStream = Channels.newInputStream(reader);
//
//	        byte[] content = null;
//	        log.info("File downloaded successfully.");
//
//	        content = IOUtils.toByteArray(inputStream);
//
//	        final ByteArrayResource byteArrayResource = new ByteArrayResource(content);
//
//	        return ResponseEntity
//	                .ok()
//	                .contentLength(content.length)
//	                .header("Content-type", "application/octet-stream")
//	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
//	                .body(byteArrayResource);
//
//	    }


	    private File convertMultiPartToFile(MultipartFile file) throws IOException {
	        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
	        FileOutputStream fos = new FileOutputStream(convertedFile);
	        fos.write(file.getBytes());
	        fos.close();
	        return convertedFile;
	    }

	    private String generateFileName(MultipartFile multiPart) {
	        return new Date().getTime() + "-" + Objects.requireNonNull(multiPart.getOriginalFilename()).replace(" ", "_");
	    }

}
