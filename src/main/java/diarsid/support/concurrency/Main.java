package diarsid.support.concurrency;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class Main {

    public static void main(String[] args) throws Exception {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get("D:/");
        path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

        WatchKey watchKey;
        boolean valid = true;
        while ( valid ) {
            try {
                watchKey = watchService.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }

            try {
                List<WatchEvent<?>> watchEventList = watchKey.pollEvents();
                for (WatchEvent<?> watchEvent : watchEventList) {
                    Path filePath = (Path) watchEvent.context();
                    Path dir = (Path) watchKey.watchable();
                    Path absolutePath = dir.resolve(filePath).toAbsolutePath();
                    System.out.println("Event " + watchEvent.kind() + " for " + absolutePath.toString());

                }

                valid = watchKey.reset();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
