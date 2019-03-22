* [Home](https://memorynotfound.com/) 
* [Spring Framework](https://memorynotfound.com/category/spring-framework/) 
* [Spring Boot](https://memorynotfound.com/category/spring-framework/spring-boot/) 


## Custom Password Constraint Validator Annotation Example

This tutorial demonstrates how to create a custom password validator annotation using custom password rules. You’ll be able to annotate your password field with a `@ValidPassword`. This’ll trigger the custom `PasswordConstraintValidator` which’ll enforce a server-side password policy.

### Password Policy

We can create our own custom password validation rules. We created the following password policy:

*   The password length must be between 8 and 30 characters.
*   The password must contain at least 1 upper-case character.
*   The password must contain at least 1 lower-case character.
*   The password must contain at least 1 digit character.
*   The password must contain at least 1 symbol (special character).
*   The password cannot contain whitespaces.
*   The password cannot match from dictionary.

## Project Structure

Let’s start by looking at the project structure.

<picture width="800" height="567" class="aligncenter size-full wp-image-7350 sp-no-webp"><source data-srcset="https://memorynotfound.com/wp-content/uploads/custom-password-constraint-validator-annotation-example-project-structure.webp" type="image/webp"> <source data-srcset="https://memorynotfound.com/wp-content/uploads/custom-password-constraint-validator-annotation-example-project-structure.png"></picture> 

## Maven Dependencies

We use Apache Maven to manage our project dependencies. Make sure the following dependencies reside on the class-path. We use `org.passay:passay` which provides a comprehensive and extensible feature set of password policy enforcement.

    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                                 http://maven.apache.org/xsd/maven-4.0.0.xsd">

        <modelVersion>4.0.0</modelVersion>
        <groupId>com.memorynotfound.spring.security</groupId>
        <artifactId>password-strength</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <url>http://memorynotfound.com</url>
        <name>Spring Security - ${project.artifactId}</name>

        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>1.5.8.RELEASE</version>
        </parent>

        <properties>
            <java.version>1.8</java.version>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        </properties>

        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-thymeleaf</artifactId>
            </dependency>
            <dependency>
                <groupId>commons-beanutils</groupId>
                <artifactId>commons-beanutils</artifactId>
            </dependency>
            <dependency>
                <groupId>org.passay</groupId>
                <artifactId>passay</artifactId>
                <version>1.3.0</version>
            </dependency>

            <!-- bootstrap and jquery -->
            <dependency>
                <groupId>org.webjars</groupId>
                <artifactId>bootstrap</artifactId>
                <version>3.3.7</version>
            </dependency>
            <dependency>
                <groupId>org.webjars</groupId>
                <artifactId>jquery</artifactId>
                <version>3.2.1</version>
            </dependency>

            <!-- testing -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-test</artifactId>
                <scope>test</scope>
            </dependency>
        </dependencies>

        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                </plugin>
            </plugins>
        </build>

    </project>

## Custom @ValidPassword Annotation

Let’s start by creating the `@ValidPassword` annotation. This annotation is a field-level annotation which is validated by the `PasswordConstraintValidator` that we’ll create in the following section.

    package com.memorynotfound.spring.security.constraint;

    import javax.validation.Payload;
    import javax.validation.Constraint;
    import java.lang.annotation.Documented;
    import java.lang.annotation.Retention;
    import java.lang.annotation.Target;
    import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
    import static java.lang.annotation.ElementType.TYPE;
    import static java.lang.annotation.ElementType.FIELD;
    import static java.lang.annotation.RetentionPolicy.RUNTIME;

    @Documented
    @Constraint(validatedBy = PasswordConstraintValidator.class)
    @Target({ TYPE, FIELD, ANNOTATION_TYPE })
    @Retention(RUNTIME)
    public @interface ValidPassword {

        String message() default "Invalid Password";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};

    }

## Custom Password Constraint Validator

Here, we create the actual password validator. We use `org.passay:passay` to enforce our custom password policy. We start by initializing a `DictionaryRule` which reads and parses a list of insecure password which the user cannot use. Finally, we create a `PasswordValidator` and pass the different password rules as arguments to the constructor. We enforce our password policy by creating different rules.

*   `LengthRule(8, 30)` – enforces the password length to be between 8 and 30 characters.
*   `CharacterRule(EnglishCharacterData.UpperCase, 1)` – enforces the password to have at least 1 upper-case character.
*   `CharacterRule(EnglishCharacterData.LowerCase, 1)` – enforces the password to have at least 1 lower-case character.
*   `CharacterRule(EnglishCharacterData.Digit, 1)` – enforces the password to have at least 1 digit character.
*   `CharacterRule(EnglishCharacterData.Special, 1)` – enforces the password to have at least 1 symbol (special character).
*   `WhitespaceRule` – enforces the password does not contain a whitespace.

    package com.memorynotfound.spring.security.constraint;

    import org.passay.*;
    import org.passay.dictionary.WordListDictionary;
    import org.passay.dictionary.WordLists;
    import org.passay.dictionary.sort.ArraysSort;

    import javax.validation.ConstraintValidator;
    import javax.validation.ConstraintValidatorContext;
    import java.io.FileReader;
    import java.io.IOException;
    import java.util.Arrays;
    import java.util.List;
    import java.util.stream.Collectors;

    public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

        private DictionaryRule dictionaryRule;

        @Override
        public void initialize(ValidPassword constraintAnnotation) {
            try {
                String invalidPasswordList = this.getClass().getResource("/invalid-password-list.txt").getFile();
                dictionaryRule = new DictionaryRule(
                        new WordListDictionary(WordLists.createFromReader(
                                // Reader around the word list file
                                new FileReader[] {
                                        new FileReader(invalidPasswordList)
                                },
                                // True for case sensitivity, false otherwise
                                false,
                                // Dictionaries must be sorted
                                new ArraysSort()
                        )));
            } catch (IOException e) {
                throw new RuntimeException("could not load word list", e);
            }
        }

        @Override
        public boolean isValid(String password, ConstraintValidatorContext context) {
            PasswordValidator validator = new PasswordValidator(Arrays.asList(

                    // at least 8 characters
                    new LengthRule(8, 30),

                    // at least one upper-case character
                    new CharacterRule(EnglishCharacterData.UpperCase, 1),

                    // at least one lower-case character
                    new CharacterRule(EnglishCharacterData.LowerCase, 1),

                    // at least one digit character
                    new CharacterRule(EnglishCharacterData.Digit, 1),

                    // at least one symbol (special character)
                    new CharacterRule(EnglishCharacterData.Special, 1),

                    // no whitespace
                    new WhitespaceRule(),

                    // no common passwords
                    dictionaryRule
            ));

            RuleResult result = validator.validate(new PasswordData(password));

            if (result.isValid()) {
                return true;
            }

            List<String> messages = validator.getMessages(result);
            String messageTemplate = messages.stream().collect(Collectors.joining(","));
            context.buildConstraintViolationWithTemplate(messageTemplate)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }
    }

The `invalid-password-list.txt` is loaded from the `src/main/resources` folder. You can add any password to this list which the user isn’t able to use.

    azerty12!
    12345678!
    password123

## Using the Password Validator Annotation

We use the `@ValidPassword` annotation on `FIELD` level.

    package com.memorynotfound.spring.security.web.dto;

    import com.memorynotfound.spring.security.constraint.ValidPassword;
    import org.hibernate.validator.constraints.Email;
    import org.hibernate.validator.constraints.NotEmpty;
    import javax.validation.constraints.AssertTrue;

    public class UserRegistrationDto {

        @NotEmpty
        private String firstName;

        @NotEmpty
        private String lastName;

        @NotEmpty
        @ValidPassword
        private String password;

        @NotEmpty
        @ValidPassword
        private String confirmPassword;

        @Email
        @NotEmpty
        private String email;

        @Email
        @NotEmpty
        private String confirmEmail;

        @AssertTrue
        private Boolean terms;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getConfirmEmail() {
            return confirmEmail;
        }

        public void setConfirmEmail(String confirmEmail) {
            this.confirmEmail = confirmEmail;
        }

        public Boolean getTerms() {
            return terms;
        }

        public void setTerms(Boolean terms) {
            this.terms = terms;
        }

    }

## User Registration Controller

To demonstrate a form submission, we created the `UserRegistrationController`. The `UserRegistrationDto` is validated upon form post using the `@Vali` annotation.

    package com.memorynotfound.spring.security.web;

    import com.memorynotfound.spring.security.web.dto.UserRegistrationDto;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.validation.BindingResult;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.ModelAttribute;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestMapping;

    import javax.validation.Valid;

    @Controller
    @RequestMapping("/registration")
    public class UserRegistrationController {

        @ModelAttribute("user")
        public UserRegistrationDto userRegistrationDto() {
            return new UserRegistrationDto();
        }

        @GetMapping
        public String showRegistrationForm(Model model) {
            return "registration";
        }

        @PostMapping
        public String registerUserAccount(@ModelAttribute("user") @Valid UserRegistrationDto userDto,
                                          BindingResult result){

            if (result.hasErrors()){
                return "registration";
            }

            return "redirect:/registration?success";
        }

    }

## Spring Boot

We use Spring Boot to start our application.

    package com.memorynotfound.spring.security;

    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;

    @SpringBootApplication
    public class Run {

        public static void main(String[] args) {
            SpringApplication.run(Run.class, args);
        }

    }

## User Registration Thymeleaf Template

The `registration.html` uses `bootstrap` and `jquery` and is located in the `src/main/resources/templates/` folder.

    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org">
    <head>
        <meta charset="utf-8"/>
        <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
        <meta name="viewport" content="width=device-width, initial-scale=1"/>

        <link rel="stylesheet" type="text/css" th:href="@{/webjars/bootstrap/3.3.7/css/bootstrap.min.css}"/>
        <link rel="stylesheet" type="text/css" th:href="@{/css/main.css}"/>

        <title>Registration</title>
    </head>
    <body>
    <div class="container">
        <div class="row">
            <div class="col-md-6 col-md-offset-3">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <div class="text-center">
                            <h3><i class="glyphicon glyphicon-user" style="font-size:2em;"></i></h3>
                            <h2 class="text-center">Register</h2>
                            <div class="panel-body">

                                <div th:if="${param.success}">
                                    <div class="alert alert-info">
                                        You've successfully registered to our awesome app!
                                    </div>
                                </div>

                                <form th:action="@{/registration}" th:object="${user}" method="post">

                                    <p class="error-message"
                                       th:if="${#fields.hasGlobalErrors()}"
                                       th:each="error : ${#fields.errors('global')}"
                                       th:text="${error}">Validation error</p>

                                    <div class="form-group"
                                         th:classappend="${#fields.hasErrors('firstName')}? 'has-error':''">
                                        <div class="input-group">
                                            <span class="input-group-addon">
                                                <i class="glyphicon glyphicon-user color-blue"></i>
                                            </span>
                                            <input id="firstName"
                                                   class="form-control"
                                                   placeholder="First name"
                                                   th:field="*{firstName}"/>
                                        </div>
                                        <p class="error-message"
                                           th:each="error: ${#fields.errors('firstName')}"
                                           th:text="${error}">Validation error</p>
                                    </div>
                                    <div class="form-group"
                                         th:classappend="${#fields.hasErrors('lastName')}? 'has-error':''">
                                        <div class="input-group">
                                            <span class="input-group-addon">
                                                <i class="glyphicon glyphicon-user color-blue"></i>
                                            </span>
                                            <input id="lastName"
                                                   class="form-control"
                                                   placeholder="Last name"
                                                   th:field="*{lastName}"/>
                                        </div>
                                        <p class="error-message"
                                           th:each="error: ${#fields.errors('lastName')}"
                                           th:text="${error}">Validation error</p>
                                    </div>
                                    <div class="form-group"
                                         th:classappend="${#fields.hasErrors('email')}? 'has-error':''">
                                        <div class="input-group">
                                            <span class="input-group-addon">@</span>
                                            <input id="email"
                                                   class="form-control"
                                                   placeholder="E-mail"
                                                   th:field="*{email}"/>
                                        </div>
                                        <p class="error-message"
                                           th:each="error: ${#fields.errors('email')}"
                                           th:text="${error}">Validation error</p>
                                    </div>
                                    <div class="form-group"
                                         th:classappend="${#fields.hasErrors('confirmEmail')}? 'has-error':''">
                                        <div class="input-group">
                                            <span class="input-group-addon">@</span>
                                            <input id="confirmEmail"
                                                   class="form-control"
                                                   placeholder="Confirm e-mail"
                                                   th:field="*{confirmEmail}"/>
                                        </div>
                                        <p class="error-message"
                                           th:each="error: ${#fields.errors('confirmEmail')}"
                                           th:text="${error}">Validation error</p>
                                    </div>
                                    <div class="form-group"
                                         th:classappend="${#fields.hasErrors('password')}? 'has-error':''">
                                        <div class="input-group">
                                            <span class="input-group-addon">
                                                <i class="glyphicon glyphicon-lock"></i>
                                            </span>
                                            <input id="password"
                                                   class="form-control"
                                                   placeholder="password"
                                                   type="password"
                                                   th:field="*{password}"/>
                                        </div>
                                        <ul class="text-left"
                                            th:each="error: ${#fields.errors('password')}">
                                            <li th:each="message : ${error.split(',')}">
                                                <p class="error-message"
                                                   th:text="${message}"></p>
                                            </li>
                                        </ul>
                                    </div>
                                    <div class="form-group"
                                         th:classappend="${#fields.hasErrors('confirmPassword')}? 'has-error':''">
                                        <div class="input-group">
                                            <span class="input-group-addon">
                                                <i class="glyphicon glyphicon-lock"></i>
                                            </span>
                                            <input id="confirmPassword"
                                                   class="form-control"
                                                   type="password"
                                                   placeholder="Confirm password"
                                                   th:field="*{confirmPassword}"/>
                                        </div>
                                        <ul class="text-left"
                                                th:each="error: ${#fields.errors('confirmPassword')}">
                                            <li th:each="message : ${error.split(',')}">
                                                <p class="error-message"
                                                   th:text="${message}"></p>
                                            </li>
                                        </ul>
                                    </div>
                                    <div class="form-group"
                                         th:classappend="${#fields.hasErrors('terms')}? 'has-error':''">
                                        <input id="terms"
                                               type="checkbox"
                                               th:field="*{terms}"/>  
                                        <label class="control-label" for="terms">
                                            I agree with the <a href="#">terms and conditions</a> for Registration.
                                        </label>
                                        <p class="error-message"
                                           th:each="error : ${#fields.errors('terms')}"
                                           th:text="${error}">Validation error</p>
                                    </div>
                                    <div class="form-group">
                                        <button type="submit" class="btn btn-success btn-block">Register</button>
                                    </div>
                                </form>

                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        Already registered? <a href="/" th:href="@{/login}">Login</a>
                    </div>
                    <div class="col-md-12">
                        Forgot password? <a href="/" th:href="@{/forgot-password}">Reset password</a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script type="text/javascript" th:src="@{/webjars/jquery/3.2.1/jquery.min.js/}"></script>
    <script type="text/javascript" th:src="@{/webjars/bootstrap/3.3.7/js/bootstrap.min.js}"></script>

    </body>
    </html>

## Demo

Access the `http://localhost:8080/registration` url and try it out.

<picture width="800" height="945" class="aligncenter size-full wp-image-7345 sp-no-webp"><source data-srcset="https://memorynotfound.com/wp-content/uploads/custom-password-constraint-validator-annotation-example.webp" type="image/webp"> <source data-srcset="https://memorynotfound.com/wp-content/uploads/custom-password-constraint-validator-annotation-example.png"></picture> 

## Password Validator Integration Testing

To validate if our controller is functioning properly, we created some integration tests using `spring-test` and `MockMvc`.

    package com.memorynotfound.spring.security.test;

    import org.junit.Test;
    import org.junit.runner.RunWith;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
    import org.springframework.test.web.servlet.MockMvc;

    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

    @SpringBootTest
    @AutoConfigureMockMvc
    @RunWith(SpringJUnit4ClassRunner.class)
    public class UserRegistrationIT {

        @Autowired
        private MockMvc mockMvc;

        @Test
        public void submitRegistrationPasswordNotValid() throws Exception {
            this.mockMvc
                    .perform(
                            post("/registration")
                                    .param("firstName", "Memory")
                                    .param("lastName", "Not Found")
                                    .param("email", "[email protected]")
                                    .param("confirmEmail", "[email protected]d.com")
                                    .param("password", "password")
                                    .param("confirmPassword", "password")
                                    .param("terms", "on")
                    )
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("user", "password", "confirmPassword"))
                    .andExpect(status().isOk());
        }

        @Test
        public void submitRegistrationPasswordNotMatching() throws Exception {
            this.mockMvc
                    .perform(
                            post("/registration")
                                    .param("firstName", "Memory")
                                    .param("lastName", "Not Found")
                                    .param("email", "[email protected]")
                                    .param("confirmEmail", "[email protected]")
                                    .param("password", "xjD1!3djk4")
                                    .param("confirmPassword", "xjD1!3djk3")
                                    .param("terms", "on")
                    )
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasErrors("user"))
                    .andExpect(status().isOk());
        }

        @Test
        public void submitRegistrationSuccess() throws Exception {
            this.mockMvc
                    .perform(
                            post("/registration")
                                    .param("firstName", "Memory")
                                    .param("lastName", "Not Found")
                                    .param("email", "[email protected]")
                                    .param("confirmEmail", "[email protected]")
                                    .param("password", "xjD1!3djk4")
                                    .param("confirmPassword", "xjD1!3djk4")
                                    .param("terms", "on")
                    )
                    .andExpect(model().hasNoErrors())
                    .andExpect(redirectedUrl("/registration?success"))
                    .andExpect(status().is3xxRedirection());
        }

    }

## Password Constraint Validator Unit Testing

We also wrote a `JUnit` Unit Test to validate if our `PasswordConstraintValidator` and `ValidPassword` annotation are working properly.

    package com.memorynotfound.spring.security.test;

    import com.memorynotfound.spring.security.web.dto.UserRegistrationDto;
    import org.junit.Assert;
    import org.junit.BeforeClass;
    import org.junit.Test;
    import javax.validation.ConstraintViolation;
    import javax.validation.Validation;
    import javax.validation.Validator;
    import javax.validation.ValidatorFactory;
    import java.util.Set;

    public class PasswordConstraintValidatorTest {

        private static Validator validator;

        @BeforeClass
        public static void setUp() {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        }

        @Test
        public void testInvalidPassword() {
            UserRegistrationDto userRegistration = new UserRegistrationDto();
            userRegistration.setFirstName("memory");
            userRegistration.setLastName("not found");
            userRegistration.setEmail("[email protected]");
            userRegistration.setConfirmEmail("[email protected]");
            userRegistration.setPassword("password");
            userRegistration.setConfirmPassword("password");
            userRegistration.setTerms(true);

            Set<ConstraintViolation<UserRegistrationDto>> constraintViolations = validator.validate(userRegistration);

            Assert.assertEquals(constraintViolations.size(), 2);
        }

        @Test
        public void testValidPasswords() {
            UserRegistrationDto userRegistration = new UserRegistrationDto();
            userRegistration.setFirstName("memory");
            userRegistration.setLastName("not found");
            userRegistration.setEmail("[email protected]");
            userRegistration.setConfirmEmail("[email protected]");
            userRegistration.setPassword("xJ3!dij50");
            userRegistration.setConfirmPassword("xJ3!dij50");
            userRegistration.setTerms(true);

            Set<ConstraintViolation<UserRegistrationDto>> constraintViolations = validator.validate(userRegistration);

            Assert.assertEquals(constraintViolations.size(), 0);
        }
    }

## References
*   by [MemoryNotFound](https://memorynotfound.com/author/memorynotfound/ "Posts by MemoryNotFound")· <time class="published" datetime="November 9, 2017">November 9, 2017</time>
*   [Passay Official Website](http://www.passay.org/)
*   [Passay API JavaDoc](http://www.passay.org/javadocs/)
*   [PasswordValidator JavaDoc](http://www.passay.org/javadocs/org/passay/PasswordValidator.html)