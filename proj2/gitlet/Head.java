package gitlet;

import java.io.Serializable;

public class Head implements Serializable {
    private String currCommit;
    private String currBranch;

    public Head(String currCommit, String currBranch) {
        this.currBranch = currBranch;
        this.currCommit = currCommit;
    }

    public void save() {
        Utils.writeObject(Repository.HEAD_FILE, this);
    }

    public static Head loadHead() {
        return Utils.readObject(Repository.HEAD_FILE, Head.class);
    }

    public void changeCommit(String newCommit) {
        currCommit = newCommit;
    }

    public void changeBranch(String newBranch) {
        currBranch = newBranch;
    }

    public String getCurrCommit() {
        return currCommit;
    }

    public String getCurrBranch() {
        return currBranch;
    }

}
