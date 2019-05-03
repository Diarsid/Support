/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.test;

import static diarsid.support.configuration.Configuration.configure;

/**
 *
 * @author Diarsid
 */
public class BaseTest {
    
    static {
        configure().withDefault();
    }
}
