package com.appsecco.dvja.security;


import org.mindrot.jbcrypt.BCrypt; // **Suite criptográfica para mitigar fallas de autenticación**

public class PasswordHasher {

    // **ROBUSTEZ:** Aplicación de un factor de costo computacional 12 para mitigar ataques por ráfagas
    public static String hashPassword(String plainTextPassword) {
        String salt = BCrypt.gensalt(12); // Generación de sal aleatoria única por usuario
        return BCrypt.hashpw(plainTextPassword, salt);
    }

    // **PREVENCIÓN DE TIMING ATTACKS:** Verificación en tiempo constante contra el hash salado
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            return false;
        }
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}