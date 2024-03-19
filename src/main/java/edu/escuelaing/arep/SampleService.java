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
            System.out.println((token != null));
            if (token != null) {
                if(!token.equals("")){
                    if (!(db.keySet().contains(JWTService.decodeToken(token).getSubject()))){
                        halt(401, "You are not logged in!");
                    }
                }else{
                    halt(401, "You are not logged in!");
                }
            }else{
                halt(401, "You are not logged in!");
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
