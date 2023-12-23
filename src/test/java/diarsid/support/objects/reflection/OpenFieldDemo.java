package diarsid.support.objects.reflection;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class OpenFieldDemo {

    public static void main(String[] args) {
        C c = new C();
        c.i = 1;
        c.is = true;
        c.list = List.of(1, 2);
        ((B) c).list = List.of("aaa", "bbb");
        c.name = "name";
        c.number = new AtomicReference<>(new BigDecimal("1.2"));

        OpenFields<C> fields = new OpenFields<>(c);
    }
}
