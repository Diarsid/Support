/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.strings.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

/**
 *
 * @author Diarsid
 */
class StringRepeatings {
    
    private final Map<Character, CharRepeatings> repeatingCharsWithPositions;
    private final List<CharRepeatings> usedRepeatings;
    private final List<Integer> allRepeatedPositions;

    StringRepeatings() {
        this.repeatingCharsWithPositions = new HashMap<>();
        this.usedRepeatings = new ArrayList<>();
        this.allRepeatedPositions = new ArrayList<>();
    }
    
    void add(Character repeatedChar, int position) {
        CharRepeatings charRepeatings = this.repeatingCharsWithPositions.get(repeatedChar);
        
        if ( isNull(charRepeatings) ) {
            charRepeatings = new CharRepeatings(repeatedChar);
            charRepeatings.add(position);
            this.repeatingCharsWithPositions.put(repeatedChar, charRepeatings);
        }
        
        charRepeatings.add(position);
        this.usedRepeatings.add(charRepeatings);
        this.allRepeatedPositions.add(position);
    }
    
    void clear() {
        this.usedRepeatings.forEach(CharRepeatings::clear);
        this.usedRepeatings.clear();
        this.allRepeatedPositions.clear();
    }
}
