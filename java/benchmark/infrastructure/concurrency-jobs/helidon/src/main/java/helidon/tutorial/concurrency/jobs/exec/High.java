package helidon.tutorial.concurrency.jobs.exec;
import jakarta.inject.Qualifier;
import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
@Qualifier @Retention(RUNTIME) @Target({FIELD, PARAMETER, METHOD, TYPE})
public @interface High {}
