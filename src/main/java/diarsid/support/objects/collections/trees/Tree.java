package diarsid.support.objects.collections.trees;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.stream.Collectors.joining;

public interface Tree<T> {

    int size();

    boolean doesExist(T node);

    boolean doesExist(TreePath<T> treePath);

    Set<T> all();

    TreePath<T> add(T parent, T child);

    TreePath<T> addPath(T... nodes);

    TreePath<T> addPath(List<T> nodes);

    TreePath<T> absolutePathTo(T node);

    TreePath<T> absolutePathOf(T... nodes);

    TreePath<T> absolutePathOf(List<T> nodes);

    TreePath<T> relativePathOf(T parent, T child);

    void copy(T nodeToCopy, T newParent);

    TreePath<T> replace(T nodeToChange, T newVersion);

    boolean canBeMoved(T nodeToMove, T newParent);

    void move(T nodeToMove, T newParent);

    void move(
            T nodeToMove,
            T newParent,
            Consumer<TreePath<T>> nodeBeforeMoveCallback,
            Consumer<TreePath<T>> nodeAfterMoveCallback);

    void remove(T node);

    List<T> childrenOf(T node);

    List<T> childrenOf(T node, int depth);

    List<TreePath<T>> childrenAbsolutePathsOf(T node);

    String stringify(T node);

    String stringify(List<T> nodes);

}
