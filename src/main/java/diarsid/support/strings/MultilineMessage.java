package diarsid.support.strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import diarsid.support.objects.reflection.OpenField;
import diarsid.support.objects.reflection.OpenFields;
import diarsid.support.strings.split.Split;
import diarsid.support.strings.split.SplitByString;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import static diarsid.support.strings.MultilineMessage.IndentLevel.INDENT_LEVEL_1;

public class MultilineMessage {

    private static final Split SPLIT_BY_LINE_SEPARATOR = new SplitByString(lineSeparator());

    public enum IndentLevel {

        INDENT_LEVEL_0(0, ""),
        INDENT_LEVEL_1(1, "    "),
        INDENT_LEVEL_2(2, "        ");

        private String indent;
        private int level;

        IndentLevel(int level, String indent) {
            this.indent = indent;
        }

        protected String string() {
            return indent;
        }
    }

    private static class VerticalMapContent implements VerticalMap {

        private final static class Pair {

            private String key;
            private String value;

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Pair)) return false;
                Pair pair = (Pair) o;
                return Objects.equals(key, pair.key) &&
                        Objects.equals(value, pair.value);
            }

            @Override
            public int hashCode() {
                return Objects.hash(key, value);
            }

            @Override
            public String toString() {
                return "[" +
                        "key='" + key + '\'' +
                        ", value='" + value + '\'' +
                        ']';
            }
        }

        private final MultilineMessage message;
        private final int position;
        private final String separator;
        private final int indentLevel;
        private final List<Pair> pairs;

        private Pair current;

        private int longestKey = 0;
        private int longestValue = 0;

        public VerticalMapContent(MultilineMessage message, int position, String separator, int indentLevel) {
            this.message = message;
            this.position = position;
            this.separator = separator;
            this.indentLevel = indentLevel;
            this.pairs = new ArrayList<>();
        }

        private void newPair() {
            if ( nonNull(this.current) ) {
                this.pairs.add(this.current);
            }
            this.current = new Pair();
        }

        private void setLongestKey() {
            int newLength;

            if ( isNull(this.current.key) ) {
                newLength = 4;
            }
            else {
                newLength = this.current.key.length();
            }

            if ( newLength > this.longestKey ) {
                this.longestKey = newLength;
            }
        }

        @Override
        public VerticalMap key(String s) {
            this.newPair();
            this.current.key = isNull(s) ? "null" : s;
            this.setLongestKey();
            return this;
        }

        @Override
        public VerticalMap key(boolean b) {
            this.newPair();
            this.current.key = String.valueOf(b);
            this.setLongestKey();
            return this;
        }

        @Override
        public VerticalMap key(int i) {
            this.newPair();
            this.current.key = String.valueOf(i);
            this.setLongestKey();
            return this;
        }

        @Override
        public VerticalMap key(double d) {
            this.newPair();
            this.current.key = String.valueOf(d);
            this.setLongestKey();
            return this;
        }

        @Override
        public VerticalMap key(long l) {
            this.newPair();
            this.current.key = String.valueOf(l);
            this.setLongestKey();
            return this;
        }

        @Override
        public VerticalMap key(float f) {
            this.newPair();
            this.current.key = String.valueOf(f);
            this.setLongestKey();
            return this;
        }

        @Override
        public VerticalMap key(char c) {
            this.newPair();
            this.current.key = String.valueOf(c);
            this.setLongestKey();
            return this;
        }

        @Override
        public VerticalMap key(UUID uuid) {
            this.newPair();
            this.current.key = isNull(uuid) ? "null" : uuid.toString();
            this.setLongestKey();
            return this;
        }

        @Override
        public VerticalMap key(String formatS, Object... args) {
            this.newPair();
            this.current.key = format(formatS, args);
            this.setLongestKey();
            return this;
        }

        private void newPairIfNull() {
            if ( isNull(this.current) ) {
                this.current = new Pair();
            }
        }

        private void setLongestValue() {
            int newLength;

            if ( isNull(this.current.value) ) {
                newLength = 4;
            }
            else {
                newLength = this.current.value.length();
            }

            if ( newLength > this.longestValue ) {
                this.longestValue = newLength;
            }
        }

        @Override
        public VerticalMap value(String s) {
            this.newPairIfNull();
            this.current.value = isNull(s) ? "null" : s;
            this.setLongestValue();
            return this;
        }

        @Override
        public VerticalMap value(boolean b) {
            this.newPairIfNull();
            this.current.value = String.valueOf(b);
            this.setLongestValue();
            return this;
        }

        @Override
        public VerticalMap value(int i) {
            this.newPairIfNull();
            this.current.value = String.valueOf(i);
            this.setLongestValue();
            return this;
        }

        @Override
        public VerticalMap value(double d) {
            this.newPairIfNull();
            this.current.value = String.valueOf(d);
            this.setLongestValue();
            return this;
        }

        @Override
        public VerticalMap value(long l) {
            this.newPairIfNull();
            this.current.value = String.valueOf(l);
            this.setLongestValue();
            return this;
        }

        @Override
        public VerticalMap value(float f) {
            this.newPairIfNull();
            this.current.value = String.valueOf(f);
            this.setLongestValue();
            return this;
        }

        @Override
        public VerticalMap value(char c) {
            this.newPairIfNull();
            this.current.value = String.valueOf(c);
            this.setLongestValue();
            return this;
        }

        @Override
        public VerticalMap value(UUID uuid) {
            this.newPairIfNull();
            this.current.value = isNull(uuid) ? "null" : uuid.toString();
            this.setLongestValue();
            return this;
        }

        @Override
        public VerticalMap value(String formatS, Object... args) {
            this.newPairIfNull();
            this.current.value = format(formatS, args);
            this.setLongestValue();
            return this;
        }

        @Override
        public MultilineMessage ends() {
            if ( isNull(this.message.structures) ) {
                this.message.structures = new ArrayList<>();
            }

            if ( nonNull(this.current) ) {
                this.pairs.add(this.current);
            }

            this.message.structures.add(this);
            this.message.map = null;

            return this.message;
        }
    }

    private static class TableImpl implements Table {

        private static class LineImpl implements Line {

            private static class CellImpl implements Cell {

                private final LineImpl line;
                private final String name;
                private final int index;
                private final StringBuilder value;

                public CellImpl(LineImpl line, String name, int index) {
                    this.line = line;
                    this.name = name;
                    this.index = index;
                    this.value = new StringBuilder();
                }

                @Override
                public String name() {
                    return name;
                }

                @Override
                public int index() {
                    return index;
                }

                @Override
                public Cell add(String s) {
                    this.value.append(s);
                    return this;
                }

                @Override
                public Cell add(boolean b) {
                    this.value.append(b);
                    return this;
                }

                @Override
                public Cell add(int i) {
                    this.value.append(i);
                    return this;
                }

                @Override
                public Cell add(double d) {
                    this.value.append(d);
                    return this;
                }

                @Override
                public Cell add(long l) {
                    this.value.append(l);
                    return this;
                }

                @Override
                public Cell add(float f) {
                    this.value.append(f);
                    return this;
                }

                @Override
                public Cell add(char c) {
                    this.value.append(c);
                    return this;
                }

                @Override
                public Cell add(UUID uuid) {
                    this.value.append(uuid.toString());
                    return this;
                }

                @Override
                public Cell add(String formatS, Object... args) {
                    this.value.append(format(formatS, args));
                    return this;
                }

                @Override
                public Cell next() {
                    return line.nextCell();
                }

                @Override
                public Line line() {
                    return line;
                }

                @Override
                public boolean hasNext() {
                    return line.hasNextCell();
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (!(o instanceof CellImpl)) return false;
                    CellImpl cell = (CellImpl) o;
                    return index == cell.index &&
                            line.equals(cell.line) &&
                            Objects.equals(name, cell.name);
                }

                @Override
                public int hashCode() {
                    return Objects.hash(line, name, index);
                }

                @Override
                public String toString() {
                    return "[" +
                            value.toString() +
                            ']';
                }
            }

            private final TableImpl table;
            private final int index;
            private final List<CellImpl> cells;
            private CellImpl currentCell;

            public LineImpl(TableImpl table, int index) {
                this.table = table;
                this.index = index;
                this.cells = new ArrayList<>(table.columns);

                for ( int i = 0; i < table.columns; i++ ) {
                    this.cells.add(null);
                }
            }

            private boolean hasNextCell() {
                if ( isNull(currentCell) ) {
                    return true;
                }

                return currentCell.index < table.columns-1;
            }

            @Override
            public CellImpl nextCell() {
                if ( ! hasNextCell() ) {
                    throw new IndexOutOfBoundsException();
                }

                if ( isNull(currentCell) ) {
                    currentCell = new CellImpl(this, cellNameOrNull(0), 0);
                    cells.set(0, currentCell);
                    return currentCell;
                }
                else {
                    int cellIndex = currentCell.index + 1;
                    currentCell = new CellImpl(this, cellNameOrNull(cellIndex), cellIndex);
                    cells.set(cellIndex, currentCell);
                    return currentCell;
                }
            }

            @Override
            public Line add(List<?> cellValues) {
                Cell cell;
                Object cellValue;
                for ( int i = 0; i < cellValues.size() && i < cells.size(); i++ ) {
                    cellValue = cellValues.get(i);
                    cell = this.cell(i);

                    if ( isNull(cellValue) ) {
                        cell.add("null");
                    }
                    else {
                        cell.add(cellValue.toString());
                    }
                }
                return this;
            }

            @Override
            public int index() {
                return index;
            }

            @Override
            public Cell cell(String name) {
                if ( isNull(table.columnNames) ) {
                    throw new IllegalArgumentException("This table doesn't have column names!");
                }

                int cellIndex = this.table.columnNames.indexOf(name);

                if ( cellIndex < 0 ) {
                    throw new IllegalArgumentException("This table doesn't have column with this name!");
                }

                currentCell = cells.get(cellIndex);

                if ( isNull(currentCell) ) {
                    currentCell = new CellImpl(this, cellNameOrNull(cellIndex), cellIndex);
                    cells.set(cellIndex, currentCell);
                }

                return currentCell;
            }

            private String cellNameOrNull(int cellIndex) {
                if ( isNull(table.columnNames) ) {
                    return null;
                }

                return table.columnNames.get(cellIndex);
            }

            @Override
            public Cell cell(int cellIndex) {
                if ( cellIndex < 0 || cellIndex >= table.columns ) {
                    throw new IndexOutOfBoundsException();
                }

                currentCell = cells.get(cellIndex);

                if ( isNull(currentCell) ) {
                    currentCell = new CellImpl(this, cellNameOrNull(cellIndex), cellIndex);
                    cells.set(cellIndex, currentCell);
                }

                return currentCell;
            }

            @Override
            public Cell cell() {
                if ( isNull(currentCell) ) {
                    currentCell = new CellImpl(this, cellNameOrNull(0), 0);
                    cells.set(0, currentCell);
                }

                return currentCell;
            }

            @Override
            public Table ends() {
                if ( nonNull(table.currentLine) ) {
                    table.lines.add(table.currentLine);
                    table.calculateColumnLongest();
                    table.currentLine = null;
                }

                return table;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof LineImpl)) return false;
                LineImpl line = (LineImpl) o;
                return index == line.index &&
                        table.equals(line.table);
            }

            @Override
            public int hashCode() {
                return Objects.hash(table, index);
            }

            @Override
            public String toString() {
                return "[" +
                        index +
                        ", " + cells +
                        ']';
            }
        }

        private final MultilineMessage message;
        private final int position;
        private final int indentLevel;
        private final List<String> columnNames;
        private final int columns;
        private final List<LineImpl> lines;
        private final List<Integer> longest;
        private String columnsSeparator;
        private LineImpl currentLine;

        public TableImpl(MultilineMessage message, int position, int indentLevel, List<String> columnNames) {
            this.message = message;
            this.position = position;
            this.indentLevel = indentLevel;
            this.columnNames = columnNames;
            this.columns = columnNames.size();
            this.longest = new ArrayList<>(this.columns);
            this.lines = new ArrayList<>();
            this.columnsSeparator = TABLE_DEFAULT_SEPARATOR;

            for ( int i = 0; i < this.columnNames.size(); i++ ) {
                this.longest.add(this.columnNames.get(i).length());
            }
        }

        public TableImpl(MultilineMessage message, int position, int indentLevel, int columns) {
            this.message = message;
            this.position = position;
            this.indentLevel = indentLevel;
            this.columnNames = null;
            this.columns = columns;
            this.longest = new ArrayList<>(this.columns);
            this.lines = new ArrayList<>();
            this.columnsSeparator = TABLE_DEFAULT_SEPARATOR;

            for ( int i = 0; i < this.columns; i++ ) {
                this.longest.add(0);
            }
        }

        @Override
        public <T> Table addAsLines(List<T> list, BiConsumer<T, Line> writeObjectToLine) {
            Line line;
            T t;

            for ( int i = 0; i < list.size(); i++ ) {
                line = this.line();
                t = list.get(i);
                writeObjectToLine.accept(t, line);
                if ( nonNull(this.currentLine) ) {
                    line.ends();
                }
            }

            return this;
        }

        @Override
        public <T> Table addAsCellValues(List<T> list, Function<T, List<?>> objectToColumnValues) {
            Line line;
            T t;
            List<?> cellValues;

            for ( int i = 0; i < list.size(); i++ ) {
                line = this.line();
                t = list.get(i);
                cellValues = objectToColumnValues.apply(t);
                line.add(cellValues);
                line.ends();
            }

            return this;
        }

        @Override
        public Line line() {
            if ( isNull(currentLine) ) {
                if ( lines.isEmpty() ) {
                    currentLine = new LineImpl(this, 0);
                }
                else {
                    currentLine = new LineImpl(this, lines.size());
                }
            }

            return currentLine;
        }

        @Override
        public Table columnsSeparator(String separator) {
            this.columnsSeparator = separator;
            return this;
        }

        @Override
        public MultilineMessage ends() {
            if ( nonNull(currentLine) ) {
                lines.add(currentLine);
                calculateColumnLongest();
                currentLine = null;
            }

            if ( isNull(message.structures) ) {
                message.structures = new ArrayList<>();
            }

            message.structures.add(this);
            message.table = null;

            return message;
        }

        private void calculateColumnLongest() {
            LineImpl lastLine = this.lines.get(this.lines.size()-1);

            LineImpl.CellImpl cell;
            int longest;
            for ( int i = 0; i < this.columns; i++ ) {
                cell = lastLine.cells.get(i);
                longest = this.longest.get(i);

                if ( nonNull(cell) ) {
                    if ( cell.value.length() > 0 ) {
                        if ( cell.value.length() > longest ) {
                            this.longest.set(i, cell.value.length());
                        }
                    }
                }
                else {
                    if ( 4 > longest ) {
                        this.longest.set(i, 4);
                    }
                }
            }
        }
    }

    public static interface VerticalMap {

        VerticalMap key(String s);

        VerticalMap key(boolean b);

        VerticalMap key(int i);

        VerticalMap key(double d);

        VerticalMap key(long l);

        VerticalMap key(float f);

        VerticalMap key(char c);

        VerticalMap key(UUID uuid);

        VerticalMap key(String formatS, Object... args);

        VerticalMap value(String s);

        VerticalMap value(boolean b);

        VerticalMap value(int i);

        VerticalMap value(double d);

        VerticalMap value(long l);

        VerticalMap value(float f);

        VerticalMap value(char c);

        VerticalMap value(UUID uuid);

        VerticalMap value(String formatS, Object... args);

        MultilineMessage ends();
    }

    public static interface Table {

        public static interface Line {

            int index();

            Cell cell(String name);

            Cell cell(int i);

            /*
             * points to 'current' cell. If there is no current cell yet, set pointer at [0] cell of line
             */
            Cell cell();

            Cell nextCell();

            Line add(List<?> cellValues);

            Table ends();
        }

        public static interface Cell {

            String name();

            int index();

            Cell add(String s);

            Cell add(boolean b);

            Cell add(int i);

            Cell add(double d);

            Cell add(long l);

            Cell add(float f);

            Cell add(char c);

            Cell add(UUID uuid);

            Cell add(String formatS, Object... args);

            Line line();

            Cell next();

            boolean hasNext();
        }
        
        <T> Table addAsLines(List<T> ts, BiConsumer<T, Line> writeObjectToLine);

        <T> Table addAsCellValues(List<T> ts, Function<T, List<?>> objectToColumnValues);

        Line line();

        Table columnsSeparator(String separator);

        MultilineMessage ends();
    }

    private static final String SEPARATOR = lineSeparator();
    private static final String VERTICAL_MAP_DEFAULT_SEPARATOR = " : ";
    private static final String TABLE_DEFAULT_SEPARATOR = " | ";

    private final StringBuilder message;
    private final String prefix;
    private final String indent;


    private List<Object> structures;

    private TableImpl table;
    private VerticalMapContent map;

    public MultilineMessage() {
        this.prefix = "";
        this.indent = INDENT_LEVEL_1.indent;
        this.message = new StringBuilder();
    }

    public MultilineMessage(String prefix) {
        this.prefix = prefix + " ";
        this.indent = INDENT_LEVEL_1.indent;
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

    private static String indentStringOfLevel(String indent, int level) {
        String indents;
        if ( level == 0 ) {
            indents = "";
        }
        else {
            indents = indent.repeat(level);
        }

        return indents;
    }

    public MultilineMessage newLine() {
        message.append(SEPARATOR).append(prefix);
        return this;
    }

    public MultilineMessage indent() {
        message.append(indent);
        return this;
    }

    public MultilineMessage indent(int level) {
        message.append(indentStringOfLevel(this.indent, level));
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

    public MultilineMessage add(UUID uuid) {
        message.append(uuid.toString());
        return this;
    }

    public MultilineMessage add(String formatS, Object... args) {
        message.append(format(formatS, args));
        return this;
    }

    public MultilineMessage addAsLines(StackTraceElement[] stackTraceElements, int indentsLevel) {
        String indents = indentStringOfLevel(this.indent, indentsLevel);

        stream(stackTraceElements).forEach(element -> {
            this.newLine().add(indents).add(element.toString());
        });

        return this;
    }

    public MultilineMessage addAsLines(List<String> strings) {
        return this;
    }

    public MultilineMessage addAsLines(List<String> strings, int indentsLevel) {
        String indents = indentStringOfLevel(this.indent, indentsLevel);

        strings.forEach(string -> this.newLine().add(indents).add(string));

        return this;
    }

    public <T> MultilineMessage addAsTable(List<T> objects) {
        if ( isNull(objects) || objects.isEmpty() ) {
            return this;
        }

        List<OpenFields<T>> all = new ArrayList<>();
        for ( T t : objects ) {
            all.add(new OpenFields<>(t));
        }

        List<String> fieldNames;
        OpenFields<T> first = all.get(0);
        fieldNames = first
                .all
                .stream()
                .map(openField -> openField.fullName)
                .collect(toList());

        this.newLine().add(first.type.getSimpleName());
        Table table = this.startTable(fieldNames);
        Table.Line line;
        for ( OpenFields<T> fields : all ) {
            line = table.line();
            for ( OpenField<T> field : fields.all ) {
                line.cell(field.fullName).add(field.valueToString());
            }
            line.ends();
        }
        table.ends();
        return this;
    }

    public VerticalMap startVerticalMap() {
        return startVerticalMap(VERTICAL_MAP_DEFAULT_SEPARATOR);
    }

    public VerticalMap startVerticalMap(int indentLevel) {
        return startVerticalMap(indentLevel, VERTICAL_MAP_DEFAULT_SEPARATOR);
    }

    public VerticalMap startVerticalMap(IndentLevel indentLevel) {
        return startVerticalMap(indentLevel, VERTICAL_MAP_DEFAULT_SEPARATOR);
    }

    private void addPreviousUnfinishedMapIfAny() {
        if ( nonNull(this.map) ) {
            if ( isNull(this.structures) ) {
                this.structures = new ArrayList<>();
            }
            this.structures.add(this.map);
        }
        this.map = null;
    }

    public VerticalMap startVerticalMap(String separator) {
        this.addPreviousUnfinishedMapIfAny();
        this.addPreviousUnfinishedTableIfAny();
        this.map = new VerticalMapContent(this, this.message.length(), separator, 0);
        return this.map;
    }

    public VerticalMap startVerticalMap(int indentLevel, String separator) {
        this.addPreviousUnfinishedMapIfAny();
        this.addPreviousUnfinishedTableIfAny();
        this.map = new VerticalMapContent(this, this.message.length(), separator, indentLevel);
        return this.map;
    }

    public VerticalMap startVerticalMap(IndentLevel indentLevel, String separator) {
        this.addPreviousUnfinishedMapIfAny();
        this.addPreviousUnfinishedTableIfAny();
        this.map = new VerticalMapContent(this, this.message.length(), separator, indentLevel.level);
        return this.map;
    }

    public VerticalMap verticalMap() {
        if ( isNull(this.map) ) {
            this.startVerticalMap();
        }
        return this.map;
    }

    private boolean hasStartedVerticalMap() {
        return nonNull(this.map);
    }

    private void addPreviousUnfinishedTableIfAny() {
        if ( nonNull(this.table) ) {
            if ( isNull(this.structures) ) {
                this.structures = new ArrayList<>();
            }

            this.structures.add(this.table);

            if ( nonNull(this.table.currentLine) ) {
                this.table.lines.add(this.table.currentLine);
                this.table.calculateColumnLongest();
                this.table.currentLine = null;
            }
        }
        this.table = null;
    }

    public Table startTable(int columns) {
        this.addPreviousUnfinishedMapIfAny();
        this.addPreviousUnfinishedTableIfAny();
        this.table = new TableImpl(this, this.message.length(), 0, columns);
        return this.table;
    }

    public Table startTable(int indentLevel, int columns) {
        this.addPreviousUnfinishedMapIfAny();
        this.addPreviousUnfinishedTableIfAny();
        this.table = new TableImpl(this, this.message.length(), indentLevel, columns);
        return this.table;
    }

    public Table startTable(IndentLevel indentLevel, int columns) {
        this.addPreviousUnfinishedMapIfAny();
        this.addPreviousUnfinishedTableIfAny();
        this.table = new TableImpl(this, this.message.length(), indentLevel.level, columns);
        return this.table;
    }

    public Table startTable(List<String> columns) {
        this.addPreviousUnfinishedMapIfAny();
        this.addPreviousUnfinishedTableIfAny();
        this.table = new TableImpl(this, this.message.length(), 0, columns);
        return this.table;
    }

    public Table startTable(String column0, String... columns) {
        var columnsList = new ArrayList<>(asList(columns));
        columnsList.add(0, column0);
        return this.startTable(columnsList);
    }

    public Table startTable(int indentLevel, String column0, String... columns) {
        this.addPreviousUnfinishedMapIfAny();
        this.addPreviousUnfinishedTableIfAny();
        var columnsList = new ArrayList<>(asList(columns));
        columnsList.add(0, column0);
        this.table = new TableImpl(this, this.message.length(), indentLevel, columnsList);
        return this.table;
    }

    public Table startTable(IndentLevel indentLevel, String column0, String... columns) {
        this.addPreviousUnfinishedMapIfAny();
        this.addPreviousUnfinishedTableIfAny();
        var columnsList = new ArrayList<>(asList(columns));
        columnsList.add(0, column0);
        this.table = new TableImpl(this, this.message.length(), indentLevel.level, columnsList);
        return this.table;
    }

    public Table table() {
        if ( isNull(table) ) {
            throw new NullPointerException("There is no started table!");
        }
        return table;
    }

    public boolean hasStartedTable() {
        return nonNull(table);
    }

    public String compose() {
        this.addPreviousUnfinishedMapIfAny();
        this.addPreviousUnfinishedTableIfAny();

        int offsetCumulative = 0;
        StringBuilder composing = new StringBuilder();
        String composed;

        if ( nonNull(this.structures) ) {

            VerticalMapContent map;
            TableImpl table;
            for ( Object structure : this.structures ) {
                if ( structure instanceof VerticalMap ) {
                    map = (VerticalMapContent) structure;

                    int spaceToMapSeparator;
                    for ( VerticalMapContent.Pair pair : map.pairs ) {
                        spaceToMapSeparator = map.longestKey - (isNull(pair.key) ? 4 : pair.key.length());

                        composing
                                .append(SEPARATOR)
                                .append(prefix)
                                .append(indentStringOfLevel(indent, map.indentLevel))
                                .append(pair.key);

                        if ( spaceToMapSeparator > 0 ) {
                            composing.append(" ".repeat(spaceToMapSeparator));
                        }

                        composing.append(map.separator).append(pair.value);
                    }

                    composed = composing.toString();
                    message.insert(map.position + offsetCumulative, composed);
                    offsetCumulative = offsetCumulative + composed.length();
                    composing.delete(0, composing.length());
                }
                else if ( structure instanceof Table ) {
                    table = (TableImpl) structure;
                    int longestI = lengthOf(table.lines.size());

                    if ( nonNull(table.columnNames) ) {
                        composing
                                .append(SEPARATOR)
                                .append(prefix)
                                .append(indentStringOfLevel(indent, table.indentLevel))
                                .append(table.columnsSeparator)
                                .append("N")
                                .append(table.columnsSeparator);

                        String name;
                        int spaceToNextColumn;
                        for ( int i = 0; i < table.columns; i++ ) {
                            name = table.columnNames.get(i);
                            spaceToNextColumn = table.longest.get(i) - name.length();

                            composing
                                    .append(name)
                                    .append(" ".repeat(spaceToNextColumn))
                                    .append(table.columnsSeparator);
                        }
                    }

                    for ( TableImpl.LineImpl line : table.lines ) {
                        composing
                                .append(SEPARATOR)
                                .append(prefix)
                                .append(indentStringOfLevel(indent, table.indentLevel));

                        int spaceToNextColumn;

                        composing.append(table.columnsSeparator)
                                .append(line.index)
                                .append(table.columnsSeparator);

                        spaceToNextColumn = longestI - lengthOf(line.index);
                        if ( spaceToNextColumn > 0 ) {
                            composing.append(" ".repeat(spaceToNextColumn));
                        }

                        TableImpl.LineImpl.CellImpl cell;
                        String cellValue;
                        for ( int i = 0; i < table.columns; i++ ) {
                            cell = line.cells.get(i);
                            if ( nonNull(cell) ) {
                                cellValue = cell.value.toString();
                            }
                            else {
                                cellValue = "null";
                            }
                            spaceToNextColumn = table.longest.get(i) - cellValue.length();

                            composing
                                    .append(cellValue)
                                    .append(" ".repeat(spaceToNextColumn))
                                    .append(table.columnsSeparator);
                        }
                    }

                    composed = composing.toString();
                    message.insert(table.position + offsetCumulative, composed);
                    offsetCumulative = offsetCumulative + composed.length();
                    composing.delete(0, composing.length());
                }
            }
        }

        return message.toString();
    }

    public static int lengthOf(int number) {
        if (number < 100000) {
            if (number < 100) {
                if (number < 10) {
                    return 1;
                }
                else {
                    return 2;
                }
            }
            else {
                if (number < 1000) {
                    return 3;
                }
                else {
                    if (number < 10000) {
                        return 4;
                    }
                    else {
                        return 5;
                    }
                }
            }
        }
        else {
            if (number < 10000000) {
                if (number < 1000000) {
                    return 6;
                }
                else {
                    return 7;
                }
            }
            else {
                if (number < 100000000) {
                    return 8;
                }
                else {
                    if (number < 1000000000) {
                        return 9;
                    }
                    else {
                        return 10;
                    }
                }
            }
        }
    }

    public List<String> composeToLines() {
        String source = this.compose();

        return SPLIT_BY_LINE_SEPARATOR.process(source);
    }

    @Override
    public String toString() {
//        return compose();
        return "...";
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
