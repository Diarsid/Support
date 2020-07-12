package diarsid.support.strings;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.Arrays.stream;

public class MultilineMessage {

    public enum IndentLevel {

        INDENT_LEVEL_0(""),
        INDENT_LEVEL_1("    "),
        INDENT_LEVEL_2("        ");

        private String indent;

        IndentLevel(String indent) {
            this.indent = indent;
        }

        protected String string() {
            return indent;
        }
    }

    private final static String SEPARATOR = lineSeparator();

    private final StringBuilder message;
    private final String prefix;
    private final String indent;

    public MultilineMessage() {
        this.prefix = "";
        this.indent = "";
        this.message = new StringBuilder();
    }

    public MultilineMessage(String prefix) {
        this.prefix = prefix + " ";
        this.indent = "";
        this.message = new StringBuilder();
    }

    public MultilineMessage(String prefix, String indent) {
        this.prefix = prefix + " ";
        this.indent = indent;
        this.message = new StringBuilder();
    }

    public MultilineMessage(String prefix, IndentLevel indentLevel) {
        this.prefix = prefix + " ";
        this.indent = indentLevel.string();
        this.message = new StringBuilder();
    }

    public MultilineMessage newLine() {
        message.append(SEPARATOR).append(prefix);
        return this;
    }

    public MultilineMessage indent() {
        message.append(indent);
        return this;
    }

    public MultilineMessage indent(IndentLevel indentLevel) {
        message.append(indentLevel.string());
        return this;
    }

    public MultilineMessage add(String s) {
        message.append(s);
        return this;
    }

    public MultilineMessage add(boolean b) {
        message.append(b);
        return this;
    }

    public MultilineMessage add(int i) {
        message.append(i);
        return this;
    }

    public MultilineMessage add(double d) {
        message.append(d);
        return this;
    }

    public MultilineMessage add(long l) {
        message.append(l);
        return this;
    }

    public MultilineMessage add(float f) {
        message.append(f);
        return this;
    }

    public MultilineMessage add(char c) {
        message.append(c);
        return this;
    }

    public MultilineMessage add(String formatS, Object... args) {
        message.append(format(formatS, args));
        return this;
    }

    public MultilineMessage addAsLines(StackTraceElement[] stackTraceElements, int indentsLevel) {
        String indentLine = "";

        for (int i = 0; i < indentsLevel; i++) {
            indentLine = indentLine + indent;
        }

        String indents = indentLine;

        stream(stackTraceElements).forEach(element -> {
            this.newLine().add(indents).add(element.toString());
        });

        return this;
    }

    public String compose() {
        return message.toString();
    }

    @Override
    public String toString() {
        return compose();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultilineMessage)) return false;

        MultilineMessage message1 = (MultilineMessage) o;

        if (!message.toString().equals(message1.message.toString())) return false;
        if (!prefix.equals(message1.prefix)) return false;
        return indent.equals(message1.indent);
    }

    @Override
    public int hashCode() {
        int result = message.hashCode();
        result = 31 * result + prefix.hashCode();
        result = 31 * result + indent.hashCode();
        return result;
    }
}
