package diarsid.support.strings;

import diarsid.support.exceptions.UnsupportedLogicException;

import java.util.ArrayList;
import java.util.List;

import static diarsid.support.objects.collections.Lists.fillNulls;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

public class StringTemplate {

    public static class Placeholder {

        public static class Unnamed extends Placeholder {

            public static final String DEFAULT = "_";

            private final String string;

            public Unnamed(String placeholder) {
                this.string = placeholder;
            }

            public Unnamed() {
                this.string = DEFAULT;
            }
        }

        public static class Named extends Placeholder {

            public static final char DEFAULT_START = '[';
            public static final char DEFAULT_END = ']';

            private final String startChar;
            private final String endChar;

            public Named(char startChar, char endChar) {
                this.startChar = String.valueOf(startChar);
                this.endChar = String.valueOf(endChar);
            }

            public Named() {
                this.startChar = String.valueOf(DEFAULT_START);
                this.endChar = String.valueOf(DEFAULT_END);
            }
        }

        void mustBeNamed() {
            if ( this instanceof Placeholder.Unnamed ) {
                throw new IllegalArgumentException("Unnamed placeholder has been used for this template!");
            }
        }
    }

    public static class Values {

        private final StringTemplate template;
        private final List<String> strings;

        Values(StringTemplate template, List<String> strings) {
            this.template = template;
            this.strings = strings;
        }

        public String valueOf(String name) {
            this.template.placeholder.mustBeNamed();

            String partName;
            for (int i = 0; i < this.strings.size(); i++ ) {
                partName = this.template.names.get(i);
                if ( partName.equals(name) ) {
                    return this.strings.get(i);
                }
            }

            throw new IllegalArgumentException("No such name in template!");
        }

        public String valueAt(int i) {
            return this.strings.get(i);
        }

        public int quantity() {
            return this.strings.size();
        }

        public Filling useForFill() {
            var filling = new Filling(this.template);
            filling.values.addAll(this.strings);
            return filling;
        }
    }

    public static class Filling {

        private final StringTemplate template;
        private final List<String> values;

        Filling(StringTemplate template) {
            this.template = template;
            this.values = new ArrayList<>();
        }

        private void fillNullsIfEmpty() {
            if ( this.values.isEmpty() ) {
                fillNulls(this.values, this.template.dynamicPartsQty);
            }
        }

        public Filling value(String name, String value) {
            this.fillNullsIfEmpty();
            this.template.placeholder.mustBeNamed();
            int i = this.template.names.indexOf(name);
            if ( i < 0 ) {
                throw new IllegalArgumentException(format("Template does not contain value named '%s'!", name));
            }
            this.values.set(i, value);
            return this;
        }

        public Filling value(int i, String value) {
            this.fillNullsIfEmpty();
            if ( i < 0 ) {
                throw new IndexOutOfBoundsException();
            }
            if ( i > this.template.dynamicPartsQty - 1 ) {
                throw new IndexOutOfBoundsException();
            }
            this.values.set(i, value);
            return this;
        }

        public Filling value(String value) {
            this.fillNullsIfEmpty();
            boolean isSet = false;

            for (int i = 0; i < this.template.dynamicPartsQty; i++) {
                if ( i >= this.values.size() ) {
                    this.values.set(i, value);
                    isSet = true;
                    break;
                }
                else {
                    if (this.values.get(i) == null) {
                        this.values.set(i, value);
                        isSet = true;
                        break;
                    }
                }
            }

            if ( ! isSet ) {
                throw new IllegalArgumentException();
            }

            return this;
        }

        public String complete() {
            this.fillNullsIfEmpty();
            StringBuilder sb = new StringBuilder();

            String staticPart;
            int dynamicPartsIndex = 0;
            int lastStaticPartIndex = this.template.staticPartsQty - 1;

            for (int i = 0; i < this.template.staticPartsQty; i++) {
                staticPart = this.template.staticParts.get(i);

                if ( i == 0 && staticPart.equals("") ) {
                    sb.append(this.values.get(dynamicPartsIndex));
                    dynamicPartsIndex++;
                }
                else if ( i == lastStaticPartIndex && staticPart.equals("") ) {
                    // end of loop
                }
                else {
                    sb.append(staticPart).append(this.values.get(dynamicPartsIndex));
                    dynamicPartsIndex++;
                }
            }

            return sb.toString();
        }
    }

    private final String template;
    private final Placeholder placeholder;
    private final List<String> names;
    private final List<String> staticParts;
    private final int staticPartsQty;
    private final int dynamicPartsQty;
    private final int lastStaticPartIndex;

    public StringTemplate(String template, Placeholder placeholder) {
        this.template = template;
        this.placeholder = placeholder;

        if ( this.placeholder instanceof Placeholder.Unnamed ) {
            this.names = emptyList();
            Placeholder.Unnamed unnamedPlaceholder = (Placeholder.Unnamed) placeholder;
            if ( !template.contains(unnamedPlaceholder.string) ) {
                throw new IllegalArgumentException("Template does not contain any placeholders!");
            }

            this.staticParts = new ArrayList<>();

            int placeholderLength = unnamedPlaceholder.string.length();

            int from = 0;
            int to = template.indexOf(unnamedPlaceholder.string);

            String part;
            while ( to >= 0 ) {
                part = template.substring(from, to);
                staticParts.add(part);

                if ( to == template.length() ) {
                    break;
                }

                if ( part.length() == 0 && staticParts.size() > 1 ) {
                    throw new IllegalArgumentException("It is not allowed to place 2 placeholder without a break! > ..." + staticParts.get(staticParts.size()-2) + placeholder + placeholder);
                }

                from = from + part.length() + placeholderLength;
                to = template.indexOf(unnamedPlaceholder.string, from);

                if ( to < 0 ) {
                    to = template.length();
                }
            }

            this.staticPartsQty = this.staticParts.size();
            this.dynamicPartsQty = this.staticPartsQty - 1;
            this.lastStaticPartIndex = this.staticPartsQty - 1;
        }
        else if ( this.placeholder instanceof Placeholder.Named ) {
            this.names = new ArrayList<>();
            Placeholder.Named namedPlaceholder = (Placeholder.Named) placeholder;
            if ( !template.contains(namedPlaceholder.startChar) || !template.contains(namedPlaceholder.endChar)) {
                throw new IllegalArgumentException("Template does not contain any placeholders!");
            }

            this.staticParts = new ArrayList<>();

            int from = 0;
            int to = template.indexOf(namedPlaceholder.startChar);
            int placeholderEnd;

            String part;
            while ( to >= 0 ) {
                part = template.substring(from, to);
                staticParts.add(part);

                if ( to == template.length() ) {
                    break;
                }

                if ( part.length() == 0 && staticParts.size() > 1 ) {
                    throw new IllegalArgumentException("It is not allowed to place 2 placeholder without a break! > ..." + staticParts.get(staticParts.size()-2) + placeholder + placeholder);
                }

                placeholderEnd = template.indexOf(namedPlaceholder.endChar, to);
                String name = template.substring(to + 1, placeholderEnd);
                names.add(name);
                from = from + part.length() + (name.length() + 2);
                to = template.indexOf(namedPlaceholder.startChar, from);

                if ( to < 0 ) {
                    to = template.length();
                }
            }

            this.staticPartsQty = this.staticParts.size();
            this.dynamicPartsQty = this.staticPartsQty - 1;
            this.lastStaticPartIndex = this.staticPartsQty - 1;
        }
        else {
            throw new UnsupportedLogicException(Placeholder.class + " is not supported!");
        }
    }

    public Values getValuesFrom(String s) {
        List<String> allExtracted = new ArrayList<>();

        int from = staticParts.get(0).length();
        String staticPart;
        String extracted;
        for ( int i = 1; i < staticPartsQty; i++ ) {
            staticPart = staticParts.get(i);

            if ( staticPart.length() == 0 && i == lastStaticPartIndex ) {
                int to = s.length();
                extracted = s.substring(from, to);
                allExtracted.add(extracted);
            }
            else {
                int to = s.indexOf(staticPart, from);
                if ( to < 0 ) {
                    throw new IllegalArgumentException("Given string does not match an initial template!");
                }
                extracted = s.substring(from, to);
                allExtracted.add(extracted);
                from = to + staticPart.length();
            }
        }

        return new Values(this, allExtracted);
    }

    public Filling fill() {
        return new Filling(this);
    }

    public int valuesQuantity() {
        return this.dynamicPartsQty;
    }
}
