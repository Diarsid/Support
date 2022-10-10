package diarsid.support.objects.corteges;

import java.util.List;

public class Cortege3<T0, T1, T2> extends Cortege {

    public final T0 o0;
    public final T1 o1;
    public final T2 o2;

    public Cortege3(T0 o0, T1 o1, T2 o2) {
        this.o0 = o0;
        this.o1 = o1;
        this.o2 = o2;
    }

    @Override
    public List<Object> objects() {
        return List.of(o0, o1, o2);
    }

    @Override
    public int length() {
        return 3;
    }
}
