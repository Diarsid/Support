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
public interface StringCharacter {
    
    int position();
    
    Character character();
    
    boolean isPresent();
    
    boolean isAbsent();
}
