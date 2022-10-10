package diarsid.support.strings;

import java.util.ArrayList;
import java.util.List;

public class TemplateExtractor {

    private final String template;
    private final String placeholder;
    private final List<String> staticParts;
    private final int staticPartsQty;
    private final int lastStaticPartIndex;

    public TemplateExtractor(String template, String placeholder) {
        this.template = template;
        this.placeholder = placeholder;

        if ( !template.contains(placeholder) ) {
            throw new IllegalArgumentException("Template does not contain any placeholders!");
        }

        this.staticParts = new ArrayList<>();

        int placeholderLength = placeholder.length();

        int from = 0;
        int to = template.indexOf(placeholder);

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
            to = template.indexOf(placeholder, from);

            if ( to < 0 ) {
                to = template.length();
            }
        }

        this.staticPartsQty = this.staticParts.size();
        this.lastStaticPartIndex = this.staticPartsQty - 1;
    }

    public List<String> extractFrom(String s) {
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

        return allExtracted;
    }
}
