/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.objects.collections;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static diarsid.support.objects.collections.CollectionUtils.shrink;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author Diarsid
 */
public class CollectionsUtilsTest {
    
    public CollectionsUtilsTest() {
    }

    @Test
    public void testShrink() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        
        shrink(list, 2);
        
        assertThat(list).hasSize(2);
    }
    
}
