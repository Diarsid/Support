package diarsid.support.objects.collections.trees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class AdjacencyListTree<T> implements Tree<T> {

    final Function<T, String> toPath;
    final Function<T, T> copyNodeFunction;
    final String separator;
    final Predicate<T> canBeParent;
    final T root;
    final Set<T> all;
    final Map<T, T> child_parent;
    final Map<T, Set<T>> parent_children;
    final Map<T, TreePath<T>> node_path;
    final boolean treeTypeSupportsCopy;

    final List<T> swapForNodeChildrenRemove;
    final List<T> swapOneForChildrenPaths;
    final List<T> swapTwoForChildrenPaths;

    public AdjacencyListTree(
            T root,
            String separator,
            Function<T, String> nodeToString,
            Predicate<T> canBeParent,
            Function<T, T> copyNodeFunction) {
        this.separator = separator;
        this.canBeParent = canBeParent;
        this.toPath = nodeToString;
        this.root = root;

        if ( isNull(copyNodeFunction) ) {
            this.copyNodeFunction = null;
            this.treeTypeSupportsCopy = false;
        }
        else {
            this.copyNodeFunction = copyNodeFunction;
            this.treeTypeSupportsCopy = true;
        }

        this.all = new HashSet<>();
        this.all.add(root);

        this.child_parent = new HashMap<>();

        this.parent_children = new HashMap<>();
        this.parent_children.put(root, new HashSet<>());

        this.node_path = new HashMap<>();
        this.node_path.put(root, new TreePath<>(this, findPathTo(root), nodeToString.apply(root)));

        this.swapForNodeChildrenRemove = new ArrayList<>();
        this.swapOneForChildrenPaths = new ArrayList<>();
        this.swapTwoForChildrenPaths = new ArrayList<>();
    }

    private T copySafely(T node) {
        T copy = copyNodeFunction.apply(node);
        requireNonNull(copy);
        if ( copy.equals(node) ) {
            throw new IllegalStateException();
        }
        return copy;
    }

    @Override
    public int size() {
        return all.size();
    }

    @Override
    public boolean doesExist(T node) {
        return all.contains(node);
    }

    @Override
    public boolean doesExist(TreePath<T> treePath) {
        T leaf = treePath.leaf();
        TreePath<T> absolutePathToLeaf = node_path.get(leaf);

        if ( isNull(absolutePathToLeaf) ) {
            return false;
        }

        if ( treePath.isAbsolute() ) {
            return absolutePathToLeaf.equals(treePath);
        }
        else {
            return absolutePathToLeaf.endsWith(treePath);
        }
    }

    @Override
    public Set<T> all() {
        return all;
    }

    public List<TreePath<T>> addAll(T parent, Collection<T> childs) {
        List<TreePath<T>> paths = new ArrayList<>();

        for ( T child : childs ) {
            paths.add(add(parent, child));
        }

        return paths;
    }

    public List<TreePath<T>> addAll(T parent, T child, T... otherChildren) {
        List<TreePath<T>> paths = new ArrayList<>();

        paths.add(add(parent, child));

        for ( T otherChild : otherChildren ) {
            paths.add(add(parent, otherChild));
        }

        return paths;
    }

    @Override
    public TreePath<T> add(T parent, T child) {
        if ( ! all.contains(parent) ) {
            throw new IllegalArgumentException();
        }

        if ( ! canBeParent.test(parent) ) {
            throw new IllegalArgumentException();
        }

        if ( all.contains(child) ) {
            throw new IllegalArgumentException();
        }

        checkChildParentRelation(parent, child);

        boolean wasNewTotal = all.add(child);

        if ( wasNewTotal ) {

        }

        T previousParent = child_parent.put(child, parent);

        if ( nonNull(previousParent) ) {
            Set<T> previousParentChildren = parent_children.get(previousParent);
            previousParentChildren.remove(child);
        }

        Set<T> children = parent_children.get(parent);

        if ( isNull(children) ) {
            children = new HashSet<>();
            parent_children.put(parent, children);
        }

        boolean wasNewInParent = children.add(child);

        List<T> path = findPathTo(child);

        TreePath<T> childPath = createNewPathFrom(path);
        node_path.put(child, childPath);

        return childPath;
    }

    @Override
    public TreePath<T> addPath(T... nodes) {
        int nodesQty = nodes.length;

        if ( nodesQty < 2 ) {
            throw new IllegalArgumentException();
        }

        T parent;
        T existedParent;
        T child;
        TreePath<T> path = null;
        int parentsQty = nodesQty - 1;
        for ( int i = 0; i < parentsQty; i++ ) {
            parent = nodes[i];
            child = nodes[i + 1];
            existedParent = child_parent.get(child);
            if ( nonNull(existedParent) ) {
                if ( ! existedParent.equals(parent) ) {
                    throw new IllegalArgumentException();
                }
                else {
                    continue;
                }
            }
            path = add(parent, child);
        }

        if ( isNull(path) ) {
            T leaf = nodes[nodesQty - 1];
            path = absolutePathTo(leaf);
        }

        return path;
    }

    @Override
    public TreePath<T> addPath(List<T> nodes) {
        int nodesQty = nodes.size();

        if ( nodesQty < 2 ) {
            throw new IllegalArgumentException();
        }

        T parent;
        T existedParent;
        T child;
        TreePath<T> path = null;
        int parentsQty = nodesQty - 1;
        for ( int i = 0; i < parentsQty; i++ ) {
            parent = nodes.get(i);
            child = nodes.get(i + 1);
            existedParent = child_parent.get(child);
            if ( nonNull(existedParent) ) {
                if ( ! existedParent.equals(parent) ) {
                    throw new IllegalArgumentException();
                }
                else {
                    continue;
                }
            }
            path = add(parent, child);
        }

        if ( isNull(path) ) {
            T leaf = nodes.get(nodesQty - 1);
            path = absolutePathTo(leaf);
        }

        return path;
    }

    private void checkChildParentRelation(T parent, T child) {
        TreePath<T> parentPath = absolutePathTo(parent);

        if ( parentPath.contains(child) ) {
            throw new IllegalArgumentException();
        }
    }

    private TreePath<T> createNewPathFrom(List<T> listPath) {
        String stringPath = stringify(listPath);
        return new TreePath<>(this, listPath, stringPath);
    }

    @Override
    public void remove(T node) {
        if ( root.equals(node) ) {
            throw new IllegalArgumentException();
        }

        Set<T> children = parent_children.remove(node);
        removeFromStructures(node);

        Set<T> childChildren;
        while ( ! children.isEmpty() ) {
            for ( T child : children ) {
                childChildren = parent_children.remove(child);
                removeFromStructures(child);
                if ( nonNull(childChildren) && ! childChildren.isEmpty() ) {
                    swapForNodeChildrenRemove.addAll(childChildren);
                }
            }
            children.clear();
            children.addAll(swapForNodeChildrenRemove);
            swapForNodeChildrenRemove.clear();
        }
    }

    private void removeFromStructures(T node) {
        all.remove(node);
        child_parent.remove(node);
        node_path.remove(node);
    }

    @Override
    public boolean canBeMoved(T nodeToMove, T newParent) {
        TreePath<T> parentPath = absolutePathTo(newParent);

        return ! parentPath.contains(nodeToMove);
    }

    @Override
    public void move(T nodeToMove, T newParent) {
        checkChildParentRelation(newParent, nodeToMove);
        move(nodeToMove, newParent, null, null);
    }

    @Override
    public void move(
            T nodeToMove,
            T newParent,
            Consumer<TreePath<T>> nodeBeforeMoveCallback,
            Consumer<TreePath<T>> nodeAfterMoveCallback) {
        checkChildParentRelation(newParent, nodeToMove);

        List<TreePath<T>> pathsWillBeMoved = childrenAbsolutePathsOf(nodeToMove);
        pathsWillBeMoved.add(0, absolutePathTo(nodeToMove));

        if ( nonNull(nodeBeforeMoveCallback) ) {
            pathsWillBeMoved.forEach(nodeBeforeMoveCallback);
        }

        moveInternally(nodeToMove, newParent);

        List<TreePath<T>> pathsWasMoved = pathsWillBeMoved
                .stream()
                .map(this::replaceCachedPath)
                .collect(toList());

        if ( nonNull(nodeAfterMoveCallback) ) {
            pathsWasMoved.forEach(nodeAfterMoveCallback);
        }
    }

    private TreePath<T> replaceCachedPath(TreePath<T> oldPath) {
        T node = oldPath.leaf();
        List<T> newPathList = findPathTo(node);
        TreePath<T> newPath = createNewPathFrom(newPathList);
        TreePath<T> oldPathCached = node_path.replace(node, newPath);

        if ( ! oldPathCached.equals(oldPath) ) {
            throw new IllegalStateException();
        }

        return newPath;
    }

    private void moveInternally(T nodeToMove, T newParent) {
        if ( ! all.contains(newParent) ) {
            throw new IllegalArgumentException();
        }

        if ( ! all.contains(nodeToMove) ) {
            throw new IllegalArgumentException();
        }

        T oldParent = child_parent.remove(nodeToMove);

        if ( isNull(oldParent) ) {
            throw new IllegalStateException();
        }

        boolean removed = parent_children.get(oldParent).remove(nodeToMove);

        if ( ! removed ) {
            throw new IllegalStateException();
        }

        child_parent.put(nodeToMove, newParent);
        Set<T> children = parent_children.get(newParent);

        if ( isNull(children) ) {
            children = new HashSet<>();
            parent_children.put(newParent, children);
        }

        children.add(nodeToMove);
    }

//    private TreePath<T>  replaceCachedPathOf(T node) {
//        List<T> newPathList = findPathTo(node);
//        TreePath<T> newPath = createNewPathFrom(newPathList);
//        TreePath<T> oldPath = node_path.replace(node, newPath);
//        return newPath;
//    }

    @Override
    public TreePath<T> absolutePathTo(T node) {
        return node_path.get(node);
    }

    @Override
    public TreePath<T> absolutePathOf(T... nodes) {
        int nodesQty = nodes.length;

        if ( nodesQty == 0 ) {
            throw new IllegalArgumentException();
        }

        T leaf = nodes[nodesQty - 1];
        TreePath<T> realPathToLeaf = absolutePathTo(leaf);

        if ( realPathToLeaf.depth() != nodesQty ) {
            throw new IllegalArgumentException();
        }

        T nodeInRealPath;
        T nodeInArgPath;
        for ( int i = 0; i < nodesQty; i++ ) {
            nodeInArgPath = nodes[i];
            nodeInRealPath = realPathToLeaf.nodeAt(i);

            if ( ! nodeInRealPath.equals(nodeInArgPath) ) {
                throw new IllegalArgumentException();
            }
        }

        return realPathToLeaf;
    }

    @Override
    public TreePath<T> absolutePathOf(List<T> nodes) {
        int nodesQty = nodes.size();

        if ( nodesQty == 0 ) {
            throw new IllegalArgumentException();
        }

        T leaf = nodes.get(nodesQty - 1);
        TreePath<T> realPathToLeaf = absolutePathTo(leaf);

        if ( realPathToLeaf.depth() != nodesQty ) {
            throw new IllegalArgumentException();
        }

        T nodeInRealPath;
        T nodeInArgPath;
        for ( int i = 0; i < nodesQty; i++ ) {
            nodeInArgPath = nodes.get(i);
            nodeInRealPath = realPathToLeaf.nodeAt(i);

            if ( ! nodeInRealPath.equals(nodeInArgPath) ) {
                throw new IllegalArgumentException();
            }
        }

        return realPathToLeaf;
    }

    @Override
    public TreePath<T> relativePathOf(T parent, T child) {
        TreePath<T> absolutePath = absolutePathTo(child);
        return absolutePath.relativeTo(parent);
    }

    @Override
    public void copy(T nodeToCopy, T newParent) {
        if ( ! treeTypeSupportsCopy ) {
            throw new IllegalStateException();
        }

        if ( ! all.contains(newParent) ) {
            throw new IllegalArgumentException();
        }

        if ( ! all.contains(nodeToCopy) ) {
            throw new IllegalArgumentException();
        }

        checkChildParentRelation(newParent, nodeToCopy);

        T copy = copySafely(nodeToCopy);
        add(newParent, copy);

        TreePath<T> copyPath = absolutePathTo(copy);
        List<TreePath<T>> childToCopySubPaths = childrenAbsolutePathsOf(nodeToCopy)
                .stream()
                .map(absolutePathToOriginalChild -> absolutePathToOriginalChild.relativeTo(nodeToCopy))
                .map(relativePathToOriginalChild -> copyPath.addRelative(relativePathToOriginalChild))
                .collect(toList());

//        for ( TreePath<T> childToCopySubPath : childToCopySubPaths ) {
//            add(childToCopySubPath.nodes());
//        }
    }

    private List<T> findPathTo(T node) {
        if ( ! all.contains(node) ) {
            throw new IllegalArgumentException();
        }

        List<T> path = new ArrayList<>();

        if ( node.equals(root) ) {
            path.add(node);
            return path;
        }

        T parent;
        T child = node;
        path.add(node);

        while ( true ) {
            parent = child_parent.get(child);
            if ( isNull(parent) ) {
                break;
            }
            else {
                path.add(0, parent);
                child = parent;
            }
        }

        return path;
    }

    @Override
    public TreePath<T> replace(T nodeToReplace, T newNode) {
        if ( ! all.contains(nodeToReplace) ) {
            throw new IllegalArgumentException();
        }

        if ( all.contains(newNode) ) {
            throw new IllegalArgumentException();
        }

        T nodeToReplaceParent = child_parent.get(nodeToReplace);

        if ( isNull(nodeToReplaceParent) ) {
            throw new IllegalArgumentException("Cannot change root!");
        }

        add(nodeToReplaceParent, newNode);
        List<T> nodeToReplaceChildren = childrenOf(nodeToReplace);

        for ( T child : nodeToReplaceChildren ) {
            move(child, newNode);
        }

        remove(nodeToReplace);

        return absolutePathTo(newNode);
    }

    @Override
    public List<T> childrenOf(T node) {
        if ( ! all.contains(node) ) {
            throw new IllegalArgumentException();
        }

        Set<T> initialChildren = parent_children.get(node);

        if ( isNull(initialChildren) || initialChildren.isEmpty() ) {
            return emptyList();
        }

        List<T> allChildren = new ArrayList<>();
        List<T> childrenSwap = new ArrayList<>();

        List<T> children = new ArrayList<>();
        children.addAll(initialChildren);

        Set<T> childChildren;
        while ( ! children.isEmpty() ) {
            for ( T child : children ) {
                childChildren = parent_children.get(child);
                allChildren.add(child);
                if ( nonNull(childChildren) && ! childChildren.isEmpty() ) {
                    childrenSwap.addAll(childChildren);
                }
            }
            children.clear();
            children.addAll(childrenSwap);
            childrenSwap.clear();
        }

        return allChildren;
    }

    @Override
    public List<T> childrenOf(T node, int depth) {
        if ( ! all.contains(node) ) {
            throw new IllegalArgumentException();
        }

        if ( depth <= 0 ) {
            throw new IllegalArgumentException();
        }

        Set<T> initialChildren = parent_children.get(node);

        if ( depth == 1 ) {
            return new ArrayList<>(initialChildren);
        }

        if ( isNull(initialChildren) || initialChildren.isEmpty() ) {
            return emptyList();
        }

        List<T> allChildren = new ArrayList<>();
        List<T> childrenSwap = new ArrayList<>();

        List<T> children = new ArrayList<>();
        children.addAll(initialChildren);

        int visitedDepth = 0;
        Set<T> childChildren;
        while ( ! children.isEmpty() && visitedDepth < depth ) {
            for ( T child : children ) {
                allChildren.add(child);
                childChildren = parent_children.get(child);
                if ( nonNull(childChildren) && ! childChildren.isEmpty() ) {
                    childrenSwap.addAll(childChildren);
                }
            }
            children.clear();
            children.addAll(childrenSwap);
            childrenSwap.clear();
            visitedDepth++;
        }

        if ( ! children.isEmpty() ) {
            children.clear();
        }

        return allChildren;
    }

    @Override
    public List<TreePath<T>> childrenAbsolutePathsOf(T node) {
        List<T> children = childrenOf(node);

        return children
                .stream()
                .map(node_path::get)
                .collect(toList());
    }

    @Override
    public String stringify(T node) {
        return toPath.apply(node);
    }

    @Override
    public String stringify(List<T> nodes) {
        return nodes.stream().map(toPath).collect(joining(separator));
    }
}
