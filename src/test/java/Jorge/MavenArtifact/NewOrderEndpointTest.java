package Jorge.MavenArtifact;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.ClientProtocolException;
import org.testng.annotations.Test;

public class NewOrderEndpointTest {
	@Test(enabled=true)
	public void positiveTestCase() throws ClientProtocolException, IOException, InvalidKeyException, NoSuchAlgorithmException {

		int expectedResponseCode = 200;
		String json = "{\"request\": \"/v1/order/new\",\"account\": \"my-trading-account\",\"nonce\": <nonce>,\"client_order_id\": <client_order_id>,\"symbol\": \"btcusd\", \"amount\": \"0.001\",\"price\": \"3633.00\",\"side\": \"buy\",\"type\": \"exchange limit\"}";
		APITestUtil api = new APITestUtil();
		api.newOrderPostRequest(json);
		api.validateResponseCode(expectedResponseCode);	
		api.close();
	}
	
	@Test
	public void emptyJSONTest() throws ClientProtocolException, IOException, InvalidKeyException, NoSuchAlgorithmException {

		//When we send an empty JSON, the response should return 400 due to an invalid request
		int expectedResponseCode = 400;
		String json = "";
		APITestUtil api = new APITestUtil();
		api.newOrderPostRequest(json);
		api.validateResponseCode(expectedResponseCode);	
		api.close();
	}
	
	@Test
	public void excludeAccountTest() throws ClientProtocolException, IOException, InvalidKeyException, NoSuchAlgorithmException {

		//When we don't include the mandatory account parameter, we should get back a 400 error.
		int expectedResponseCode = 400;
		String json = "{\"request\": \"/v1/order/new\",\"nonce\": <nonce>,\"client_order_id\": <client_order_id>,\"symbol\": \"btcusd\", \"amount\": \"0.001\",\"price\": \"3633.00\",\"side\": \"buy\",\"type\": \"exchange limit\"}";
		APITestUtil api = new APITestUtil();
		api.newOrderPostRequest(json);
		api.validateResponseCode(expectedResponseCode);	
		api.close();
	}
	
	/*All tests would follow this format of submitting a request with a specific set of data, and validating the response based on what we expect for that set of data.
	
	I would also create additional tests that call other api's following a successful order creation. For example, calling the order status api after a successful creation 
	because this should return a value since the creation was successful. 
	
	I would also create methods to check the exact error reasons returned when an error code is returned. This would be to make sure that not only are we getting the expected response 
	code, but also the expected error messages. 
	*/
}


