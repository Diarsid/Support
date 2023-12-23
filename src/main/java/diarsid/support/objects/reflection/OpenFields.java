package diarsid.support.objects.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

public class OpenFields<T> {

    private static final Logger log = LoggerFactory.getLogger(OpenFields.class);

    public final Class<T> type;
    public final List<OpenField<T>> all;
    public final boolean hasInheritedFields;

    @SuppressWarnings("unchecked")
    public OpenFields(T t) {
        this.all = new ArrayList<>();

        Class<T> tClass = (Class<T>) t.getClass();

        this.type = tClass;

        OpenField<T> openField;
        Class<?> declaredClass = tClass;
        int inheritanceLevel = -1;
        do {
            for ( Field field : declaredClass.getDeclaredFields() ) {
                field.setAccessible(true);
                if ( ! Modifier.isStatic(field.getModifiers()) ) {
                    openField = new OpenField<>(tClass, declaredClass, field.getName(), getFieldValue(field, t));
                    this.all.add(openField);
                }
            }
            declaredClass = declaredClass.getSuperclass();
            inheritanceLevel++;
        }
        while ( ! declaredClass.equals(Object.class) );

        this.hasInheritedFields = inheritanceLevel > 0;
    }

    public Object valueOf(String name) {
        return this
                .all
                .stream()
                .filter(openField -> openField.declaredName.equals(name))
                .findFirst()
                .map(openField -> openField.value)
                .orElse(null);
    }

    public Object valueOf(Class<?> declaringType, String name) {
        return this
                .all
                .stream()
                .filter(openField ->
                        openField.declaredName.equals(name) &&
                        openField.inherited &&
                        openField.declaredType.equals(declaringType))
                .findFirst()
                .map(openField -> openField.value)
                .orElse(null);
    }

    private static Object getFieldValue(Field field, Object object) {
        try {
            return field.get(object);
        }
        catch (Throwable t) {
            log.error(format("Cannot read field '%s' of %s", field.getName(), object.toString()), t);
            return "Cannot read field - " + t.getMessage();
        }
    }

}
