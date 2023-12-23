package diarsid.support.objects.collections;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ListsTest {

    @Test
    public void testDiff() {
        List<Integer> a = List.of(1, 2, 3, 4, 5);
        List<Integer> b = List.of(   2, 3,    5, 6);

        List<Integer> diff = Lists.diff(a, b);

        assertThat(diff).containsExactlyInAnyOrder(1, 4, 6);
    }
}
