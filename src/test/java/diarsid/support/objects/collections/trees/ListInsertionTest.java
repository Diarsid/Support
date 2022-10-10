package diarsid.support.objects.collections.trees;

import java.util.ArrayList;
import java.util.List;

import diarsid.support.objects.collections.ListInsertion;
import diarsid.support.objects.collections.Lists;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;

import static org.assertj.core.api.Assertions.assertThat;

public class ListInsertionTest {

    @Test
    public void test() {
        ListInsertion insertion = new ListInsertion(1, 2, 3);
        List<String> list = new ArrayList<>(asList("A", "B", "C"));
        Lists.insertAfterEachElement(1, 2, 3).into(list);
        assertThat(list).hasSize(12);
    }
}
