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
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import static diarsid.support.strings.StringUtils.isPathSeparator;
import static diarsid.support.strings.StringUtils.isTextSeparator;
import static diarsid.support.strings.StringUtils.lower;

/**
 *
 * @author Diarsid
 */
public class StringModel {
    
    private final Map<Character, List<Integer>> charsPositions;
    private final List<List<Integer>> usedCharsPositionsLists;
    private final StringTypedSeparators separators;
    private final StringRepeatings repeatings;
    private String originLowerCase;
    private String origin;
    private int originLength;

    public StringModel() {
        this.charsPositions = new HashMap<>();
        this.usedCharsPositionsLists = new ArrayList<>();
        this.separators = new StringTypedSeparators();
        this.repeatings = new StringRepeatings();
    }
    
    public StringModel resetTo(String newOrigin) {
        requireNonNull(newOrigin);
        this.clear();
        this.resetOrigin(newOrigin);
        this.buildModel();
        return this;
    }

    private void resetOrigin(String newOrigin) {
        this.origin = newOrigin;
        this.originLowerCase = lower(newOrigin);
        this.originLength = newOrigin.length();
    }
    
    private void buildModel() {
        List<Integer> charPositions;
        Character prevChar = null;
        Character currChar;
        boolean prevCharRepeatAdded = false;
        
        for (int position = 0; position < this.originLength; position++) {
            currChar = this.originLowerCase.charAt(position);
            charPositions = this.charsPositions.get(currChar);
            
            if ( isNull(charPositions) ) {
                charPositions = new ArrayList<>();
                this.charsPositions.put(currChar, charPositions);                
            }

            this.usedCharsPositionsLists.add(charPositions);
            
            if ( isPathSeparator(currChar) ) {
                this.separators.addPath(currChar, position);
            } else if ( isTextSeparator(currChar) ) {
                this.separators.addText(currChar, position);
            } 
            
            charPositions.add(position);
            if ( nonNull(prevChar) ) {
                if (currChar.equals(prevChar)) {
                    if ( ! prevCharRepeatAdded ) {
                        this.repeatings.add(prevChar, position - 1);
                        prevCharRepeatAdded = true;
                    }
                    this.repeatings.add(currChar, position);
                } else {
                    prevCharRepeatAdded = false;
                }
            }
            prevChar = currChar;
        }
    }
    
    private void clear() {
        this.separators.clear();
        this.repeatings.clear();
        this.usedCharsPositionsLists.forEach(List::clear);
        this.usedCharsPositionsLists.clear();
    }
    
    public static void main(String[] args) {
        StringModel model = new StringModel();
        model.resetTo("boooks/Tolkien/the hobbit.pdf");
        model.clear();
    }
}
