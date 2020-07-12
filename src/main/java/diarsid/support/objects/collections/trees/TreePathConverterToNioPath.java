package diarsid.support.objects.collections.trees;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TreePathConverterToNioPath implements TreePathConverter<String, Path> {

    private final boolean includeRoot;

    public TreePathConverterToNioPath(boolean includeRoot) {
        this.includeRoot = includeRoot;
    }

    @Override
    public Path convertNode(String node) {
        return Paths.get(node);
    }

    @Override
    public Path convert(TreePath<String> path) {
        int i;

        if ( includeRoot ) {
            i = 0;
        }
        else {
            i = 1;
        }

        Path pathNio = Paths.get(path.nodeAt(i));
        i++;
        for (; i < path.depth(); i++ ) {
            pathNio = pathNio.resolve(path.nodeAt(i));
        }

        return pathNio;
    }
}
