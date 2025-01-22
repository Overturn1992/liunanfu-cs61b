package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.join;

public class Storge implements Serializable {
    private Set<String> removeStorge;
    private Map<String, String> addStorge;

    public Storge() {
        removeStorge = new TreeSet<String>();
        addStorge = new TreeMap<String, String>();
    }

    public Set<String> getRemoveStorge() {
        return this.removeStorge;
    }

    public Map<String, String> getAddStorge() {
        return this.addStorge;
    }

    public static Storge loadStorge() {
        return Utils.readObject(Repository.STORAGE_FILE, Storge.class);
    }

    public void saveStorge() {
        Utils.writeObject(Repository.STORAGE_FILE, this);
    }

    public void unsafeAdd(String blobName,String blobKey){
        this.addStorge.put(blobName,blobKey);
    }

    public void addFile(String FileName) {
        File aimFile = join(Repository.CWD, FileName);
        if (!aimFile.exists() && !removeStorge.contains(FileName)) {
            System.out.print("File does not exist.");
            return;
        }
        if (removeStorge.contains(FileName)) {
            removeStorge.remove(FileName);
            if(aimFile.exists()) {
                addFile(FileName);
            }
            return;
        }
        Blob newBlob = new Blob(FileName);
        Commit currCommit = Commit.loadCommit(Head.loadHead().getCurrCommit());
        if (currCommit.getBlobs().containsKey(FileName)) {
            if(newBlob.getSHA1().equals(currCommit.getBlobs().get(FileName))) {
                addStorge.remove(FileName);
                return;
            }
        }
        newBlob.save();
        addStorge.put(FileName, newBlob.getSHA1());
    }

    public void clearStorge() {
        removeStorge.clear();
        addStorge.clear();
    }

    public void remove(String FileName) {
        Commit currCommit = Commit.loadCommit(Head.loadHead().getCurrCommit());
        if (!addStorge.containsKey(FileName) && !currCommit.getBlobs().containsKey(FileName)) {
            System.out.print("No reason to remove the file.");
            return;
        }
        if (addStorge.containsKey(FileName)) {
            addStorge.remove(FileName);
        }
        if (currCommit.getBlobs().containsKey(FileName)) {
            removeStorge.add(FileName);
            File aimFile = join(Repository.CWD, FileName);
            if (aimFile.exists()) {
                Utils.restrictedDelete(FileName);
            }
        }
    }
}
