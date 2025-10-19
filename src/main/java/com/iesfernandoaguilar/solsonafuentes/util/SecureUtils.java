package com.iesfernandoaguilar.solsonafuentes.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * The SHA (Secure Hash Algorithm) is one of the popular cryptographic hash functions.
 * This is a one-way function, so the result cannot be decrypted back to the original value.
 * https://www.javaguides.net/2020/02/java-sha-512-hash-with-salt-example.html
 */

public class SecureUtils {
    private static String bytesToHex(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        return sb.toString();

    }

    public static String generate512(String passwordToHash, byte[] salt){
        // public static String generate512(String passwordToHash){
        String generatedPassword = null;
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] byteOfTextToHash = passwordToHash.getBytes(StandardCharsets.UTF_8);
            byte[] hashedByteArray  = md.digest(byteOfTextToHash);

            generatedPassword = bytesToHex(hashedByteArray);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public static byte[] getSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public static void main(String[] args) {
        //Ten en cuenta que ambas contraseñas son iguales porque usamos la misma sal para generar la misma contraseña hash.
        byte[] salt = getSalt();
        for (byte b : salt) {
            System.out.print(b);
        }
        System.out.println();

        String password1 = generate512("Password",salt);
        String password2 = generate512("Password",salt);
        System.out.println(" Password 1 -> " + password1);
        System.out.println(" Password 2 -> " + password2);
        if (password1.equals(password2)) {
            System.out.println("passwords are equal");
        }
    }
}