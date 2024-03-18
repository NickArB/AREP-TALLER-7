package edu.escuelaing.arep;

import static spark.Spark.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class LoginService{
    private static Map<String,String> db = new HashMap<>();

    public static void main( String[] args ) throws NoSuchAlgorithmException{
        db.put("nick", SHAEncrypt("123456"));
        staticFiles.location("/public");
        secure("certificados/ecikeystore.p12", "123456", null, null); 
        port(getPort());

        before("/secure/*", (req, res) -> {
            String token = req.headers("Authorization");
            System.out.println(JWTService.decodeToken(token).getSubject());
            if (token == null || ! (db.keySet().contains(JWTService.decodeToken(token).getSubject()))) {
                halt(401, "Not logged yet");
                res.redirect("unauthorized.html");
            }
        });

        post("/login", (request, response) -> {
            return loginIn(request.body());
        });

        get("/secure/secure-resource", (request,response) ->{
            return SecureURLReader.readURL("https://localhost:5500/secure/success", request.headers("Authorization"));
        });
        
        get("/", (request,response) -> {
            response.redirect("login.html");
            return null;
        });
    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 5000; //returns default port if heroku-port isn't set (i.e. on localhost)
    }

    public static String loginIn(String message){
        String values;
        String ans = "";

        values = message.replaceAll("\\\"", "");
        values = values.replace("{", "");
        values = values.replace("}", "");
        String[] valuesLst = {"",""};
        int index = 0;
        
        for(String s: values.split(",")){
            valuesLst[index] = s.split(":")[1];
            index ++;
        }

        if(db.containsKey(valuesLst[0])){
            try {
                if(db.get(valuesLst[0]).equals(SHAEncrypt(valuesLst[1]))){
                    ans = "{\"status\":\"AUTHORIZED\",\"token\":\"" + generateTokenBySession(valuesLst[0]) + "\"}";
                }else{
                    ans = "{\"status\":\"UNAUTHORIZED\"}";
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }else{
            ans = "{\"status\":\"UNAUTHORIZED\"}";
        }
        return ans;
    }

    private static String generateTokenBySession(String sessionName){
        return JWTService.generateToken(sessionName);
    }

    public static String SHAEncrypt(String pass) throws NoSuchAlgorithmException{
        // get an instance of the SHA-256 message digest algorithm
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // compute the hash of the input string
        byte[] hash = md.digest(pass.getBytes());

        // convert the hash to a hexadecimal string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}

// {"user":"reaper", "hashp":"as98d9ad"} --> (user:reaper,hashp:as98d9ad) --> [(user:reaper), (hashp:as98d9ad)]
// --> ["reaper", "as98d9ad"]
