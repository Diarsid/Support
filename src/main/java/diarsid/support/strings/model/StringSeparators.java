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
public class StringSeparators {
    
    private final SeparatorType type;
    private final List<Integer> positions;
    private final List<Character> separators;
    private final StringCharacterView character;
    private final StringCharactersView characters;

    public StringSeparators(SeparatorType type) {
        this.type = type;
        this.positions = new ArrayList<>();
        this.separators = new ArrayList<>();
        this.character = new StringCharacterView();
        this.characters = new StringCharactersView();
    }
    
    SeparatorType type() {
        return this.type;
    }
    
    void add(Character separator, int position) {
        this.positions.add(position);
        this.separators.add(separator);
    }
    
    void clear() {
        this.positions.clear();
        this.separators.clear();
    }
    
    int size() {
        return this.positions.size();
    }
    
    boolean areEmpty() {
        return this.positions.isEmpty();
    }
    
    boolean areNotEmpty() {
        return this.positions.size() > 0;
    }

    private int indexOf(int position) {
        int index = this.positions.indexOf((Integer) position);
        return index;
    }
    
    StringCharacter at(int position) {
        int index = this.indexOf(position);
        
        if ( index < 0 ) {
            this.character.absent();
        } else {
            this.character.set(this.separators.get(index), position);
        }
        
        return this.character;
    }
    
    StringCharacter first() {
        if ( this.areEmpty() ) {
            this.character.absent();
        } else {
            this.character.set(this.separators.get(0), this.positions.get(0));
        }
        
        return this.character;
    }
    
    StringCharacter last() {
        if ( this.areEmpty() ) {
            this.character.absent();
        } else {
            int lastIndex = this.positions.size() - 1;
            this.character.set(this.separators.get(lastIndex), this.positions.get(lastIndex));
        }
        
        return this.character;
    }
    
    StringCharacter fisrtAfter(int position) {
        if ( this.areEmpty() ) {
            this.character.absent();
        } else {
            int index = this.indexOf(position);
            if ( index < 0 ) {
                this.character.absent();
            } else {
                
            }
        }
        
        return this.character;
    }
    
    StringCharacter lastBefore(int position) {
        if ( this.areEmpty() ) {
            this.character.absent();
        } else {
            int index = this.indexOf(position);
            if ( index < 0 ) {
                this.character.absent();
            } else {
                
            }
        }
        
        return this.character;
    }
    
    boolean isAnyBetween(int position1, int position2) {
        if ( position1 == position2 ) {
            return false;
        } else if ( position1 > position2 ) {
            int swap = position1;
            position1 = position2;
            position2 = swap;
        } else if ( position1 < 0 || position2 < 0 ) {
            return false;
        }
        
        if ( this.areEmpty() ) {
            return false;
        } else {
            
        }
        throw new UnsupportedOperationException();
    }
    
    StringCharacters between(int position1, int position2) {
//        x
//        return this.characters;
throw new UnsupportedOperationException();
    }
}
