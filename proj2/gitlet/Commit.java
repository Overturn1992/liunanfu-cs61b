package gitlet;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;


public class Commit implements Serializable {

    private String message;
    private String parent1;
    private String parent2;
    private String timeStamp;
    private String commitID;
    private String branchName;
    private Map<String, String> BlobsMap = new HashMap<>();

    public String TimeStampFormat() {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(new Date());
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getParent2() {
        if (parent2 == null) {
            return null;
        }
        return parent2;
    }

    public Commit(String message, String parent1, String parent2 ,String branchName) {
        this.message = message;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.timeStamp = TimeStampFormat();
        this.BlobsMap = new TreeMap<>();
        this.branchName = branchName;
        updateBlob();
        this.commitID = sha1ToCommitID();
    }

    public String getBranchName(){
        return branchName;
    }

    public String sha1ToCommitID() {
        List<Object> vals = new ArrayList<>();
        vals.add(message);
        vals.add(timeStamp);
        vals.add(BlobsMap.toString());
        if (parent1 != null) {
            vals.add(parent1);
        }
        if (parent2 != null) {
            vals.add(parent2);
        }
        return sha1(vals);
    }

    public String getCommitID() {
        return commitID;
    }

    public String getParent1() {
        if (parent1 == null) {
            return null;
        }
        return parent1;
    }

    public static Commit loadCommit(String commitID) {
        if(!join(Repository.COMMITS_DIR,commitID).exists()){
            return null;
        }
        return Utils.readObject(join(Repository.COMMITS_DIR, commitID), Commit.class);
    }

    public void saveCommit() {
        File output = new File(Repository.COMMITS_DIR, commitID);
        Utils.writeObject(output, this);
    }

    public Map<String, String> getBlobs() {
        return this.BlobsMap;
    }

    public String getMessage() {
        return message;
    }

    public void updateBlob() {
        Storge currStorge = Storge.loadStorge();
        if (parent1 == null) {
            return;
        }
        File parentCommit = new File(Repository.COMMITS_DIR, parent1);
        Commit parent = readObject(parentCommit, Commit.class);
        BlobsMap.putAll(parent.getBlobs());
        for (String remove : currStorge.getRemoveStorge()) {
            if (this.BlobsMap.containsKey(remove)) {
                this.BlobsMap.remove(remove);
            }
        }
        this.BlobsMap.putAll(currStorge.getAddStorge());
        currStorge.clearStorge();
        currStorge.saveStorge();
    }

    public void clearBlobs() {
        this.BlobsMap.clear();
    }

    public void addBlob(String blob,String key) {
        this.BlobsMap.put(blob,key);
    }

    public void reloadCommitID(){
        this.commitID = sha1ToCommitID();
    }
}
