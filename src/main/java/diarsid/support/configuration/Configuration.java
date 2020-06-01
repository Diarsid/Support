/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.support.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diarsid.support.objects.references.impl.Possible;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import static diarsid.support.configuration.ConfigurationReading.parseConfigLines;
import static diarsid.support.configuration.ConfigurationReading.readConfigEntriesAsLinesFrom;
import static diarsid.support.objects.references.impl.References.allPresent;
import static diarsid.support.objects.references.impl.References.possibleButEmpty;
import static diarsid.support.objects.references.impl.References.possibleWith;

/**
 *
 * @author Diarsid
 */
public class Configuration {
    
    private static final Object CONFIGURATION_LOCK;
    private static final Possible<Configuration> DEFAULT_CONFIGURATION;
    private static final Possible<Configuration> ACTUAL_CONFIGURATION;
    private static final Supplier<ConfigurationException> DEFAULT_UNCONFIGURED_EXCEPTION;
    private static final Supplier<ConfigurationException> ACTUAL_UNCONFIGURED_EXCEPTION;
    private static final Set<String> CONFIG_OPTION_SEPARATORS;
    
    static {
        CONFIGURATION_LOCK = new Object();
        DEFAULT_CONFIGURATION = possibleButEmpty();
        ACTUAL_CONFIGURATION = possibleButEmpty();
        DEFAULT_UNCONFIGURED_EXCEPTION = () -> 
                new ConfigurationException("Default configuration not set!");
        ACTUAL_UNCONFIGURED_EXCEPTION = () -> 
                new ConfigurationException("Actual configuration not set!");
        CONFIG_OPTION_SEPARATORS = new HashSet<>(Arrays.asList(" ", ", ", ","));
    }
    
    // Object value may be String or List<String>
    private final Map<String, Object> options;
    
    Configuration(Map<String, Object> options) {
        this.options = options;
    }
    
    static void setDefaultConfigurationIfNotPresent(String... lines) {
        synchronized ( CONFIGURATION_LOCK ) {
            if ( DEFAULT_CONFIGURATION.isPresent() ) {
                throw new ConfigurationException("Default configuration is already done.");
            }
            
            DEFAULT_CONFIGURATION.resetTo(parseConfigLines(lines));
        }
    }
    
    static void setActualConfigurationIfNotPresentAndDefaultIsDone(String pathToConfigFile) {
        synchronized ( CONFIGURATION_LOCK ) {
            if ( DEFAULT_CONFIGURATION.isNotPresent() ) {
                throw new ConfigurationException(
                        "Default configuration should be set before reading config file!");
            } 
            
            Path configFile = Paths.get(pathToConfigFile);
            Stream<String> configs = readConfigEntriesAsLinesFrom(configFile);
            ACTUAL_CONFIGURATION.resetTo(parseConfigLines(configs));
            ACTUAL_CONFIGURATION.orThrow().takeUnconfiguredFrom(DEFAULT_CONFIGURATION.orThrow());
        }
    }
    
    public static Configuring configure() {
        synchronized ( CONFIGURATION_LOCK ) {
            if ( allPresent(DEFAULT_CONFIGURATION, ACTUAL_CONFIGURATION) ) {
                throw new ConfigurationException("Configuration is already done.");
            } else {
                return new Configuring();
            }            
        }
    }
    
    public static Configuration defaultConfiguration() {
        return DEFAULT_CONFIGURATION.orThrow(DEFAULT_UNCONFIGURED_EXCEPTION);
    }
    
    public static Configuration actualConfiguration() {
        return ACTUAL_CONFIGURATION.orOther(DEFAULT_CONFIGURATION);
    }
    
    void logAll() {
        if ( this.possibleBoolean("log").equalsTo(false) ) {
            return;
        }
        
        Logger logger = LoggerFactory.getLogger(Configuration.class);
        
        this.options
                .entrySet()
                .stream()
                .map((entry) -> {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    String valueString;
                    
                    if ( isNull(value) ) {
                        valueString = "";
                    } else if ( value instanceof String ) {
                        valueString = (String) value;
                    } else if ( value instanceof List ) {
                        valueString = ((List<String>) value).stream().collect(joining(" "));
                    } else {
                        valueString = value.toString();
                    }
                    
                    return key + " = " + valueString;
                })
                .sorted()
                .forEach(line -> logger.info(line));                
    }
    
    void takeUnconfiguredFrom(Configuration other) {
        other.options.forEach((key, value) -> {
            this.options.putIfAbsent(key, value);
        });
    }
    
    public boolean hasString(String option) {
        Object value = this.options.get(option);
        if ( nonNull(value) ) {
            return value instanceof String;
        }
        return false;
    } 
    
    public boolean hasList(String option) {
        Object value = this.options.get(option);
        if ( nonNull(value) ) {
            if ( value instanceof List ) {
                return true;
            } else {
                return findSeparatorIn((String) value).isPresent();
            }            
        }
        return false;
    }
    
    public boolean hasInt(String option) {
        if ( this.hasString(option) ) {
            String found = (String) this.options.get(option);
            try {
                parseInt(found);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        } 
        return false;
    }
    
    public String asString(String option) {
        if ( this.options.containsKey(option) ) {
            Object config = this.options.get(option);
            if ( isNull(config) ) {
                throw new IllegalArgumentException(
                        format("There isn't configured '%s' option.", option));
            } else if ( config instanceof String ) {
                return (String) config;
            } else if ( config instanceof List ) {
                return join(" ", ((List<String>) config));
            } else {
                throw new IllegalArgumentException(
                        format("Unknown configured '%s' option type.", option));
            }
        } else {
            throw new IllegalArgumentException(
                    format("There isn't configured '%s' option.", option));
        }
    }
    
    public int asInt(String option) {
        if ( this.options.containsKey(option) ) {
            Object config = this.options.get(option);
            if ( isNull(config) ) {
                throw new IllegalArgumentException(
                        format("There isn't configured '%s' option.", option));
            } if ( config instanceof String ) {
                return Integer.valueOf((String) config);
            } else if ( config instanceof List ) {
                throw new IllegalArgumentException(
                        format("Configured '%s' option has multiple values, " +
                                "cannot be converted to int.", option));
            } else {
                throw new IllegalArgumentException(
                        format("Unknown configured '%s' option type.", option));
            }
        } else {
            throw new IllegalArgumentException(
                    format("There isn't configured '%s' option.", option));
        }
    }
    
    public boolean asBoolean(String option) {
        if ( this.options.containsKey(option) ) {
            Object config = this.options.get(option);
            if ( isNull(config) ) {
                throw new IllegalArgumentException(
                        format("There isn't configured '%s' option.", option));
            } if ( config instanceof String ) {
                return parseBoolean((String) config);
            } else {
                throw new IllegalArgumentException(
                        format("'%s' option is not String.", option));
            }
        } else {
            return false;
        }
    }
    
    public Possible<Boolean> possibleBoolean(String option) {
        if ( this.options.containsKey(option) ) {
            Object config = this.options.get(option);
            if ( nonNull(config) && config instanceof String ) {
                return possibleWith(parseBoolean((String) config));
            }
        } 
        return possibleButEmpty();
    }
    
    private Optional<String> findSeparatorIn(String config) {
        return CONFIG_OPTION_SEPARATORS
                .stream()
                .filter(separator -> config.contains(separator))
                .findFirst();
    }
    
    public List<String> asList(String option) {
        if ( this.options.containsKey(option) ) {
            Object config = this.options.get(option);
            if ( isNull(config) ) {
                return emptyList();
            } if ( config instanceof String ) {
                String configString = (String) config;
                Optional<String> separator = findSeparatorIn(configString);
                if ( separator.isPresent() ) {
                    return Arrays.asList(configString.split(separator.get()));
                } else {
                    return Arrays.asList(configString);
                }                
            } else if ( config instanceof List ) {
                return (List<String>) config;
            } else {
                throw new IllegalArgumentException(
                        format("Unknown configured '%s' option type.", option));
            }
        } else {
            throw new IllegalArgumentException(
                    format("There isn't configured '%s' option.", option));
        }
    }
    
    public List<Integer> asInts(String option) {
        return this.asList(option)
                .stream()
                .map(s -> parseInt(s))
                .collect(toList());
    }
}
