package edu.escuelaing.arep;

import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class SecureURLReader {

    public static void init() {
        try {
            // Create a file and a password representation
            File trustStoreFile = new File("certificados/myTrustStore.p12");
            char[] trustStorePassword = "654321".toCharArray();

            // Load the trust store, the default type is "pkcs12", the alternative is "jks"
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(new FileInputStream(trustStoreFile), trustStorePassword);

            // Get the singleton instance of the TrustManagerFactory
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            
            // Itit the TrustManagerFactory using the truststore object
            tmf.init(trustStore);
            
            //Set the default global SSLContext so all the connections will use it
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            SSLContext.setDefault(sslContext);

            // We can now read this URL
            // readURL("https://localhost:5000/hello");

            // This one can't be read because the Java default truststore has been 
            // changed.
            // readURL("https://www.google.com");         
        
        } catch (KeyStoreException ex) {
            Logger.getLogger(SecureURLReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SecureURLReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SecureURLReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SecureURLReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(SecureURLReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(SecureURLReader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static String readURL(String sitetoread, String jwtToken) {
        init();
        try {
            // Crea el objeto que representa una URL
            URL siteURL = new URL(sitetoread);
            // Crea el objeto que URLConnection
            HttpsURLConnection urlConnection = (HttpsURLConnection) siteURL.openConnection();
    
            urlConnection.setRequestProperty("Authorization", jwtToken);
    
            // Realiza la solicitud GET
            urlConnection.setRequestMethod("GET");
    
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuffer ans = new StringBuffer();
    
            String inputLine = null;
            while ((inputLine = reader.readLine()) != null) {
                ans.append(inputLine);
            }
            return ans.toString();
        } catch (IOException x) {
            System.err.println(x);
            return null;
        }
    }    
}