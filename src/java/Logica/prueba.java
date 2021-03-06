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
    import java.util.logging.Level;
    import java.util.logging.Logger;
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
        
        //------------------------------
        //Atributos
        //------------------------------
        
        private HttpPost post;
        private final String url = "http://localhost/index.php?r=admin/remotecontrol";;
	private URL serverURL;
        private HttpClient cliente;
        private String key;
        
        //-----------------------------
        //Constructor
        //-----------------------------
        
        public prueba(){
            cliente = HttpClients.createDefault();
            post = new HttpPost(url);
		post.setHeader("Content-type", "application/json");
            try {
                key = getSessionKey();
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(prueba.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public static void main(String[] args){
            try {
                prueba prueba = new prueba();
            } catch (Exception ex) {
                Logger.getLogger(prueba.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //----------------------------
        //Metodos utiles para el app
        //----------------------------
        
	public static String parse(String jsonLine) {

            JsonElement jelement = new JsonParser().parse(jsonLine);
            JsonObject jobject = jelement.getAsJsonObject();
            String result = jobject.get("result").getAsString();
            return result;
	}
        
        public String getSessionKey() throws UnsupportedEncodingException{
            post.setEntity( new StringEntity("{\"method\": "
                    + "\"get_session_key\", "
                    + "\"params\": {\"username\": \"admin\", \"password\": "
                    + "\"password\" }, \"id\": 1}"));
            try {
                HttpResponse response = cliente.execute(post);
                if(response.getStatusLine().getStatusCode() == 200){
                    HttpEntity entidad = response.getEntity();
                    key = parse(EntityUtils.toString(entidad));
                }
            } catch (Exception e) {
                System.err.println("error para conseguir el key");
            }
            return key;
        }
    
        public String transformFromB64(String toConvert){
            String retorno = new String(Base64.decodeBase64(toConvert));
            return retorno;
        }
        
        public String getQuestionsProperties(int qId){
            JsonObject request = new JsonObject();
            JsonObject params = new JsonObject();
            request.addProperty("method", "get_question_properties");
            params.addProperty("sSessionKey", key);
            params.addProperty("iQuestionID", qId);
            params.addProperty("title", "title");
            params.addProperty("sid", "sid");
            params.addProperty("gid","gid");
            params.addProperty("preg","preg");
            params.addProperty("answeroptions", "answeroptions");
            request.add("aQuestionSettings", params);
            request.addProperty("id", 1);
            System.out.println("Request : "+request.toString() );
            try {
                post.setEntity(new StringEntity(request.toString()));
                HttpResponse response = cliente.execute(post);
                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entidad = response.getEntity();
                    System.out.println(EntityUtils.toString(entidad));
                    String ans = parse(EntityUtils.toString(entidad));
                    String traducida = transformFromB64(ans);
                    System.out.println(traducida);
                }
            } catch (UnsupportedEncodingException ex) {
                System.err.println("se toteo en la entidad");
            } catch (IOException ex) {
                System.err.println("se toteo en la respuesta");
            }
            return null;
        }
    
        
        public String listSurveys() throws IOException{
            JsonObject request = new JsonObject();
            JsonObject params = new JsonObject();
            params.addProperty("method", "list_surveys");
            request.addProperty("sSessionKey", key);
            request.addProperty("sUser", "admin");
            
            System.out.println("Request: " + request.toString());
            try {
                post.setEntity(new StringEntity(request.toString()));
                HttpResponse response = cliente.execute(post);
                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    //System.out.println(EntityUtils.toString(entity));
                    String ans = parse(EntityUtils.toString(entity));
                    String stringFromBase = transformFromB64(ans);
                    System.out.println(stringFromBase);
		}
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(prueba.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return null;
        }
        
        public static String ExportarRespuestas() throws UnsupportedEncodingException{
            HttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(
				"http://localhost/index.php?r=admin/remotecontrol");
		post.setHeader("Content-type", "application/json");
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
				params.addProperty("iSurveyID", 68124);
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
                return null;
        }
    }
