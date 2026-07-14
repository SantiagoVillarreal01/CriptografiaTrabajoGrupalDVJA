package com.appsecco.dvja.controllers;

import com.appsecco.dvja.models.User;
import com.appsecco.dvja.services.UserService;
import org.apache.struts2.ServletActionContext;
import org.apache.commons.lang.StringUtils; 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiAction extends BaseController {

    private UserService userService;
    private String login;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Endpoint Administrativo: Retorna la lista de usuarios.
     * Solo debe ser accesible para el rol "Admin" o "Auditor" (con restricciones).
     */
    public String adminShowUsers() {
        Map<String, Object> results = new HashMap<>();
        
        // 1. Obtener de manera segura el usuario autenticado desde la sesión del servidor (BaseController)
        User currentUser = sessionGetUser(); 

        // 2. Control de Acceso y Segregación de Funciones
        if (currentUser == null) {
            results.put("error", "No autenticado. Acceso denegado.");
            ServletActionContext.getResponse().setStatus(401); // Unauthorized
            return renderJSON(results);
        }

        // Recuperamos el rol del usuario directamente del backend
        String role = currentUser.getRole() != null ? currentUser.getRole().toLowerCase() : "cliente";

        // Segregación estricta: Solo 'admin' o 'auditor' pueden consultar esta información
        if (role.equals("admin") || role.equals("auditor")) {
            List<Map<String, String>> userList = new ArrayList<>();

            for(User u: userService.findAllUsers()) {
                Map<String, String> m = new HashMap<>();
                m.put("id", Integer.toString(u.getId()));
                m.put("login", u.getLogin());
                
                // Un Auditor solo puede ver datos no críticos para auditoría de cumplimiento
                if (role.equals("auditor")) {
                    m.put("role", u.getRole());
                    m.put("email", "[REDACTADO POR SEGURIDAD]"); // Segregación a nivel de campo
                } else {
                    // El Admin tiene acceso total
                    m.put("email", u.getEmail());
                    m.put("role", u.getRole());
                }
                userList.add(m);
            }

            results.put("count", userList.size());
            results.put("users", userList);
            return renderJSON(results);
        } else {
            // El rol 'cliente' es rechazado de inmediato
            results.put("error", "Acceso denegado. Privilegios insuficientes para el rol de Cliente.");
            ServletActionContext.getResponse().setStatus(403); // Forbidden
            return renderJSON(results);
        }
    }

    public String ping() {
        Map<String, String> results = new HashMap<String, String>();

        if(StringUtils.isEmpty(getLogin())) {
            results.put("error", "Login not set");
            return renderJSON(results);
        }

        User user = userService.findByLogin(getLogin());

        results.put("login", getLogin());
        results.put("present", "true");
        results.put("email", user.getEmail());

        return renderJSON(results);
    }
}