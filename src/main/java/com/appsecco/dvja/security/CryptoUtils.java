package com.appsecco.dvja.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoUtils {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_BIT_LENGTH = 128; // Longitud del tag de autenticación en bits
    private static final int IV_BYTE_LENGTH = 12;  // Tamaño estándar recomendado para vectores GCM

    // Blindaje de información confidencial previo al almacenamiento en la base de datos MySQL
    public static String encrypt(String rawData, byte[] secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = new byte[IV_BYTE_LENGTH];
        new SecureRandom().nextBytes(iv); // Generación de un IV criptográficamente aleatorio

        SecretKey key = new SecretKeySpec(secretKey, "AES");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_BIT_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        byte[] cipherText = cipher.doFinal(rawData.getBytes());
        byte[] encryptedPayload = new byte[iv.length + cipherText.length];

        // Estructuración del bloque empaquetado final: [IV (12 bytes) + Texto Cifrado]
        System.arraycopy(iv, 0, encryptedPayload, 0, iv.length);
        System.arraycopy(cipherText, 0, encryptedPayload, iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(encryptedPayload);
    }
}
