package diarsid.support.filesystem;

public class Main {

    public static void main(String[] args) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "D:\\SOUL\\Programs\\Links\\Games\\Warcraft III.lnk");
        Process process = pb.start();
    }
}
