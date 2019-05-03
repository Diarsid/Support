/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.strings.model;

import static diarsid.support.strings.model.SeparatorType.ANY;
import static diarsid.support.strings.model.SeparatorType.PATH;
import static diarsid.support.strings.model.SeparatorType.TEXT;

/**
 *
 * @author Diarsid
 */
class StringTypedSeparators {
    
    private final StringSeparators all;
    private final StringSeparators path;
    private final StringSeparators text;

    StringTypedSeparators() {
        this.all = new StringSeparators(ANY);
        this.path = new StringSeparators(PATH);
        this.text = new StringSeparators(TEXT);
    }
    
    void addPath(Character separator, int position) {
        this.path.add(separator, position);
        this.all.add(separator, position);
    }
    
    void addText(Character separator, int position) {
        this.text.add(separator, position);
        this.all.add(separator, position);
    }
    
    void clear() {
        this.all.clear();
        this.path.clear();
        this.text.clear();
    }
    
    StringSeparators get(SeparatorType type) {
        switch (type) {
            case ANY: return this.all;
            case TEXT: return this.text;
            case PATH: return this.path;
            default: throw new IllegalArgumentException(type + " unsupported separator type!");            
        }
    }
}
