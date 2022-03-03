package Jorge.MavenArtifact;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.util.Base64;
import java.util.Base64.Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.*;

public class APITestUtil {

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA384";
	CloseableHttpResponse response;
	private String responseBody; 
	private int responseStatusCode;
	int order_id=10;

	public void newOrderPostRequest(String jsonBody) throws ClientProtocolException, IOException, NoSuchAlgorithmException, InvalidKeyException {

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("https://api.gemini.com/v1/order/new");
		StringEntity jsonEntity = new StringEntity(jsonBody);
		httpPost.setEntity(jsonEntity);

		//removed apiKeySecret value for submission
		String apiKeySecret = "";
		Encoder enc = Base64.getEncoder();
//		Tried mimicking the python signature setup but repeatedly get back a 400 message saying invalid signature.  
//		byte[] encryptedPwdByteArray = enc.encode(apiKeySecret.getBytes());
//		String encryptedApiKeySecret = new String(encryptedPwdByteArray, StandardCharsets.UTF_8);

		
		//getNonce method will return a nonce value based on time that will always increase.
		String payload_nonce = getNonce();
		jsonBody = jsonBody.replace("<nonce>", payload_nonce);
		
		//Common util would have necessary methods such as getOrderId which will track the order id in a properties file and provide an incremented value when called.
		Common_Util CU = new Common_Util();
		jsonBody = jsonBody.replace("<client_order_id>", Integer.toString(CU.getOrderId()));
		
		String b64 = new String(enc.encode(jsonBody.getBytes()), StandardCharsets.UTF_8);
		String signature;
		
		SecretKeySpec signingKey = new SecretKeySpec(apiKeySecret.getBytes(), HMAC_SHA1_ALGORITHM);
	    final Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
	    mac.init(signingKey);
	    signature = bytesToHex(mac.doFinal(enc.encode(jsonBody.getBytes())));
		
		httpPost.setHeader("Content-type", "text/plain");
		httpPost.setHeader("X-GEMINI-APIKEY", "master-0S0sTuEs8v7DIPXA3iTd");
		httpPost.setHeader("X-GEMINI-PAYLOAD", b64);
		httpPost.setHeader("X-GEMINI-SIGNATURE", signature);
		httpPost.setHeader("Cache-Control", "no-cache");

		response = client.execute(httpPost);
		
		HttpEntity entity = response.getEntity();
		responseBody = EntityUtils.toString(entity, "UTF-8");
		responseStatusCode = response.getStatusLine().getStatusCode();
		
		client.close();

	}

	public void close() throws IOException {
		response.close();
	}
	
	public String getResponseString() throws ParseException, IOException {
		return responseBody;
	}
	
	public int getResponseCode() {
		return responseStatusCode;
	}
	
	private static String bytesToHex(final byte[] hash) {
	    final StringBuffer hexString = new StringBuffer();
	    for (int i = 0; i < hash.length; i++) {
	        final String hex = Integer.toHexString(0xff & hash[i]);
	        if (hex.length() == 1) {
	            hexString.append('0');
	        }
	        hexString.append(hex);
	    }
	    return hexString.toString();
	}

	public String getNonce() {
		LocalTime time = LocalTime.now();
		String hour = Integer.toString(time.getHour());
		String minute = Integer.toString(time.getMinute());
		String second = Integer.toString(time.getSecond());
		return hour+minute+second;
	}
	
	public void validateResponseCode(int expectedResponseCode) throws ParseException, IOException {
		
		boolean requestSuccessful = getResponseCode()==expectedResponseCode;
		if(!requestSuccessful) {
			Assert.fail("The response code returned by the request: "+getResponseCode()+" does not match the expected response code of: "+expectedResponseCode+". The response returned is: " + getResponseString());
		}
		
	}
	
}
