/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.strings.model;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Diarsid
 */
class StringModelUtil {
    
    private StringModelUtil() {}
    
    static void clearOnlyListsIn(Map<?, ? extends Collection> map) {
        map.forEach((key, collection) -> collection.clear());
    }
}
