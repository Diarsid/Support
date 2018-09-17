/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.support.configuration;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;

import diarsid.support.objects.Pair;

import static java.lang.String.format;
import static java.util.Arrays.stream;

/**
 *
 * @author Diarsid
 */
class ConfigurationReading {
    
    private ConfigurationReading() {
    }
    
    static Stream<String> readConfigEntriesAsLinesFrom(Path configFile) {
        try {
        return Files
                .lines(configFile, Charset.forName("UTF-8"))
                .filter(line -> ! line.isEmpty())
                .filter(line -> line.contains("=")) 
                .map(line -> skipBomIfPresent(line))                               
                .filter(line -> isConfigLine(line))
                .map(line -> clean(line));
        } catch (IOException e) {
            LoggerFactory.getLogger(ConfigurationReading.class).warn( 
                    format("Config file '%s' not found. Default config will be used.", 
                           configFile.toString()));
            return Stream.empty();
        }
    }
    
    static Configuration parseConfigLines(String... lines) {
        return parseConfigLines(stream(lines));
    }
    
    static Configuration parseConfigLines(Stream<String> lines) {
        Map<String, String> singleOptions = new HashMap<>();
        Map<String, List<String>> multipleOptions = new HashMap<>();
        lines
                .map(line -> parsePair(line))
                .forEach(pair -> mergePairIntoMaps(pair, singleOptions, multipleOptions));
        Map<String, Object> options = new HashMap<>();
        options.putAll(singleOptions);
        options.putAll(multipleOptions);
        return new Configuration(options);
    }
    
    private static void mergePairIntoMaps(
            Pair<String, String> pair, 
            Map<String, String> singleOptions, 
            Map<String, List<String>> multipleOptions) {
        if ( singleOptions.containsKey(pair.first()) && 
             ! multipleOptions.containsKey(pair.first()) ) {
            List<String> list = new ArrayList<>();
            list.add(pair.second());
            list.add(singleOptions.get(pair.first()));
            multipleOptions.put(pair.first(), list);
            singleOptions.remove(pair.first());
        } else if ( ! singleOptions.containsKey(pair.first()) && 
                    multipleOptions.containsKey(pair.first()) ) {
            multipleOptions.get(pair.first()).add(pair.second());            
        } else {
            singleOptions.put(pair.first(), pair.second());
        }
    }
    
    private static Pair<String, String> parsePair(String line) {
        line = line.trim();
        String left = line.substring(0, line.indexOf("=")).trim();
        String right = line.substring(line.indexOf("=") + "=".length(), line.length()).trim();
        if ( left.length() == 0 ) {
            throw new IllegalArgumentException(
                    format("Line '%s' is not a valid linf for configuration parsing!", line));
        } 
        if ( right.length() == 0 ) {
            right = null;
        }
        return new Pair<>(left, right);
    }

    private static String clean(String line) {
        return line.trim().substring("[config] ".length());
    }

    private static boolean isConfigLine(String line) {
        return line.trim().startsWith("[config] ");
    }
    
    private static String skipBomIfPresent(String line) {
        if ( line.charAt(0) == '\ufeff' ) {
            return line.substring(1);
        } else {
            return line;
        }
    }
}
