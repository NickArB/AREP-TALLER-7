package edu.escuelaing.arep;

import static spark.Spark.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ){
        // secure(keystoreFilePath, keystorePassword, truststoreFilePath,truststorePassword);
        secure("certificados/ecikeystore.p12", "123456", null, null); 
        port(getPort());
        get("hello", (request, response) -> {
            return "Hello World";
        });
    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 5000; //returns default port if heroku-port isn't set (i.e. on localhost)
    }

    
}
