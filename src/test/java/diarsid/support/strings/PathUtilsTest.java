package diarsid.support.strings;


import java.util.List;

import diarsid.support.objects.collections.Lists;
import org.junit.Test;

import static diarsid.support.strings.PathUtils.decomposePath;

public class PathUtilsTest {

    @Test
    public void testDecomposePathWithoutNormalization_nonNormalizedPath() {
        String path = "\\One/ /two//three\\\\four";
        List<String> paths = List.of(
                "\\One/ /two//three",
                "\\One/ /two",
                "\\One/ /");

        List<String> actualPaths = decomposePath(path, false, false);
        int a = 5;
    }

    @Test
    public void testDecomposePathWithoutNormalization_normalizedPath() {
        String path = "/One/two/three/four";
        List<String> paths = List.of(
                "/One/two/three",
                "/One/two",
                "/One");

        List<String> actualPaths = decomposePath(path, false, false);
        int a = 5;
    }
}
