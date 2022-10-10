package diarsid.support.objects.corteges;

import java.util.List;

public class Cortege10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> extends Cortege {

    public final T0 o0;
    public final T1 o1;
    public final T2 o2;
    public final T3 o3;
    public final T4 o4;
    public final T5 o5;
    public final T6 o6;
    public final T7 o7;
    public final T8 o8;
    public final T9 o9;

    public Cortege10(T0 o0, T1 o1, T2 o2, T3 o3, T4 o4, T5 o5, T6 o6, T7 o7, T8 o8, T9 o9) {
        this.o0 = o0;
        this.o1 = o1;
        this.o2 = o2;
        this.o3 = o3;
        this.o4 = o4;
        this.o5 = o5;
        this.o6 = o6;
        this.o7 = o7;
        this.o8 = o8;
        this.o9 = o9;
    }

    @Override
    public List<Object> objects() {
        return List.of(o0, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public int length() {
        return 10;
    }
}
