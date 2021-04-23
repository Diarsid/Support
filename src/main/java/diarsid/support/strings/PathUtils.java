package diarsid.support.strings;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import diarsid.support.objects.Pair;

import static java.lang.String.join;

import static diarsid.support.strings.StringUtils.containsPathSeparator;
import static diarsid.support.strings.StringUtils.isPathSeparator;
import static diarsid.support.strings.StringUtils.normalizeDashes;

public class PathUtils {
    
    private PathUtils() {
    }
    
    public static boolean existsInFileSystem(String path) {
        return Files.exists(Paths.get(path));
    }
    
    public static boolean notExistsInFileSystem(String path) {
        return ! Files.exists(Paths.get(path));
    }
    
    public static boolean pathIsDirectory(String path) {
        return Files.exists(Paths.get(path)) && Files.isDirectory(Paths.get(path));
    }
    
    public static boolean pathIsDirectory(Path dir) {
        return Files.exists(dir) && Files.isDirectory(dir);
    }
    
    public static int indexOfFirstPathSeparator(String target) {
        int indexOfSlash = target.indexOf("/");
        int indexOfBackSlash = target.indexOf("\\");
        if ( indexOfBackSlash < 0 ) {
            return indexOfSlash;
        } else if ( indexOfSlash < 0 ) {
            return indexOfBackSlash;
        } else {
            return ( indexOfSlash < indexOfBackSlash) ? indexOfSlash : indexOfBackSlash; 
        }
    }
    
    public static Path joinPathFrom(String... fragments) {
        return Paths.get(normalizeSeparators(join("/", fragments)));
    }
    
    public static String joinToPath(String one, String two) {        
        return normalizeSeparators(one + "/" + two);
    }
    
    public static String joinToPathFrom(String... fragments) {
        if ( fragments.length == 0 ) {
            return "";
        }
        return normalizeSeparators(join("/", fragments));
    }
    
    public static String joinToPathFrom(List<String> fragments) {
        if ( fragments.isEmpty() ) {
            return "";
        }
        return normalizeSeparators(join("/", fragments));
    }
    
    public static int indexOfNextPathSeparatorAfter(String target, String pattern) {
        int indexOfNextSlash = target.indexOf("/", target.indexOf(pattern));
        int indexOfNextBackSlash = target.indexOf("\\", target.indexOf(pattern));
        if ( indexOfNextBackSlash < 0 ) {
            return indexOfNextSlash;
        } else if ( indexOfNextSlash < 0 ) {
            return indexOfNextBackSlash;
        } else {
            return ( indexOfNextSlash < indexOfNextBackSlash ) ? indexOfNextSlash : indexOfNextBackSlash; 
        }
    } 
    
    public static String subpathToPattern(String target, String pattern) {
        if ( containsPathSeparator(pattern) ) {
            pattern = pattern.substring(indexOfLastPathSeparator(pattern) + 1, pattern.length());
        }
        return target.substring(0, indexOfNextPathSeparatorAfter(target, pattern));
    }
    
    public static int indexOfLastPathSeparator(String target) {
        int indexOfSlash = target.lastIndexOf("/");
        int indexOfBackSlash = target.lastIndexOf("\\");
        if ( indexOfBackSlash < 0 ) {
            return indexOfSlash;
        } else if ( indexOfSlash < 0 ) {
            return indexOfBackSlash;
        } else {
            return ( indexOfSlash > indexOfBackSlash) ? indexOfSlash : indexOfBackSlash; 
        }
    }
    
    public static Pair<String, String> toSubpathAndTarget(String path) {
        int lastSeparatorIndex = indexOfLastPathSeparator(path);
        String target = path.substring(lastSeparatorIndex + 1, path.length());
        String subpath = path.substring(0, lastSeparatorIndex);
        return new Pair(subpath, target);
    }
    
    public static String extractLastElementFromPath(String path) {
        int lastSeparatorIndex = indexOfLastPathSeparator(path);
        if ( lastSeparatorIndex < 0 ) {
            return path;
        } else {
            return path.substring(lastSeparatorIndex + 1, path.length());
        }        
    }
    
    public static boolean isAcceptableWebPath(String target) {        
        try {            
            // validate possible url.
            return 
                    target.contains("/") && 
                    ! new URL(target).toURI().toString().isEmpty();
        } catch (MalformedURLException|URISyntaxException e) {
            return false;
        }
    }
    
    public static String extractLocationFromPath(String path) {
        if ( path.isEmpty() ) {
            return "";
        } else if ( containsPathSeparator(path) ) {
            return path.substring(0, indexOfFirstPathSeparator(path));
        } else {
            return path;
        }       
    }
    
    public static String extractTargetFromPath(String path) {
        if ( path.isEmpty() ) {
            return "";
        } else if ( containsPathSeparator(path) ) {
            return path.substring(indexOfFirstPathSeparator(path) + 1);
        } else {
            return path;
        }
    }
    
    public static String trimSeparators(String target) {
        if (target.endsWith("/")) {
            target = target.substring(0, target.length()-1);
        }
        if (target.startsWith("/")) {
            target = target.substring(1);
        }
        return target;
    }

//    public static String trimAnySeparators(String target) {
//        target = target.trim().strip();
//        int firstSeparatorAtStart = indexOfFirstPathSeparator(target);
//        int lastSeparatorAtStart = 0;
//
//        if ( firstSeparatorAtStart == 0 ) {
//            for (int i = 1; i < target.length(); i++) {
//                if ( isPathSeparator(target.charAt(i)) ) {
//                    lastSeparatorAtStart = i;
//                }
//                else {
//                    break;
//                }
//            }
//        }
//
//
//    }
    
    public static String normalizeArgument(String target) {
        return normalizeDashes(normalizeSeparators(target));
    }

    public static String normalizeSeparators(String target) {
        return target.replaceAll("[/\\\\]+", "/");
    }
    
    public static String removeSeparators(String target) {
        return target.replaceAll("[/\\\\]+", "");
    }
    
    public static String[] splitPathFragmentsFrom(String target) {
        target = normalizeArgument(target);
        target = trimSeparators(target);
        return target.split("/");
    }    
    
    public static String[] splitToParts(Path path) {
        return normalizeSeparators(path.normalize().toString()).split("/");
    }
    
    public static String asRelativeString(Path root, Path file) {
        return root
                .relativize(file)
                .normalize()
                .toString()
                .replace("\\", "/");
    }
    
    public static String asName(Path file) {
        if ( file.getNameCount() > 0 ) {
            return file.getFileName().toString();
        } else {
            return file.toString();
        }
    }
    
    public static List<String> decomposePath(String path) {
        List<String> decomposedPaths = new ArrayList<>();
        path = normalizeArgument(path);
        decomposedPaths.add(path);
        while ( containsPathSeparator(path) ) {            
            path = path.substring(0, indexOfLastPathSeparator(path));
            decomposedPaths.add(path);
        }
        return decomposedPaths;
    }

    public static List<String> decomposePath(String path, boolean normalize, boolean includeOriginal) {
        List<String> decomposedPaths = new ArrayList<>();

        if ( normalize ) {
            path = normalizeArgument(path);
        }
        else {
            path = path.trim().strip();
        }

        if ( includeOriginal ) {
            decomposedPaths.add(path);
        }

        while ( containsPathSeparator(path) ) {
            path = path.substring(0, indexOfLastPathSeparator(path)).trim().strip();
            if ( path.isEmpty() || path.isBlank() ) {
                continue;
            }
            decomposedPaths.add(path);
        }

        return decomposedPaths;
    }
    
    public static int findDepthOf(String path) {
        int separators = 0;
        int pathLength = path.length();
        int lastCharIndex = pathLength - 1;
        boolean previousIsSeparator = false;
        boolean hasSeparatorsAtStart = false;
        boolean hasSeparatorsAtEnd = false;
        
        for (int i = 0; i < pathLength; i++) {
            if ( isPathSeparator(path.charAt(i)) ) {
                if ( i == 0 ) {
                    hasSeparatorsAtStart = true;
                } else if ( i == lastCharIndex ) {
                    hasSeparatorsAtEnd = true;
                }
                if ( previousIsSeparator ) {
                    continue;
                }
                separators++;
                previousIsSeparator = true;
            } else {
                previousIsSeparator = false;
            }
        }
        
        int depth = separators + 1;
        
        if ( hasSeparatorsAtStart ) {
            depth--;
        }
        if ( hasSeparatorsAtEnd ) {
            depth--;
        }
        
        return depth;
    }
}
