    /*
    * To change this license header, choose License Headers in Project Properties.
    * To change this template file, choose Tools | Templates
    * and open the template in the editor.
    */

    package Logica;

    import com.google.gson.*;
    import java.io.IOException;
    import java.io.UnsupportedEncodingException;
    import java.net.URL;
    import org.apache.commons.codec.binary.Base64;
    import org.apache.http.HttpEntity;
    import org.apache.http.HttpResponse;
    import org.apache.http.client.HttpClient;
    import org.apache.http.client.methods.HttpPost;
    import org.apache.http.entity.StringEntity;
    import org.apache.http.impl.client.HttpClients;
    import org.apache.http.util.EntityUtils;
    
    /**
    *
    * @author estudiante
    */
    public class prueba {
        static String url = "http://localhost/index.php?r=admin/remotecontrol";
	static URL serverURL;

	public static String parse(String jsonLine) {
		JsonElement jelement = new JsonParser().parse(jsonLine);
		JsonObject jobject = jelement.getAsJsonObject();
		String result = jobject.get("result").getAsString();
		return result;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		HttpClient client = HttpClients.createDefault();

		HttpPost post = new HttpPost(
				"http://localhost/index.php?r=admin/remotecontrol");
		post.setHeader("Content-type", "application/json");
		post.setEntity(new StringEntity(
				"{\"method\": \"get_session_key\", \"params\": {\"username\": \"admin\", \"password\": \"password\" }, \"id\": 1}"));
		System.out.println(post);
		try {
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				String sessionKey = parse(EntityUtils.toString(entity));
				System.out.println(sessionKey);
				JsonObject request = new JsonObject();
				JsonObject params = new JsonObject();
				request.addProperty("id", 1);
				request.addProperty("method", "export_responses");
				params.addProperty("sSessionKey", sessionKey.toString());
				params.addProperty("iSurveyID", 436586);
				params.addProperty("sDocumentType", "json");
				request.add("params", params);
				System.out.println("Request: " + request.toString());
				post.setEntity(new StringEntity(request.toString()));
				response = client.execute(post);
				if (response.getStatusLine().getStatusCode() == 200) {
					entity = response.getEntity();
					JsonParser gson = new JsonParser();
					JsonObject respuesta = (JsonObject) gson.parse(EntityUtils
							.toString(entity));
					String ans = respuesta.get("result").getAsString();
					String stringFromBase = new String(Base64.decodeBase64(ans));
					System.out.println("reponse: " + stringFromBase);
				}
			}
			// post.setEntity( new
			// StringEntity("{\"method\": \"list_groups\", \"params\": {\"sSessionKey \": "+sessionKey+", \"iSurveyID \": \"436586\" }, \"id\": 1}"));
			// response = client.execute(post);
			// if(response.getStatusLine().getStatusCode() == 200){
			// entity = response.getEntity();
			// System.out.println(EntityUtils.toString(entity));
			// }
			// }

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    }
