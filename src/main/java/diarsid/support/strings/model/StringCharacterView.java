/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.strings.model;

/**
 *
 * @author Diarsid
 */
class StringCharacterView implements StringCharacter {
    
    private final static int UNINIT = -1;
    
    private int position;
    private Character character;

    StringCharacterView() {
        this.position = UNINIT;
        this.character = null;
    }
    
    void set(Character character, int position) {
        this.position = position;
        this.character = character;
    }
    
    void absent() {
        this.position = UNINIT;
        this.character = null;
    }

    @Override
    public int position() {
        return this.position;
    }

    @Override
    public Character character() {
        return this.character;
    }    

    @Override
    public boolean isPresent() {
        return this.position != UNINIT;
    }

    @Override
    public boolean isAbsent() {
        return this.position == UNINIT;
    }
    
}
