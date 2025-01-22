package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;

public class Repository {
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "Objects");
    public static final File COMMITS_DIR = join(OBJECTS_DIR, "Commits");
    public static final File BLOBS_DIR = join(OBJECTS_DIR, "Blobs");
    public static final File BRANCHES_DIR = join(GITLET_DIR, "Branches");
    public static final File HEAD_FILE = join(GITLET_DIR, "Head");
    public static final File STORAGE_FILE = join(GITLET_DIR, "Storage");

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.print("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        try {
            HEAD_FILE.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            STORAGE_FILE.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Storge gitStorge = new Storge();
        gitStorge.saveStorge();
        Commit initCommit = new Commit("initial commit", null, null);
        initCommit.saveCommit();
        Branch master = new Branch("master", initCommit.getCommitID());
        master.save();
        Head head = new Head(initCommit.getCommitID(), "master");
        head.save();
    }
}
