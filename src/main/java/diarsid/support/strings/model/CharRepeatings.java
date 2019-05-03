/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.strings.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Diarsid
 */
class CharRepeatings {
    
    private final Character character;
    private final List<Integer> repeatings;

    public CharRepeatings(Character character) {
        this.character = character;
        this.repeatings = new ArrayList<>();
    }
    
    Character character() {
        return this.character;
    }
    
    void add(int position) {
        this.repeatings.add(position);
    }
    
    void clear() {
        this.repeatings.clear();
    }
    
}
