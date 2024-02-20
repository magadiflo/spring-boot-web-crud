# [Spring Boot + Spring Data JPA | CRUD](https://www.youtube.com/watch?v=s7a9aVGxwxs)

Tutorial tomado del canal de **Joas Dev**

---

## Dependencias

````xml
<!--Spring Boot 3.2.2-->
<!--Java 21-->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.modelmapper</groupId>
        <artifactId>modelmapper</artifactId>
        <version>3.0.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
````

## Configurando application.yml

Vamos a configurar las propiedades de conexión a la base de datos y agregar configuraciones adicionales:

````yml
server:
  port: 8080
  error:
    include-message: always

spring:
  application:
    name: spring-boot-web-crud

  datasource:
    url: jdbc:mysql://localhost:3306/db_spring_boot_web_crud
    username: admin
    password: magadiflo

  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    org.hibernate.SQL: DEBUG
````

## Configurando CORS y creando Bean Model Mapper

Creamos la clase de configuración `AppConfig` que implementa la interfaz `WebMvcConfigurer`. Gracias a esa
implementación podemos sobreescribir el método `addCorsMappings(CorsRegistry registry)` para configurar el `CORS`.
Es importante notar que se está definiendo los `@Value()` con el nombre de las configuraciones
que podríamos colocar en el `application.yml`, de esa manera si más adelante quisiéramos agregar alguna configuración
al cors, simplemente agregamos el nombre de la configuración correspondiente en el archivo yml. Sin embargo, tal cual
está ahora nuestro `application.yml` no tiene ninguna de esas configuraciones definidas, por lo tanto se tomará
por defecto el valor después de los `dos puntos (:)` en cada propiedad. Por ejemplo, del siguiente atributo se está
tomando el valor por defecto `/**` ya que no está configurado en el application.yml:

````
@Value("${app.cors.pathPattern:/**}")
private String pathPattern;
````

Además de la configuración del cors, vamos a configurar el `@Bean` del `ModelMapper` para usarlo más adelante en la
aplicación:

````java

@Slf4j
@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Value("${app.cors.pathPattern:/**}")
    private String pathPattern;

    @Value("${app.cors.allowedOrigins:*}")
    private String[] allowedOrigins;

    @Value("${app.cors.allowedHeaders:*}")
    private String[] allowedHeaders;

    @Value("${app.cors.allowedMethods:*}")
    private String[] allowedMethods;

    @Value("${app.cors.maxAge:1800}")
    private long maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("pathPattern: {}", this.pathPattern);
        log.info("allowedOrigins: {}", Arrays.toString(this.allowedOrigins));
        log.info("allowedMethods: {}", Arrays.toString(this.allowedMethods));
        log.info("maxAge: {}", maxAge);

        registry.addMapping(this.pathPattern)
                .allowedHeaders(this.allowedHeaders)
                .allowedOrigins(this.allowedOrigins)
                .allowedMethods(this.allowedMethods)
                .maxAge(this.maxAge);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
````

## Creando excepción personalizada

````java

@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus httpStatus;

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.httpStatus = status;
    }
}
````