/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.configuration;

import static diarsid.support.configuration.Configuration.setActualConfigurationIfNotPresentAndDefaultIsDone;
import static diarsid.support.configuration.Configuration.setDefaultConfigurationIfNotPresent;

/**
 *
 * @author Diarsid
 */
public class Configuring {

    Configuring() {
    }
    
    public Configuring withDefault(String... lines) {
        setDefaultConfigurationIfNotPresent(lines);
        return this;
    }
    
    public Configuring read(String pathToFile) {
        setActualConfigurationIfNotPresentAndDefaultIsDone(pathToFile);
        return this;
    }
}
