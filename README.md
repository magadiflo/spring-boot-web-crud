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

**NOTA**

> Existe otra manera de configurar el cors y es creando un<br>
> **@Bean<br>
> public CorsFilter corsFilter() {...}**<br>
> Ese tipo de configuración lo realizamos en el curso de **Get Arrays**

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

## Definiendo Entidades y relaciones de Muchos a Muchos

Vamos a trabajar con una relación de `Muchos a Muchos` entre `Author` y `Book`, pero en este proyecto no usaremos la
anotación `@ManyToMany`, ya que esta anotación nos crea automáticamente una tabla intermedia. En nuestro caso, seremos
nosotros mismos quienes crearemos la tabla intermedia a través de una entidad mapeada a la base de datos.

### Entidad Author y Book

````java

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthdate;
}
````

````java

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private LocalDate publicationDate;
    @Builder.Default
    private Boolean onlineAvailability = false;
}
````

### Entidad de relación usando clave primaria compuesta

Es importante recordar que cuando utilizas una entidad de relación como esta, `debes manejar la persistencia de la misma
manualmente`. **Por ejemplo, cuando guardas una relación entre un book y un author, necesitarás crear una instancia de
BookAuthor y persistirla en tu contexto de persistencia.**

Primero necesitamos crear nuestra clase `BookAuthorPK`, que será la clase que represente la `clave primaria compuesta`
para nuestra tabla intermedia.

````java

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Embeddable
public class BookAuthorPK {

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

}
````

**DONDE**

- `@Embeddable`, especifica una clase cuyas instancias se almacenan como parte intrínseca de una entidad propietaria y
  comparten la identidad de la entidad. Cada una de las propiedades o campos persistentes del objeto incrustado se
  asigna a la tabla de base de datos de la entidad.

Representamos una `clave primaria compuesta` en Spring Data utilizando la anotación `@Embeddable` en una clase. Luego,
esta clave se incrusta en la clase de entidad correspondiente de la tabla como clave principal compuesta mediante el uso
de la anotación `@EmbeddedId` en un campo del tipo `@Embeddable`.

Ahora toca definir la entidad que mapeará a nuestra tabla intermedia donde hacemos uso de la clase `BookAuthorPK`
creada en el apartado superior:

````java

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "books_authors")
public class BookAuthor {

    @EmbeddedId
    private BookAuthorPK id;

}
````

**DONDE**

- `@EmbeddedId`, se aplica a un campo persistente o propiedad de una clase de entidad o superclase asignada para indicar
  una `clave primaria compuesta` que es una clase integrable (`@Embeddable`). La clase integrable debe anotarse
  como `@Embeddable`. Solo debe haber una anotación `EmbeddedId` y ninguna anotación `Id` cuando se utiliza la anotación
  `EmbeddedId`.

### Ejecutando la aplicación

Al ejecutar la aplicación veremos el siguiente log en consola:

````bash
2024-02-20T00:11:50.249-05:00 DEBUG 13320 --- [spring-boot-web-crud] [           main] org.hibernate.SQL                        : 
    create table authors (
        id bigint not null auto_increment,
        birthdate date,
        first_name varchar(255),
        last_name varchar(255),
        primary key (id)
    ) engine=InnoDB
2024-02-20T00:11:50.352-05:00 DEBUG 13320 --- [spring-boot-web-crud] [           main] org.hibernate.SQL                        : 
    create table books (
        id bigint not null auto_increment,
        online_availability bit,
        publication_date date,
        title varchar(255),
        primary key (id)
    ) engine=InnoDB
2024-02-20T00:11:50.409-05:00 DEBUG 13320 --- [spring-boot-web-crud] [           main] org.hibernate.SQL                        : 
    create table books_authors (
        author_id bigint not null,
        book_id bigint not null,
        primary key (author_id, book_id)
    ) engine=InnoDB
2024-02-20T00:11:50.464-05:00 DEBUG 13320 --- [spring-boot-web-crud] [           main] org.hibernate.SQL                        : 
    alter table books_authors 
       add constraint FK3qua08pjd1ca1fe2x5cgohuu5 
       foreign key (author_id) 
       references authors (id)
2024-02-20T00:11:50.583-05:00 DEBUG 13320 --- [spring-boot-web-crud] [           main] org.hibernate.SQL                        : 
    alter table books_authors 
       add constraint FK1b933slgixbjdslgwu888m34v 
       foreign key (book_id) 
       references books (id)
````

Y si usamos `DBeaver` para ver el diagrama en la base de datos, veremos que las tablas y sus relaciones se han
creado correctamente:

![database relationship](./assets/01.database-relationship.png)

