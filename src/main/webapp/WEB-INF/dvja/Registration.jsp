<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sb" uri="/struts-bootstrap-tags" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <jsp:include page="common/Head.jsp"></jsp:include>
</head>
<body>
    <jsp:include page="common/Navigation.jsp"></jsp:include>
    <div class='container' style='min-height: 450px'><div class='row'><div class='col-md-12'>

        <div class='row'>
            <div class='col-md-6 col-md-offset-3'>
                <div class='page-header'>
                    <h2>Registro de Usuario</h2>
                </div>

                <s:actionerror theme="bootstrap"/>
                <s:actionmessage theme="bootstrap"/>
                <s:fielderror theme="bootstrap"/>

                <div class='page-body'>
                    <s:form action="register" method="post" theme="bootstrap">
                        <s:textfield
                            label="Nombre Completo"
                            name="name"
                            placeholder="Ingrese su nombre completo"/>
                        <s:textfield
                            label="Usuario (Login)"
                            name="login"
                            placeholder="Ingrese su nombre de usuario"/>
                        <s:textfield
                            label="Correo Electrónico"
                            name="email"
                            placeholder="ejemplo@correo.com"/>
                         <s:password
                            label="Contraseña"
                            name="password"
                            placeholder="Ingrese su contraseña"/>
                          <s:password
                             label="Confirmar Contraseña"
                             name="passwordConfirmation"
                             placeholder="Confirme su contraseña"/>

                        <div class="form-group" style="margin-top: 20px; margin-bottom: 20px; padding: 15px; border: 1px solid #ccc; border-radius: 6px; background-color: #f9f9f9;">
                            <div class="checkbox" style="margin: 0; padding-left: 20px;">
                                <label style="font-size: 13px; line-height: 1.5; color: #333; font-weight: normal;">
                                    <input type="checkbox" name="acceptLOPDP" id="acceptLOPDP" value="true" required style="margin-top: 3px;">
                                    Doy mi <strong>consentimiento libre, específico, informado e inequívoco</strong> para el tratamiento de mis datos personales (nombre, correo electrónico blindado mediante AES-256-GCM y credenciales) bajo los términos establecidos en la <a href="#" data-toggle="modal" data-target="#lopdpModal" style="text-decoration: underline; color: #0275d8; font-weight: bold;">Política de Privacidad y Consentimiento (LOPDP Ecuador)</a>.
                                </label>
                            </div>
                        </div>

                         <s:submit cssClass="btn btn-primary btn-block" value="Registrarse"/>
                    </s:form>

                    <br/>
                    <a href='<s:url action="login"/>'>¿Ya estás registrado? Inicia sesión aquí</a>
                </div>
            </div>
         </div>

    </div></div></div>

    <div class="modal fade" id="lopdpModal" tabindex="-1" role="dialog" aria-labelledby="lopdpModalLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title" id="lopdpModalLabel"><strong>Política de Tratamiento de Datos (LOPDP Ecuador)</strong></h4>
          </div>
          <div class="modal-body" style="font-size: 13px; max-height: 350px; overflow-y: auto; line-height: 1.6;">
            <p>De conformidad con la <strong>Ley Orgánica de Protección de Datos Personales (LOPDP)</strong> de la República del Ecuador, le informamos detalladamente las condiciones bajo las cuales trataremos sus datos:</p>
            
            <p><strong>1. Finalidad del Tratamiento (Art. 10 LOPDP):</strong> Los datos recopilados en este formulario (Nombre, Usuario, Correo Electrónico y Contraseña) serán utilizados única y exclusivamente para la gestión de su cuenta académica, autenticación y análisis dentro de esta plataforma de simulación controlada (DVJA).</p>
            
            <p><strong>2. Medidas de Seguridad Criptográficas (Art. 39 LOPDP):</strong> Para salvaguardar su derecho a la confidencialidad, su correo electrónico será cifrado en reposo utilizando criptografía simétrica avanzada **AES-256 en modo GCM (Galois/Counter Mode)** antes de ser almacenado en la base de datos relacional MySQL. Sus contraseñas se almacenarán de forma irreversible mediante la función de derivación de clave robusta **BCrypt** (factor de costo computacional 12).</p>
            
            <p><strong>3. Base Legal del Tratamiento (Art. 8 LOPDP):</strong> La base que legitima este tratamiento es su consentimiento explícito provisto al marcar afirmativamente la casilla de verificación previa.</p>
            
            <p><strong>4. Ejercicio de Derechos ARCO:</strong> Como titular de la información, usted podrá revocar su consentimiento o solicitar el acceso, rectificación, eliminación u oposición de sus datos personales enviando una solicitud a la administración del sistema.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>
          </div>
        </div>
      </div>
    </div>

    <jsp:include page="common/Footer.jsp"></jsp:include>
</body>
</html>