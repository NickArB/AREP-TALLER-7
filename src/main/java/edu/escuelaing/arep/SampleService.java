package edu.escuelaing.arep;

import java.util.Map;
import java.util.HashMap;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import static spark.Spark.*;

public class SampleService {
    private static Map<String,String> db = new HashMap<>();


    
    public static void main(String[] args) throws NoSuchAlgorithmException {
        db.put("nick", SHAEncrypt("123456"));
        staticFiles.location("/public");
        secure("certificados/ecikeystore.p12", "123456", null, null); 
        port(getPort());

        before("/secure/*", (req, res) -> {
            String token = req.headers("Authorization");
            if (token == null || ! (db.keySet().contains(JWTService.decodeToken(token).getSubject()))) {
                halt(401, "Unauthorized");
            }
        });

        get("/secure/success", (req, res) -> {
            return "This is a secure resource and only works if u are logged in!";
        });
    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 5500; //returns default port if heroku-port isn't set (i.e. on localhost)
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
