package tr.edu.gazi.bilisim.veri.madencik.customAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Ramazan CESUR on 25.1.2021.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataMatch {
    int index() default -1;

    boolean dataClass() default false;
}
