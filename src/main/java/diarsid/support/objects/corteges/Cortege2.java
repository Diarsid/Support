package diarsid.support.objects.corteges;

import java.util.List;

public class Cortege2<T0, T1> extends Cortege {

    public final T0 o0;
    public final T1 o1;

    public Cortege2(T0 o0, T1 o1) {
        this.o0 = o0;
        this.o1 = o1;
    }

    @Override
    public List<Object> objects() {
        return List.of(o0, o1);
    }

    @Override
    public int length() {
        return 2;
    }
}
