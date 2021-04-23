package diarsid.support.objects.collections;

import java.util.List;
import java.util.function.Consumer;

public class ListInsertion implements Consumer<List> {

    private final Object[] extendings;

    public ListInsertion(Object... extendings) {
        this.extendings = extendings;
    }

    public void into(List list) {
        if ( list.isEmpty() ) {
            return;
        }

        int initialSize = list.size();
        int counter = 1;
        int j = 0;
        for (int i = 0; i < initialSize; i++) {
            for (; j < extendings.length; j++) {
                list.add(counter + j, extendings[j]);
            }
            j = 0;
            counter = counter + extendings.length + 1;
        }
    }

    @Override
    public void accept(List list) {
        this.into(list);
    }
}
