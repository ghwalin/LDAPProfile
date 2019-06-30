package ch.ghwalin.ldap.util;

import ch.ghwalin.ldap.service.JerseyConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * utility for creating and reading JSON web token
 * <p>
 * LDAP Profile
 *
 * @author Marcel Suter
 * @version 1.0
 * @since 2019-04-03
 */

public class TokenHandler {

    /**
     * builds the token
     * @param data the token data
     * @param duration the duration of this token in minutes
     * @param role the user role ("user" or "admin")
     * @return
     */
    public static String buildToken(String data, int duration, String role) {
        byte[] keyBytes = JerseyConfig.getProperty("jwtSecret").getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
        Date now = new Date();
        Date expiration = new Date(now.getTime() + duration * 60000);
        String jws = Jwts.builder()
                .setIssuer("LDAPProfile")
                .setSubject(encrypt(data, JerseyConfig.getProperty("jwtKey")))
                .claim("role", encrypt(role, JerseyConfig.getProperty("jwtKey")))
                .setExpiration(expiration)
                .setIssuedAt(now)
                .signWith(secretKey)
                .compact();
        return jws;
    }

    /**
     * reads all claims from the token
     * @param token
     * @return
     */
    public static Map<String,String> readClaims(String token) {
        Map<String,String> claimMap = new HashMap<>();
        Jws<Claims> jwsClaims;
        byte[] keyBytes = JerseyConfig.getProperty("jwtSecret").getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
        try {
            jwsClaims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            claimMap.put(
                    "subject",
                    decrypt(
                            jwsClaims.getBody().getSubject(),
                            JerseyConfig.getProperty("jwtKey")
                    )
            );


        } catch (JwtException ex) {
            ex.printStackTrace();
//            System.out.println(ex.getCause());
        }
        return claimMap;
    }

    /**
     * encrypts the string
     * @author Lokesh Gupta (https://howtodoinjava.com/security/java-aes-encryption-example/)
     * @param strToEncrypt  string to be encrypted
     * @return encrypted string
     */
    private static String encrypt(String strToEncrypt, String secret) {
        try {
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(
                    cipher.doFinal(
                            strToEncrypt.getBytes(StandardCharsets.UTF_8)
                    )
            );
        } catch (Exception ex) {
            System.out.println("Error while encrypting: " + ex.toString());
        }
        return null;
    }

    /**
     * decrypts the string
     * @author Lokesh Gupta (https://howtodoinjava.com/security/java-aes-encryption-example/)
     * @param strToDecrypt  string to be dencrypted
     * @return decrypted string
     */
    private static String decrypt(String strToDecrypt, String secret) {
        try {
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception ex) {
            System.out.println("Error while decrypting: " + ex.toString());
        }
        return null;
    }
}
