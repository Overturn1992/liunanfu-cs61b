package gitlet;

import java.io.File;
import java.io.Serializable;

public class Branch implements Serializable {
    private String BranchName;
    private String BranchHead;

    public Branch(String BranchName, String BranchHead) {
        this.BranchName = BranchName;
        this.BranchHead = BranchHead;
    }

    public void save() {
        File output = Utils.join(Repository.BRANCHES_DIR, BranchName);
        Utils.writeObject(output, this);
    }

    public static Branch branchLoad(String BranchName) {
        File aimFile = Utils.join(Repository.BRANCHES_DIR, BranchName);
        if(!aimFile.exists()) {
            return null;
        }
        return Utils.readObject(aimFile, Branch.class);
    }

    public void changeHead(String BranchHead) {
        this.BranchHead = BranchHead;
    }

    public String getBranchHead() {
        return BranchHead;
    }

    public String getBranchName() {
        return BranchName;
    }
}
