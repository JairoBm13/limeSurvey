/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Logica;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
import java.net.URL;
import java.util.HashMap;

/**
 *
 * @author estudiante
 */
public class prueba2 {
    static String url = "http://localhost/index.php?r=admin/remotecontrol";
	static URL serverURL;

	public static void main(String[] args) {
		try {
			serverURL = new URL(url);

		} catch (Exception e) {
			// handle exception...
		}

		// Create new JSON-RPC 2.0 client session
		JSONRPC2Session mySession = new JSONRPC2Session(serverURL);
		mySession.getOptions().setRequestContentType("application/json");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("username", "admin");
		params.put("password", "password");
		JSONRPC2Request request = new JSONRPC2Request("get_session_key",
				params, 1);
		System.out.println(mySession);
		System.out.println(request.toJSONString());
		// Send request
		JSONRPC2Response response = null;

		try {
			response = mySession.send(request);

		} catch (JSONRPC2SessionException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			// handle exception...
		}

		// Print response result / error
		if (response.indicatesSuccess())
			System.out.println(response.getResult());
		else
			System.out.println(response.getError().getMessage());
	}
}
