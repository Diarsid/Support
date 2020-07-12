package diarsid.support.objects.collections.trees;

public interface TreePathConverter <T, P> {

    P convertNode(T node);

    P convert(TreePath<T> path);
}
