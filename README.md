#  covidpy-core

Proyecto core (backend implementation) para el sistema de seguimiento de pacientes Covid19 Paraguay. Proyecto backend encargado de realizar las consultas a la base de datos.

## Introducción

Estas instrucciones permitirán levantar el proyecto core.

### Tecnologías

Las tecnologías utilizadas por el proyecto son:

```
Servidor de Aplicación: Wildfly 18.0.1
Sistema Operativo: Centos 7. 
Backend: Java EE
Base de Datos: Postgresql 12.2
Maven: Gestión de dependencias para proyecto Backend. Versión 3.3.9
Git: control de versionamiento.
Eclipse oxygen o superior con Jboss Tools instalado.
Redis
```

Nota:

* Este proyecto se conecta al Sistema de Intercambio de Información.
* Todos los cambios actualizados del código fuente se encuentran en la rama master


### Base de datos

El backup de base de datos se encuentra en la carpeta **"bd"**. 
Deberá crearse un usuario en la base de datos para conexión de la aplicación.

* [Postgresql](https://www.postgresql.org/docs/12/app-pgrestore.html) - Como restaurar backup de base de datos.

* Usuarios creados por defecto en la tabla covid19admin.usuario:

**Usuario:** admin **Contraseña:** admin      
**Usuario:** agendamiento **Contraseña:** agendamiento
      
El usuario admin tiene el rol de operador, con el cual se podrá ingresar al backend y realizar altas, agendamientos entre otros, el usuario agendamiento tiene el rol del mismo nombre, con el cual se podrán correr los scripts de envio automáticos de sms.

### Wildfly
  
  Descargue el servidor desde la página oficial de wildfly con la versión 18.0.1: http://wildfly.org/downloads/ 
  
  Configurar Módulo de Postgresql en Wildfly
  
  * Abrir la carpeta del directorio
  
      ```
      $WILDFLY_HOME/modules/system/layers/base/org/postgresql/main
      ```
  
  * Crear las carpetas: org, postgresql y main si no se encuentran creadas.
  * Copiar el driver del postgresql y pegarlo en la carpeta main, que fue creada en el punto 1.
  * Dentro de la carpeta main, crear un archivo de nombre **module.xml** y agregue el siguiente contenido (Reemplazar postgresql-XXX.jar por el nombre del driver):
  
      ```
	<?xml version="1.0" encoding="UTF-8"?>
        <module xmlns="urn:jboss:module:1.0" name="org.postgresql">
         <resources>
         <resource-root path="postgresql-XXX.jar"/>
         </resources>
         <dependencies>
         <module name="javax.api"/>
         <module name="javax.transaction.api"/>
         </dependencies>
        </module>
	```
    
   * Configuración del datasource en el standalone del Wildfly
      
      Abrir el directorio **$WILDFLY_HOME/standalone/configuration/**
      
      Dentro de la carpeta **/configuration/**, abra el archivo **standalone-full.xml o standalone.xml** y agregue el driver de postgresql. Ejemplo: 
   
        ```
	   <drivers>
          <driver name="h2" module="com.h2database.h2">
             <xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
          </driver>
          <driver name="postgresql" module="org.postgresql">
             <driver-class>org.postgresql.Driver</driver-class>
          </driver>
        </drivers>
       ```
      
      Dentro de la carpeta **/configuration/**, abra el archivo **standalone-full.xml o standalone.xml** busque los marcadores **<datasources></datasourses>** y dentro de los mismos agregue el siguiente contenido:
      
      
	<datasource jndi-name="java:jboss/datasources/covid19DS" pool-name="covid19DS" enabled="true" use-java-context="true">
          <connection-url>jdbc:postgresql://localhost:5432/covid19</connection-url>
          <driver-class>org.postgresql.Driver</driver-class>
          <driver>postgresql-XXX.jar</driver>
          <transaction-isolation>TRANSACTION_READ_COMMITTED</transaction-isolation>
          <pool>
              <min-pool-size>10</min-pool-size>
              <max-pool-size>100</max-pool-size>
              <prefill>true</prefill>
              <use-strict-min>false</use-strict-min>
              <flush-strategy>FailingConnectionOnly</flush-strategy>
          </pool>
          <security>
              <user-name>USUARIO</user-name>
              <password>CONTRASENHA</password>
          </security>
          <statement>
              <prepared-statement-cache-size>32</prepared-statement-cache-size>
          </statement>
        </datasource>
	
	
### Properties
  
  Es necesario la creación de un archivo con el nombre **"config.properties"** en el servidor en el siguiente path: **/opt/covid-core/** y colocar el siguiente contenido:

```  
  SII_USERNAME=usuario
  SII_PASSWORD=contrasenha
  URL_SERVER=url
  covid19_diagnostico_positivo_instrucciones_introduccion=A usted se le ha diagnosticado el virus que causa el COVID-19, tambi\u00e9n conocido como el nuevo coronavirus. Debe descansar, mantenerse hidratado y dormir suficiente. Es posible que sienta dolores en el cuerpo y fatiga durante varios d\u00edas. Si sus s\u00edntomas empeoran, llame a su m\u00e9dico, al 154 o busque atenci\u00f3n m\u00e9dica inmediata.\nUsted debe mantenerse aislado en su casa por 14 d\u00edas hasta que se sepa que sus pruebas son negativas o hasta que se le indique desde el Ministerio de Salud. Esto es obligatorio.\nIgualmente, debe enviar la \u201cActualizaci\u00f3n de su Ubicaci\u00f3n\u201d cada 12 horas y \u201cEstado de Salud\u201d cada 24 horas por medio de la aplicaci\u00f3n.
  covid19_diagnostico_positivo_instrucciones_recomendacion_seguimiento_estado_salud=- Tomarse la temperatura varias veces al d\u00eda\n- Si se siente peor (fiebre persistente, falta de aire, dolor en el pecho) debe consultar con su m\u00e9dico o ir al establecimiento de salud m\u00e1s cercano.\n- P\u00f3ngase una mascarilla antes de salir de su casa o pida una antes de entrar al establecimiento de salud. Esto ayudar\u00e1 a evitar que otras personas en el consultorio o en la sala de espera se infecten o se expongan.\n- Env\u00ede su reporte de Estado de Salud y Ubicaci\u00f3n por la aplicaci\u00f3n todos los d\u00edas.
  covid19_diagnostico_positivo_instrucciones_recomendacion_aislamiento_domiciliario=- El paciente debe mantener aislamiento en una habitaci\u00f3n individual, bien ventilada. Debe usar un ba\u00f1o separado en lo posible.\n- El paciente debe usar mascarilla (tapabocas) permanentemente.\n- Debe limitar sus movimientos dentro de la casa a lo estrictamente indispensable.\n- No recibir visitas.\n- No compartir utensilios para comer o beber, toallas, pa\u00f1os y ropas de cama. Todo debe ser exclusivo para la persona aislada.\n- Se debe desechar los materiales descartables como pa\u00f1uelos, toallas, mascarillas en basureros con bolsas exclusivamente para este fin. Luego se desecha como otro residuo dom\u00e9stico.\n- Higienizarse las manos con frecuencia. Lavarse las manos con agua y jab\u00f3n durante al menos 20 segundos.\n- Evitar tocarse la cara, la boca y los ojos sin lavarse las manos.\n- Mantener distanciamiento de las mascotas u otros animales mientras est\u00e9 enfermo.
  covid19_diagnostico_positivo_instrucciones_recomendacion_cuidadores=- El cuidador debe usar mascarilla (tapabocas) cubriendo la boca y la nariz, ajustada a la cara, cuando ingrese a la habitaci\u00f3n de la persona aislada.\n- NO TOCAR LA MASCARILLA, si se moja o humedece, debe ser cambiada de inmediato.\n- Debe desechar la mascarilla despu\u00e9s de su uso y debe lavarse las manos de manera inmediata.
  covid19_diagnostico_positivo_instrucciones_recomendacion_limpieza_1=- Utensilios y platos: Lavar con agua + detergente. Luego poner en remojo al menos por 2 minutos en abundante agua con hipoclorito de sodio. Escurrir hasta que sequen solos.\nF\u00f3rmula para agua de remojo: 1 cucharada (2ml) de hipoclorito de sodio (al 5-6%) por cada 4 litros de agua.\n- Superficies: Limpie y desinfecte diariamente las superficies que se tocan con mayor frecuencia (mesita de luz, somieres, picaporte y otros muebles del dormitorio) con hipoclorito de sodio.\nF\u00f3rmula Hipoclorito de concentraci\u00f3n al 5-6%:  Para diluirse en proporci\u00f3n 1:10:\n1 parte de Hipoclorito de sodio por 9 de agua.\nEjemplo: 1 taza de cloro y 9 tazas de agua.
  covid19_diagnostico_positivo_instrucciones_recomendacion_limpieza_2=- Pisos y ba\u00f1os: Primero limpiar con agua y detergentes comunes, luego repasar con hipoclorito de sodio (al 5-6%), dejar actuar por 10 minutos.\nEjemplo: \u00bd litro de hipoclorito de sodio en 10 litros de agua.\n- Ropas:\n- Colocar en bolsa de ropas, no agite la bolsa y evite el contacto directo con la piel.\n- Lavar ropas, ropa de cama, toallas de ba\u00f1o con agua caliente y jab\u00f3n o en lavarropas a 60-90\u00b0C con detergente de uso dom\u00e9stico.
```
 
### Levantar el proyecto backend
  
  ```
  Utilizando Eclipse posicionarse sobre el proyecto y con el botón derecho del mouse buscar la opción “Export” luego “War File”.
  Elegir en que carpeta desea crear el archivo war y seleccione la opción “Overwrite existing file”. Por último clic en el botón Finish. 
  Proceda a deployar en el servidor wildfly.
  ```
