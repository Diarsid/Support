package diarsid.support.objects.collections.trees;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.unmodifiableList;

public class TreePath<T> {

    private final boolean isAbsolute;
    private final AdjacencyListTree<T> tree;
    private final List<T> nodes;
    private final String path;

    TreePath(AdjacencyListTree<T> tree, List<T> nodes, String path) {
        this.tree = tree;
        this.nodes = unmodifiableList(nodes);
        this.path = path;
        this.isAbsolute = tree.root.equals(nodes.get(0));
    }

    public boolean isAbsolute() {
        return isAbsolute;
    }

    public T leaf() {
        return nodes.get(nodes.size() - 1);
    }

    public Optional<T> leafParent() {
        if ( nodes.size() >= 2 ) {
            return Optional.of(nodes.get(nodes.size() - 2));
        }
        else {
            return Optional.empty();
        }
    }

    public T root() {
        return nodes.get(0);
    }

    public T nodeAt(int i) {
        return nodes.get(i);
    }

    public List<T> nodes() {
        return nodes;
    }

    public int depth() {
        return nodes.size();
    }

    public String string() {
        return path;
    }

    public boolean contains(T t) {
        return nodes.contains(t);
    }

    public boolean isValid() {
        return tree.doesExist(this);
    }

    public TreePath<T> addRelative(TreePath<T> relativePath) {
        System.out.println("add relative: "  + relativePath.string());
        return tree.add(leaf(), relativePath.leaf());
    }

    public TreePath<T> addChild(T child) {
        return tree.add(leaf(), child);
    }

    public TreePath<T> relativeTo(T parent) {
        int parentIndex = nodes.indexOf(parent);

        if ( parentIndex < 0 ) {
            throw new IllegalArgumentException();
        }

        if ( leaf().equals(parent) ) {
            throw new IllegalArgumentException();
        }

        List<T> path = nodes.subList(parentIndex + 1, nodes.size());

        return new TreePath<>(tree, new ArrayList<>(path), tree.stringify(path));
    }

    public boolean endsWith(TreePath<T> subPath) {
        if ( subPath.isAbsolute ) {
            return this.equals(subPath);
        }

        int subPathDepth = subPath.depth();
        int thisPathDepth = this.depth();

        if ( subPathDepth > thisPathDepth ) {
            return false;
        }

        int subPathPointer = subPathDepth;
        int thisPathPointer = thisPathDepth;
        T subPathNode;
        T thisPathNode;
        for (int i = 0; i < subPathDepth; i++) {
            subPathPointer--;
            thisPathPointer--;
            subPathNode = subPath.nodeAt(subPathPointer);
            thisPathNode = this.nodeAt(thisPathPointer);
            if ( ! thisPathNode.equals(subPathNode) ) {
                return false;
            }
        }

        return true;
    }

    public boolean startsWith(TreePath<T> subPath) {
        int subPathDepth = subPath.depth();
        int thisPathDepth = this.depth();

        if ( subPathDepth > thisPathDepth ) {
            return false;
        }

        int subPathPointer = 0;
        int thisPathPointer = 0;
        T subPathNode;
        T thisPathNode;
        for (int i = 0; i < subPathDepth; i++) {
            subPathNode = subPath.nodeAt(subPathPointer);
            thisPathNode = this.nodeAt(thisPathPointer);
            if ( ! thisPathNode.equals(subPathNode) ) {
                return false;
            }
            subPathPointer++;
            thisPathPointer++;
        }

        return true;
    }

    public boolean contains(TreePath<T> subPath) {
        int subPathDepth = subPath.depth();
        int thisPathDepth = this.depth();

        if ( subPathDepth > thisPathDepth ) {
            return false;
        }

        T subPathRoot = subPath.root();
        int indexOfSubPath = nodes.indexOf(subPathRoot);

        if ( indexOfSubPath < 0 ) {
            return false;
        }

        int subPathPointer = 0;
        int thisPathPointer = indexOfSubPath;
        T subPathNode;
        T thisPathNode;
        for (int i = 0; i < subPathDepth; i++) {
            subPathNode = subPath.nodeAt(subPathPointer);
            thisPathNode = this.nodeAt(thisPathPointer);
            if ( ! thisPathNode.equals(subPathNode) ) {
                return false;
            }
            subPathPointer++;
            thisPathPointer++;
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TreePath)) return false;
        TreePath<?> treePath = (TreePath<?>) o;
        return nodes.equals(treePath.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes);
    }
}
