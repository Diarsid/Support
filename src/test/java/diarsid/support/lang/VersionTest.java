package diarsid.support.lang;


import diarsid.support.model.versioning.Version;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VersionTest {

    @Test
    public void testParseVersion() {
        Version v = new Version("10.22.33.44.-name");
        assertThat(v.name).isEqualTo("name");
        assertThat(v.fullName).isEqualTo("10.22.33.44.-name");
        assertThat(v.numbers).containsExactly(10, 22, 33, 44);
    }

    @Test
    public void testIncrementManyInts() {
        Version v = new Version("1.2.3");
        Version v2 = v.increment(1);

        assertThat(v2.fullName).isEqualTo("1.3.3");
    }

    @Test
    public void testSingleInteger() {
        Version v = new Version("1");

        assertThat(v.numbers).hasSize(1);
        assertThat(v.numbers.get(0)).isEqualTo(1);
    }

    @Test
    public void testSingleIntegerIncrement() {
        Version v = new Version("1");
        Version v2 = v.increment();

        assertThat(v2.numbers).hasSize(1);
        assertThat(v2.numbers.get(0)).isEqualTo(2);
    }

    @Test
    public void testVersionCompareHigher() {
        Version v1 = new Version("1.2.3");
        Version v2 = new Version("1.2.4");

        assertThat(v1.compareTo(v2)).isLessThan(0);
    }

    @Test
    public void testVersionCompareEqual() {
        Version v1 = new Version("1.2.3");
        Version v2 = new Version("1.2.3");

        assertThat(v1.compareTo(v2)).isEqualTo(0);
    }

    @Test
    public void testVersionCompareHavingDifferentLevels() {
        Version v1 = new Version("1.2");
        Version v2 = new Version("1.2.4");

        assertThat(v1.compareTo(v2)).isLessThan(0);
    }

    @Test
    public void testVersionCompareHavingDifferentLevelsButEssentiallySame() {
        Version v1 = new Version("1.2");
        Version v2 = new Version("1.2.0");

        assertThat(v1.compareTo(v2)).isEqualTo(0);
    }
}
