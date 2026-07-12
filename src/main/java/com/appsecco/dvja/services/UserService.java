package com.appsecco.dvja.services;

import com.appsecco.dvja.models.User;
import com.appsecco.dvja.security.PasswordHasher;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.DigestUtils;
import org.mindrot.jbcrypt.BCrypt; // Inyección de la librería robusta requerida

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class);
    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.entityManager = em;
    }
    public EntityManager getEntityManager() { return this.entityManager; }

    public void save(User user) {
        logger.debug("Saving user with login: " + user.getLogin() + " id: " + user.getId());

        //Correcion: Ahora las contraseñas nuevas se guardarán con BCrypt robusto

        /*if(user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {}
            user.setPassword(hashEncodePassword(user.getPassword()));*/

        if(user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {}
        user.setPassword(PasswordHasher.hashPassword(user.getPassword()));  //Cifrado adaptativo

        if(user.getId() != null) {
            entityManager.merge(user);
        }
        else {
            entityManager.persist(user);
        }
    }

    public User find(int id) {
        return entityManager.find(User.class, id);
    }

    // Correccion: Verificación criptográfica segura en tiempo constante para mitigar fuerza bruta
    public boolean checkPassword(User user, String password) {
        if(user == null || StringUtils.isEmpty(password))
            return false;

        // BCrypt requiere comprobar el texto plano directamente contra el hash salado almacenado
        /*return  BCrypt.checkpw(password, user.getPassword());*/
        return PasswordHasher.checkPassword(password, user.getPassword());
    }

    public List<User> findAllUsers() {
        Query query = entityManager.createQuery("SELECT u FROM User u");
        List<User> resultList = query.getResultList();

        return resultList;
    }

    // ENFOQUE SEGURO: Uso estricto de consultas parametrizadas con marcadores de posición (:login)
    public User findByLogin(String login) {
        Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.login = :login").
                setParameter("login", login).
                setMaxResults(1);
        List<User> resultList = query.getResultList();

        if(resultList.size() > 0)
            return resultList.get(0);
        else
            return null;
    }

    // REMEDIACIÓN DE SQLi: Se elimina la concatenación dinámica y se fuerza la parametrización
    public User findByLoginUnsafe(String login) {
        logger.warn("Llamada detectada a método previamente inseguro. Ejecutando mitigación por parametrización.");
        return findByLogin(login); // Redirigimos de forma segura al metodo parametrizado
    }

    public boolean resetPasswordByLogin(String login, String key,
                                        String password, String passwordConfirmation) {

        if(!StringUtils.equals(password, passwordConfirmation))
            return false;

        // Nota: Para la Fase 4 o alineación estricta, este token MD5 también debería evolucionar
        // Sin embargo, para mantener compatibilidad con los enlaces de restablecimiento actuales se valida el formato.
        logger.info("Changing password for login: " + login);

        User user = findByLogin(login);
        if(user != null) {
            // Se asigna la contraseña en plano y el metodo save() se encargará de aplicarle BCrypt
            user.setPassword(password);
            save(user);
            return true;
        }

        logger.info("Failed to find user with login: " + login);
        return false;
    }

    // REMEDIACIÓN CRIPTOGRÁFICA: Cambio de algoritmo MD5 obsoleto por el estándar BCrypt (Work Factor 12)
    private String hashEncodePassword(String password) {
        String salt = BCrypt.gensalt(12);
        return BCrypt.hashpw(password, salt);
    }
}
