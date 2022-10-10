package diarsid.support.strings;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringTemplateTest {

    @Test
    public void testNamedValues() {
        var template = new StringTemplate("login=[name], pass=[pass]", new StringTemplate.Placeholder.Named());
        var values = template.getValuesFrom("login=aaa, pass=bbb");

        assertThat(values.quantity()).isEqualTo(2);
        
        assertThat(values.valueOf("name")).isEqualTo("aaa");
        assertThat(values.valueOf("pass")).isEqualTo("bbb");

        assertThat(values.valueAt(0)).isEqualTo("aaa");
        assertThat(values.valueAt(1)).isEqualTo("bbb");
    }

    @Test
    public void testNamedValuesPlaceholderAtStart() {
        var template = new StringTemplate("[user] login=[name], pass=[pass]", new StringTemplate.Placeholder.Named());
        var values = template.getValuesFrom("JohnDoe login=aaa, pass=bbb");

        assertThat(values.quantity()).isEqualTo(3);

        assertThat(values.valueOf("user")).isEqualTo("JohnDoe");
        assertThat(values.valueOf("name")).isEqualTo("aaa");
        assertThat(values.valueOf("pass")).isEqualTo("bbb");

        assertThat(values.valueAt(0)).isEqualTo("JohnDoe");
        assertThat(values.valueAt(1)).isEqualTo("aaa");
        assertThat(values.valueAt(2)).isEqualTo("bbb");
    }

    @Test
    public void testNamedValuesPlaceholderAtEnd() {
        var template = new StringTemplate("login=[name], pass=[pass] [user]", new StringTemplate.Placeholder.Named());
        var values = template.getValuesFrom("login=aaa, pass=bbb JohnDoe");

        assertThat(values.quantity()).isEqualTo(3);

        assertThat(values.valueOf("user")).isEqualTo("JohnDoe");
        assertThat(values.valueOf("name")).isEqualTo("aaa");
        assertThat(values.valueOf("pass")).isEqualTo("bbb");

        assertThat(values.valueAt(0)).isEqualTo("aaa");
        assertThat(values.valueAt(1)).isEqualTo("bbb");
        assertThat(values.valueAt(2)).isEqualTo("JohnDoe");
    }

    @Test
    public void testUnnamedValues() {
        var template = new StringTemplate("login=_, pass=_", new StringTemplate.Placeholder.Unnamed());
        var values = template.getValuesFrom("login=aaa, pass=bbb");

        assertThat(values.quantity()).isEqualTo(2);

        assertThat(values.valueAt(0)).isEqualTo("aaa");
        assertThat(values.valueAt(1)).isEqualTo("bbb");
    }

    @Test
    public void testUnnamedValuesPlaceholderAtStart() {
        var template = new StringTemplate("_ login=_, pass=_", new StringTemplate.Placeholder.Unnamed());
        var values = template.getValuesFrom("JohnDoe login=aaa, pass=bbb");

        assertThat(values.quantity()).isEqualTo(3);

        assertThat(values.valueAt(0)).isEqualTo("JohnDoe");
        assertThat(values.valueAt(1)).isEqualTo("aaa");
        assertThat(values.valueAt(2)).isEqualTo("bbb");
    }

    @Test
    public void testUnnamedValuesPlaceholderAtEnd() {
        var template = new StringTemplate("login=_, pass=_ _", new StringTemplate.Placeholder.Unnamed());
        var values = template.getValuesFrom("login=aaa, pass=bbb JohnDoe");

        assertThat(values.quantity()).isEqualTo(3);

        assertThat(values.valueAt(0)).isEqualTo("aaa");
        assertThat(values.valueAt(1)).isEqualTo("bbb");
        assertThat(values.valueAt(2)).isEqualTo("JohnDoe");
    }

    @Test
    public void testCustomUnnamedValues() {
        var template = new StringTemplate("login=___, pass=___", new StringTemplate.Placeholder.Unnamed("___"));
        var values = template.getValuesFrom("login=aaa, pass=bbb");

        assertThat(values.quantity()).isEqualTo(2);

        assertThat(values.valueAt(0)).isEqualTo("aaa");
        assertThat(values.valueAt(1)).isEqualTo("bbb");
    }

    @Test
    public void testCustomUnnamedValuesPlaceholderAtStart() {
        var template = new StringTemplate("___ login=___, pass=___", new StringTemplate.Placeholder.Unnamed("___"));
        var values = template.getValuesFrom("JohnDoe login=aaa, pass=bbb");

        assertThat(values.quantity()).isEqualTo(3);

        assertThat(values.valueAt(0)).isEqualTo("JohnDoe");
        assertThat(values.valueAt(1)).isEqualTo("aaa");
        assertThat(values.valueAt(2)).isEqualTo("bbb");
    }

    @Test
    public void testCustomUnnamedValuesPlaceholderAtEnd() {
        var template = new StringTemplate("login=___, pass=___ ___", new StringTemplate.Placeholder.Unnamed("___"));
        var values = template.getValuesFrom("login=aaa, pass=bbb JohnDoe");

        assertThat(values.quantity()).isEqualTo(3);

        assertThat(values.valueAt(0)).isEqualTo("aaa");
        assertThat(values.valueAt(1)).isEqualTo("bbb");
        assertThat(values.valueAt(2)).isEqualTo("JohnDoe");
    }

    @Test
    public void testNamedFillingByNames() {
        var template = new StringTemplate("[user] login=[name], pass=[pass]", new StringTemplate.Placeholder.Named());

        String result = template
                .fill()
                .value("pass", "bbb")
                .value("name", "aaa")
                .value("user", "JohnDoe")
                .complete();

        assertThat(result).isEqualTo("JohnDoe login=aaa, pass=bbb");
    }

    @Test
    public void testNamedFillingByNamesNotFull() {
        var template = new StringTemplate("[user] login=[name], pass=[pass]", new StringTemplate.Placeholder.Named());

        String result = template
                .fill()
                .value("pass", "bbb")
                // .value("name", "aaa") omitted, expected to be inserted as "null"
                .value("user", "JohnDoe")
                .complete();

        assertThat(result).isEqualTo("JohnDoe login=null, pass=bbb");
    }

    @Test
    public void testNamedFillingByIndexesRandomOrder() {
        var template = new StringTemplate("[user] login=[name], pass=[pass]", new StringTemplate.Placeholder.Named());

        String result = template
                .fill()
                .value(2, "bbb")
                .value(1, "aaa")
                .value(0, "JohnDoe")
                .complete();

        assertThat(result).isEqualTo("JohnDoe login=aaa, pass=bbb");
    }

    @Test
    public void testNamedFillingByNaturalOrder() {
        var template = new StringTemplate("[user] login=[name], pass=[pass]", new StringTemplate.Placeholder.Named());

        String result = template
                .fill()
                .value("JohnDoe")
                .value("aaa")
                .value("bbb")
                .complete();

        assertThat(result).isEqualTo("JohnDoe login=aaa, pass=bbb");
    }

    @Test
    public void testNamedFillingFromValues() {
        var template = new StringTemplate("[user] login=[name], pass=[pass]", new StringTemplate.Placeholder.Named());

        String result = template
                .getValuesFrom("JohnDoe login=aaa, pass=bbb")
                .useForFill()
                .value("name", "REPLACED")
                .complete();

        assertThat(result).isEqualTo("JohnDoe login=REPLACED, pass=bbb");
    }
}
