package diarsid.support.objects.reflection;

import java.util.Objects;

import static java.util.Objects.isNull;

public class OpenField<T> {

    public final Class<T> ownerType;
    public final Class<?> declaredType;
    public final String declaredName;
    public final String fullName;
    public final Object value;
    public final boolean inherited;

    public OpenField(Class<T> ownerType, Class<?> declaredType, String name, Object value) {
        this.ownerType = ownerType;
        this.declaredType = declaredType;
        if ( declaredType.equals(ownerType) ) {
            this.declaredName = name;
            this.fullName = name;
            this.inherited = false;
        }
        else {
            this.declaredName = name;
            this.fullName = declaredType.getSimpleName() + "." + name;
            this.inherited = true;
        }
        this.value = value;
    }

    public String valueToString() {
        if ( isNull(this.value) ) {
            return "null";
        }
        else {
            return Objects.toString(this.value);
        }
    }
}
