# 🔐 Proyecto de Desarrollo Seguro, Criptografía Aplicada y Hardening de Infraestructura sobre DVJA

## 📖 Descripción General

Este proyecto tiene como objetivo la identificación, explotación controlada, remediación y validación de vulnerabilidades de seguridad presentes en la aplicación **DVJA (Damn Vulnerable Java Application)**.

El trabajo fue desarrollado siguiendo un enfoque integral de seguridad compuesto por cuatro fases:

1. **Análisis Ofensivo y Línea Base de Vulnerabilidades**
2. **Desarrollo Seguro y Criptografía Aplicada**
3. **Hardening de Infraestructura y Dependencias**
4. **Gobierno de Seguridad y Cumplimiento de la LOPDP**

La metodología utilizada tomó como referencia:

- OWASP Top 10 2021
- ISO/IEC 27001
- NIST Cybersecurity Framework (CSF)
- Ley Orgánica de Protección de Datos Personales del Ecuador (LOPDP)

---

# 👥 Equipo de Trabajo

## Matriz de Roles y Responsabilidades

| Integrante | Rol Principal | Responsabilidades |
|------------|--------------|------------------|
| Damian Minda  | Líder del Proyecto + Ingeniero de Desarrollo Seguro | Coordinación general, planificación y consolidación del informe técnico + Corrección de vulnerabilidades y aplicación de controles criptográficos |
| Santiago Villarreal  | Analista Ofensivo | Identificación y explotación de vulnerabilidades OWASP |
| Francis Pulles  | Especialista en Infraestructura | Hardening de Docker, dependencias y entorno de ejecución |
| Billy Moreno  | Oficial de Cumplimiento | Elaboración de matriz de riesgos y adecuación LOPDP |

---

# 🏗️ Arquitectura del Proyecto

```text
DVJA
│
├── src/main/java
│   ├── controllers
│   ├── services
│   │   ├── UserService.java
│   │   ├── ProductService.java
│   │
│   └── security
│       ├── PasswordHasher.java
│       └── CryptoUtils.java
│
├── docker
├── mysql
├── pom.xml
└── Dockerfile
```

---

# ⚔️ Sección Ofensiva

## Objetivo

Identificar vulnerabilidades críticas presentes en la aplicación DVJA antes de aplicar controles defensivos.

---

## Vulnerabilidades Encontradas

| OWASP Top 10 | Vulnerabilidad |
|-------------|----------------|
| A03:2021 | SQL Injection |
| A07:2021 | Fallas de Autenticación |
| A02:2021 | Fallas Criptográficas |
| A01:2021 | Broken Access Control |
| A05:2021 | Security Misconfiguration |

---

## Evidencia 1 — SQL Injection

### Payload utilizado

```sql
' OR '1'='1
```

### Resultado

El sistema permitía alterar la lógica de autenticación mediante concatenación directa de consultas SQL.

### Evidencia

```java
String query =
"SELECT u FROM User u WHERE u.login = '" + login + "'";
```

### Impacto

- Acceso no autorizado.
- Bypass completo de autenticación.
- Exposición de información sensible.

---

## Evidencia 2 — Fuerza Bruta y Enumeración de Usuarios

### Comando utilizado

```bash
for clave in admin root password test 123456
do
curl -s \
-w "HTTP %{http_code} | Tamaño: %{size_download}\n" \
-d "username=admin&password=$clave" \
http://IP/login
done
```

### Resultado observado

| Contraseña | Tamaño Respuesta |
|------------|-----------------|
| admin | 5330 bytes |
| root | 5330 bytes |
| password | 5338 bytes |

### Impacto

Las diferencias de respuesta permitían identificar comportamientos internos del sistema y facilitar ataques automatizados.

---

## Evidencia 3 — Exposición de Datos Sensibles

### Payload utilizado

```bash
curl -s \
-d "username=%24%7B100%2B100%7D&password=test"
```

### Resultado

```text
Error setting expression 'username'
['${100+100}']
```

### Impacto

- Fuga de información interna.
- Procesamiento de datos en texto claro.
- Riesgo de exposición de información personal.

---

# 🛡️ Sección Defensiva

## Objetivo

Implementar controles de seguridad que mitiguen las vulnerabilidades identificadas durante la fase ofensiva.

---

# 🔒 Control 1 — Mitigación de SQL Injection

## Solución

Sustitución de consultas dinámicas por consultas parametrizadas.

### Código Corregido

```java
Query query = entityManager
.createQuery(
"SELECT u FROM User u WHERE u.login = :login")
.setParameter("login", login);
```

### Beneficios

- Separación entre datos y comandos.
- Neutralización de payloads SQL.
- Cumplimiento OWASP A03.

---

# 🔐 Control 2 — Protección de Contraseñas con BCrypt

## Algoritmo Utilizado

```text
BCrypt
```

### Configuración

```java
String salt = BCrypt.gensalt(12);
String hash = BCrypt.hashpw(password, salt);
```

### Validación Segura

```java
return BCrypt.checkpw(
password,
user.getPassword()
);
```

### Beneficios

- Salt aleatorio por usuario.
- Resistencia a Rainbow Tables.
- Mitigación de fuerza bruta.
- Verificación segura en tiempo constante.

---

# 🔑 Control 3 — Protección de Datos Personales

## Algoritmo Utilizado

```text
AES-256-GCM
```

### Implementación

```java
Cipher cipher =
Cipher.getInstance("AES/GCM/NoPadding");
```

### Características

| Propiedad | Estado |
|------------|---------|
| Confidencialidad | ✅ |
| Integridad | ✅ |
| Autenticación | ✅ |
| IV Aleatorio | ✅ |

### Ejemplo

```java
String emailCifrado =
CryptoUtils.encrypt(
email,
secretKey
);
```

### Cumplimiento

- LOPDP Ecuador
- ISO 27001 A.5.34
- NIST PR.DS-1

---

# 🏰 Hardening de Infraestructura

## Docker Hardening

### Problema Detectado

La aplicación se ejecutaba como:

```bash
root
```

### Solución

```dockerfile
RUN groupadd appgroup
RUN useradd -g appgroup appuser

USER appuser
```

### Beneficios

- Principio de mínimo privilegio.
- Reducción del impacto de explotación.
- Mitigación de escalamiento de privilegios.

---

## Hardening de Dependencias

### Dependencias Actualizadas

| Componente | Antes | Después |
|------------|--------|---------|
| Log4j | 2.3 | 2.17.1 |
| Apache Struts | 2.3.30 | 2.3.37 |
| Spring Framework | 3.0.5 | 3.2.18 |
| MySQL Connector | 5.1.42 | 5.1.49 |
| Gson | 2.8.1 | 2.8.9 |
| Commons Codec | 1.10 | 1.15 |

### Vulnerabilidades Mitigadas

- Log4Shell (CVE-2021-44228)
- CVE-2017-5638
- Vulnerabilidades de deserialización
- Riesgos MITM
- Vulnerabilidades DoS

---

# 📊 Matriz de Riesgos

| ID | Activo | Amenaza | Riesgo Inicial | Control Aplicado | Riesgo Residual |
|----|---------|---------|---------------|------------------|----------------|
| ACT-01 | Usuarios | Fuerza bruta | Crítico | BCrypt | Bajo |
| ACT-02 | Datos Personales | Exposición de PII | Crítico | AES-256-GCM | Bajo |
| ACT-03 | Base de Datos | SQL Injection | Crítico | Consultas Parametrizadas | Bajo |
| ACT-04 | API Administrativa | IDOR / BOLA | Alto | Control de Roles | Bajo |
| ACT-05 | Registro de Usuarios | Incumplimiento Legal | Alto | Consentimiento Expreso | Bajo |

---

# 🔐 Segregación de Funciones (SoD)

## Modelo Implementado

| Rol | Permisos |
|-------|----------|
| Cliente | Acceso únicamente a su información |
| Auditor | Consulta restringida y anonimizada |
| Administrador | Gestión completa del sistema |

### Beneficios

- Principio de mínimo privilegio.
- Separación de funciones.
- Mitigación de Broken Access Control.

---

# 📋 Anexo de Cumplimiento LOPDP

## Principios Aplicados

| Principio LOPDP | Implementación |
|----------------|---------------|
| Confidencialidad | AES-256-GCM |
| Integridad | GCM Authentication Tag |
| Minimización de Datos | Restricción de acceso por roles |
| Seguridad por Diseño | Desarrollo Seguro |
| Protección de Datos Personales | Cifrado antes de persistencia |
| Responsabilidad Proactiva | Matriz de Riesgos |

---

## Medidas Técnicas Implementadas

### Protección de Credenciales

- BCrypt Cost Factor 12
- Salt único por usuario

### Protección de Datos Personales

- AES-256-GCM
- IV aleatorio
- Claves gestionadas mediante variables de entorno

### Protección de Infraestructura

- Docker Hardening
- Usuario sin privilegios
- Dependencias actualizadas

### Protección de Aplicación

- Consultas parametrizadas
- Validación de roles
- Eliminación de componentes vulnerables

---

# 📈 Resultados Obtenidos

| Control | Estado |
|----------|---------|
| SQL Injection | ✅ Mitigado |
| Fuerza Bruta | ✅ Mitigado |
| Exposición de Datos | ✅ Mitigado |
| Escalamiento de Privilegios | ✅ Mitigado |
| Dependencias Vulnerables | ✅ Mitigado |
| Cumplimiento LOPDP | ✅ Implementado |

---

# 📚 Referencias

- OWASP Top 10 2021
- ISO/IEC 27001:2022
- NIST Cybersecurity Framework
- OWASP Dependency Check
- Apache Struts Security Bulletins
- Apache Log4j Security Advisories
- Ley Orgánica de Protección de Datos Personales del Ecuador (LOPDP)

---

# 🏁 Conclusión

La aplicación DVJA fue sometida a un proceso completo de análisis ofensivo, remediación segura, fortalecimiento de infraestructura y adecuación normativa. Las vulnerabilidades críticas identificadas inicialmente fueron mitigadas mediante controles criptográficos modernos, desarrollo seguro, hardening de dependencias y aplicación de principios de protección de datos personales, logrando una reducción significativa del riesgo residual y alineando el sistema con estándares internacionales de seguridad y cumplimiento.

# Damn Vulnerable Java Application

## Quick Start

Install Docker and Docker Compose.

```
docker-compose up
```
Navigate to `http://localhost:8080`

To update image

```
docker-compose build
```

## Requirements

* Java 1.7+
* Maven 3.x
* MySQL Server

## Configuration

### Database

Create MySQL database and credentials and configure the same in:

```
./src/main/webapp/WEB-INF/config.properties
```

### Schema Import

Import the schema into MySQL database:

```
$ mysql -u USER -pPASSWORD dvja < ./db/schema.sql
```

## Build

```
$ mvn clean package
```

The deployable `war` file is generated in targets directory.

## Run with Jetty

```
$ mvn jetty:run
```

This will start the `Jetty` server on port 8080.

## Deploy in Tomcat Server

* Build app
* Copy targets/dvja.war to Tomcat webapps directory
* To serve as root application, copy as `ROOT.war` to Tomcat webapps directory.

