package diarsid.support.objects.corteges;

import java.util.List;

public class Cortege6<T0, T1, T2, T3, T4, T5> extends Cortege {

    public final T0 o0;
    public final T1 o1;
    public final T2 o2;
    public final T3 o3;
    public final T4 o4;
    public final T5 o5;

    public Cortege6(T0 o0, T1 o1, T2 o2, T3 o3, T4 o4, T5 o5) {
        this.o0 = o0;
        this.o1 = o1;
        this.o2 = o2;
        this.o3 = o3;
        this.o4 = o4;
        this.o5 = o5;
    }

    @Override
    public List<Object> objects() {
        return List.of(o0, o1, o2, o3, o4, o5);
    }

    @Override
    public int length() {
        return 6;
    }
}
