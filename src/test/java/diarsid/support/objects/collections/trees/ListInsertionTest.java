package diarsid.support.objects.collections.trees;

import java.util.ArrayList;
import java.util.List;

import diarsid.support.objects.collections.ListInsertion;
import diarsid.support.objects.collections.Lists;
import org.junit.Test;

import static java.util.Arrays.asList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ListInsertionTest {

    @Test
    public void test() {
        ListInsertion insertion = new ListInsertion(1, 2, 3);
        List<String> list = new ArrayList<>(asList("A", "B", "C"));
        Lists.insertAfterEachElement(1, 2, 3).into(list);
        assertThat(list.size(), equalTo(12));
    }
}
