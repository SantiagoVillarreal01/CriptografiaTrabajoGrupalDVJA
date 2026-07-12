package com.appsecco.dvja.services;

import com.appsecco.dvja.models.User;
import com.appsecco.dvja.security.CryptoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.log4j.Logger;

public class UserRegistrationService {

    private static final Logger logger = Logger.getLogger(UserRegistrationService.class);

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public UserService getUserService() {
        return userService;
    }

    public User register(String name, String login, String email,
                         String password, String passwordConfirmation)
    {
        User user;

        if(password == null)
            return null;
        if(!password.equals(passwordConfirmation))
            return null;

        user = new User();
        user.setName(name);
        user.setLogin(login);
        user.setPassword(password); // El UserService se encargará de aplicarle BCrypt en el metodo .save()

        // IMPLEMENTACIÓN CRIPTOGRÁFICA OPTIMIZADA - PROTECCIÓN LOPDP ECUADOR
        try {
            String keyEnv = System.getenv("AES_SECRET_KEY");
            if (keyEnv == null || keyEnv.isEmpty()) {
                // CORRECCIÓN: Clave ajustada a exactamente 32 bytes (32 caracteres de 1 byte)
                keyEnv = "ClaveMaestraSuperSecretaDe256Bit";
            }
            byte[] secretKeyBytes = keyEnv.getBytes("UTF-8");

            // Ciframos el email antes de mandarlo al UserService / Base de datos MySQL
            String emailCifrado = CryptoUtils.encrypt(email, secretKeyBytes);
            user.setEmail(emailCifrado);

            logger.info("Dato de identidad personal (Email) cifrado exitosamente mediante AES-256-GCM.");
        } catch (Exception e) {
            logger.error("Falla crítica al aplicar el algoritmo de cifrado sobre el dato sensible: " + e.getMessage());
            // Opcional para pruebas: lanzar el error para no registrar datos desprotegidos
            throw new RuntimeException("Error en cifrado AES: " + e.getMessage());
        }

        userService.save(user);
        return user;
    }
}
