package diarsid.support.objects.collections.trees;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import diarsid.test.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static java.util.stream.Collectors.toList;

import static org.assertj.core.api.Assertions.assertThat;

public class AdjacencyListTreeTest extends BaseTest {

    private AdjacencyListTree<String> tree;

    @BeforeEach
    public void setUp() {
        tree = new AdjacencyListTree<>(
                "1",
                "/",
                String::toString,
                (node) -> !node.isBlank(),
                (node) -> {
                    int counter = 1;
                    String copy = node + " (" + counter + ")";
                    while ( tree.all.contains(copy) ) {
                        counter++;
                        copy = node + " (" + counter + ")";
                    }
                    return copy;
                });

        tree.add("1", "2");
        tree.add("1", "3");
        tree.add("1", "4");

        tree.add("2", "5");
        tree.add("2", "6");

        tree.add("3", "7");
        tree.add("3", "8");

        tree.add("6", "9");
        tree.add("6", "10");

        tree.add("10", "11");
        tree.add("10", "12");
    }

    @Disabled
    @Test
    public void test_add() {

        tree.remove("2");
    }

    @Disabled
    @Test
    public void test_addPath_absoluteRoute() {

        TreePath<String> path = tree.addPath("1", "2", "6", "20", "30");
        int a = 5;
    }

    @Disabled
    @Test
    public void test_addPath_relativeRoute() {

        TreePath<String> path = tree.addPath("6", "20", "30");
        int a = 5;
    }

    @Disabled
    @Test
    public void test_absPathOfNodes() {

        TreePath<String> path = tree.absolutePathOf("1", "2", "6", "9");
        int a = 5;
    }

    @Disabled
    @Test
    public void test_add_2() {

        TreePath<String> path1 = tree.absolutePathTo("10");

//        int b = 5;tree.move("6", "1");
//
//        List<String> path2 = tree.pathTo("10");

        int a = 5;

        tree.move("2", "8");

        TreePath<String> path3 = tree.absolutePathTo("10");

        a = 7;
    }

    @Disabled
    @Test
    public void test_add_3() {

        List<TreePath<String>> paths = tree.childrenAbsolutePathsOf("2");

        int b = 5;
    }

    @Disabled
    @Test
    public void test_childrenOf_all() {
        List<String> children = tree.childrenOf("3");
        List<String> expectedChildren = List.of("7", "8");

        assertThat(children).isEqualTo(expectedChildren);
    }

    @Disabled
    @Test
    public void test_childrenOf_all_2() {
        List<String> children = tree.childrenOf("2");
        List<String> expectedChildren = List.of("5", "6", "9", "10", "11", "12");

        assertThat(children).isEqualTo(expectedChildren);
    }

    @Disabled
    @Test
    public void test_childrenOfDepth_all_2() {
        List<String> children = tree.childrenOf("2", 2);
        List<String> expectedChildren = List.of("5", "6", "9", "10");

        assertThat(children).isEqualTo(expectedChildren);
    }

    @Disabled
    @Test
    public void test_moveAndNotify() {
        List<TreePath<String>> oldPaths = new ArrayList<>();
        List<TreePath<String>> newPaths = new ArrayList<>();

        tree.move(
                "6", "8",
                oldPaths::add,
                newPaths::add);

        int a = 5;
    }

    @Disabled
    @Test
    public void test_move_X() {

        TreePath<String> path1 = tree.absolutePathTo("10");

        tree.move("2", "10");

        TreePath<String> path3 = tree.absolutePathTo("10");
    }

    @Disabled
    @Test
    public void test_copy_single() {
        String nodeToCopy = "12";
        String expectedCopy = "12 (1)";

        tree.copy(nodeToCopy, "2");

        assertThat(tree.all()).contains(expectedCopy);

        TreePath<String> expectedCopyPath = tree.absolutePathTo(expectedCopy);

        assertThat(expectedCopyPath.string()).isEqualTo("1/2/12 (1)");
    }

    @Disabled
    @Test
    public void test_copy_withChildren() {
        String nodeToCopy = "10";
        String expectedCopy = "10 (1)";
        List<String> expectedCopyChildren = List.of("11 (1)", "12 (1)");

        tree.copy(nodeToCopy, "2");

        assertThat(tree.all()).contains(expectedCopy);

        for ( String expectedCopyChild : expectedCopyChildren ) {
            assertThat(tree.all()).contains(expectedCopyChild);
        }

        TreePath<String> expectedCopyPath = tree.absolutePathTo(expectedCopy);

        assertThat(expectedCopyPath.string()).isEqualTo("1/2/10 (1)");

        List<TreePath<String>> copiedChildren = tree.childrenAbsolutePathsOf(expectedCopy);
        assertThat(copiedChildren.size()).isEqualTo(expectedCopyChildren.size());
        assertThat(copiedChildren).isEqualTo(expectedCopyChildren);
    }

    @Test
    public void test_subPaths_startsWith() {
        TreePath<String> path = tree.absolutePathTo("12");
        assertThat(path.string()).isEqualTo("1/2/6/10/12");

        TreePath<String> shorterPath = tree.absolutePathTo("6");

        assertThat(path.startsWith(shorterPath)).isEqualTo(true);
        assertThat(path.contains(shorterPath)).isEqualTo(true);
    }

    @Test
    public void test_subPaths_endsWith() {
        TreePath<String> path = tree.absolutePathTo("12");
        assertThat(path.string()).isEqualTo("1/2/6/10/12");

        TreePath<String> subPath = path.relativeTo("6");

        assertThat(path.endsWith(subPath)).isTrue();
        assertThat(path.contains(subPath)).isTrue();
        assertThat(subPath.string()).isEqualTo("10/12");
    }

    @Test
    public void test_subPaths_contains() {
        TreePath<String> path = tree.absolutePathTo("12");
        assertThat(path.string()).isEqualTo("1/2/6/10/12");

        TreePath<String> shorterPath = tree.absolutePathTo("10");
        TreePath<String> subPath = shorterPath.relativeTo("6");

        assertThat(path.contains(subPath)).isEqualTo(true);
        assertThat(subPath.string()).isEqualTo("10");
    }

    @Test
    public void test_replace() {
        List<TreePath<String>> childrenToRemoved = tree.childrenAbsolutePathsOf("3");

        List<TreePath<String>> childrenToRemovedSubPath = childrenToRemoved
                .stream()
                .map(path -> path.relativeTo("3"))
                .collect(toList());

        tree.replace("3", "99");

        List<TreePath<String>> childrenMoved = tree.childrenAbsolutePathsOf("99");

        List<TreePath<String>> childrenMovedSubPath = childrenMoved
                .stream()
                .map(path -> path.relativeTo("99"))
                .collect(toList());

        assertThat(childrenToRemovedSubPath).isEqualTo(childrenMovedSubPath);

        assertThat(tree.all.contains("3")).isFalse();
        TreePath<String> replacedPath;
        for ( TreePath<String> oldPath : childrenToRemoved ) {
            assertThat(oldPath.isValid()).isFalse();
            assertThat(tree.doesExist(oldPath)).isFalse();
            replacedPath = tree.absolutePathTo(oldPath.leaf());
            assertThat(replacedPath.contains("99")).isTrue();
        }
    }

    @Test
    public void test_path_root() {
        TreePath<String> path = tree.absolutePathTo("1");
        assertThat(path.string()).isEqualTo("1");
        assertThat(path.nodes().size()).isEqualTo(1);
    }

    @Test
    public void test_path_toNioPath() {
        TreePath<String> path = tree.absolutePathTo("12");

        TreePathConverter<String, Path> converter = new TreePathConverterToNioPath(false);
        Path nioPath = converter.convert(path);
        assertThat(nioPath).isEqualTo(Paths.get("2/6/10/12"));
    }
}
