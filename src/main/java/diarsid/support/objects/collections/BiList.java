package diarsid.support.objects.collections;

public interface BiList<A, B> {

    A getFirst(int i);

    B getSecond(int i);

    A setFirst(int i, A a);

    B setSecond(int i, B b);

    void set(int i, A a, B b);

    int size();


}
