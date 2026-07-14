package com.appsecco.dvja.controllers;

import com.appsecco.dvja.models.User;
import com.appsecco.dvja.services.UserRegistrationService;
import org.apache.commons.lang.StringUtils;

public class Register extends BaseController {

    private String name;
    private String login;
    private String email;
    private String password;
    private String passwordConfirmation;
    private boolean acceptLOPDP; // Nuevo parámetro para cumplir con la LOPDP

    private UserRegistrationService userRegistrationService;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public boolean isAcceptLOPDP() {
        return acceptLOPDP;
    }

    public void setAcceptLOPDP(boolean acceptLOPDP) {
        this.acceptLOPDP = acceptLOPDP;
    }

    public UserRegistrationService getUserRegistrationService() {
        return userRegistrationService;
    }

    public void setUserRegistrationService(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @Override
    public void validate() {
        if(!getServletRequest().getMethod().equalsIgnoreCase("POST"))
            return;

        if(StringUtils.isEmpty(getName()))
            addFieldError("name", "El nombre es obligatorio.");
        if(StringUtils.isEmpty(getLogin()))
            addFieldError("login", "El nombre de usuario es obligatorio.");
        if(StringUtils.isEmpty(getEmail()))
            addFieldError("email", "El correo electrónico es obligatorio.");
        if(StringUtils.isEmpty(getPassword()))
            addFieldError("password", "La contraseña es obligatoria.");
        if(StringUtils.isEmpty(getPasswordConfirmation()))
            addFieldError("passwordConfirmation", "La confirmación de la contraseña es obligatoria.");
        if(!StringUtils.equals(getPassword(), getPasswordConfirmation()))
            addFieldError("password", "Las contraseñas no coinciden.");
        
        // VALIDACIÓN DE SEGURIDAD LOPDP (BACKEND)
        if(!acceptLOPDP) {
            addFieldError("acceptLOPDP", "Debe otorgar su consentimiento explícito conforme a la LOPDP de Ecuador para poder registrarse.");
        }
    }

    @Override
    public String execute() {
        User user = null;

        try {
            user = userRegistrationService.register(getName(), getLogin(), getEmail(),
                    getPassword(), getPasswordConfirmation());
        }
        catch(Exception e) {
            addActionError("Error ocurrido durante el proceso: " + e.getMessage());
        }

        if(user != null) {
            return SUCCESS;
        }

        return INPUT;
    }
}