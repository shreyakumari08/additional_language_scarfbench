package micronaut.tutorial.guessnumber.config;
import jakarta.inject.Qualifier;
import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
@Qualifier @Retention(RUNTIME) @Target({FIELD, METHOD, PARAMETER, TYPE})
public @interface MaxNumber {}
