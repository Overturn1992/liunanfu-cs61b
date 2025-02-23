package gitlet;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class API {
    public static void add(String args) {
        Storge currStorge = Storge.loadStorge();
        currStorge.addFile(args);
        currStorge.saveStorge();
    }

    public static void init() {
        Repository.init();
    }

    public static void commit(String args) {
        Storge currStorge = Storge.loadStorge();
        if (currStorge.getAddStorge().isEmpty() && currStorge.getRemoveStorge().isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        if (args.isBlank()) {
            System.out.println("Please enter a commit message.");
            return;
        }
        Head currHead = Head.loadHead();
        Commit newCommit = new Commit(args, currHead.getCurrCommit(), null, currHead.getCurrBranch());
        currHead.changeCommit(newCommit.getCommitID());
        currHead.save();
        Branch currBranch = Branch.branchLoad(currHead.getCurrBranch());
        currBranch.changeHead(currHead.getCurrCommit());
        currBranch.save();
        newCommit.saveCommit();
    }

    public static void remove(String args) {
        Storge currStorge = Storge.loadStorge();
        currStorge.remove(args);
        currStorge.saveStorge();
    }

    public static void log() {
        Commit currCommit = Commit.loadCommit(Head.loadHead().getCurrCommit());
        while (currCommit.getParent1() != null) {
            if (currCommit.getParent2() == null) {
                System.out.println("===");
                System.out.println("commit" + " " + currCommit.getCommitID());
                System.out.println("Date:" + " " + currCommit.getTimeStamp());
                System.out.println(currCommit.getMessage());
            } else {
                System.out.println("===");
                System.out.println("commit" + " " + currCommit.getCommitID());
                System.out.println("Merge:" + " " + currCommit.getParent1().substring(0, 7) + " " + currCommit.getParent2().substring(0, 7));
                System.out.println("Date:" + " " + currCommit.getTimeStamp());
                System.out.println(currCommit.getMessage());
            }
            System.out.println();
            currCommit = Commit.loadCommit(currCommit.getParent1());
        }
        System.out.println("===");
        System.out.println("commit" + " " + currCommit.getCommitID());
        System.out.println("Date:" + " " + currCommit.getTimeStamp());
        System.out.println(currCommit.getMessage());
        System.out.println();
    }

    public static void global_log() {
        List<String> commitSet = Utils.plainFilenamesIn(Repository.COMMITS_DIR);
        if (commitSet == null) {
            return;
        }
        for (String commit : commitSet) {
            Commit currCommit = Commit.loadCommit(commit);
            if (currCommit.getParent2() == null) {
                System.out.println("===");
                System.out.println("commit" + " " + currCommit.getCommitID());
                System.out.println("Date:" + " " + currCommit.getTimeStamp());
                System.out.println(currCommit.getMessage());
            } else {
                System.out.println("===");
                System.out.println("commit" + " " + currCommit.getCommitID());
                System.out.println("Merge:" + " " + currCommit.getParent1().substring(0, 7) + " " + currCommit.getParent2().substring(0, 7));
                System.out.println("Date:" + " " + currCommit.getTimeStamp());
                System.out.println(currCommit.getMessage());
            }
            System.out.println();
        }
    }

    public static void find(String args) {
        List<String> commitSet = Utils.plainFilenamesIn(Repository.COMMITS_DIR);
        if (commitSet == null) {
            return;
        }
        boolean flag = false;
        for (String commit : commitSet) {
            Commit currCommit = Commit.loadCommit(commit);
            if (currCommit.getMessage().equals(args)) {
                System.out.println(currCommit.getCommitID());
                flag = true;
            }
        }
        if (!flag) {
            System.out.print("Found no commit with that message.");
        }
    }

    public static void status() {
        System.out.println("=== Branches ===");
        Head currHead = Head.loadHead();
        List<String> branchSet = Utils.plainFilenamesIn(Repository.BRANCHES_DIR);
        if (branchSet != null) {
            for (String branch : branchSet) {
                if (branch.equals(currHead.getCurrBranch())) {
                    System.out.println("*" + branch);
                } else {
                    System.out.println(branch);
                }
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        Storge currStorge = Storge.loadStorge();
        for (String add : currStorge.getAddStorge().keySet()) {
            System.out.println(add);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String remove : currStorge.getRemoveStorge()) {
            System.out.println(remove);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        List<String> fileSet = Utils.plainFilenamesIn(Repository.CWD);
        Commit currCommit = Commit.loadCommit(currHead.getCurrCommit());
        Set<String> printSet = new TreeSet<>();
        if (fileSet != null) {
            for (Map.Entry<String, String> file : currCommit.getBlobs().entrySet()) {
                if (!fileSet.contains(file.getKey())) {
                    if (!currStorge.getRemoveStorge().contains(file.getKey())) {
                        printSet.add(file.getKey() + " (deleted)");
                    }
                    continue;
                }
                Blob testBlob = new Blob(file.getKey());
                if (!file.getValue().equals(testBlob.getSHA1())) {
                    if (!currStorge.getAddStorge().containsKey(file.getKey()) || !currStorge.getAddStorge().get(file.getKey()).equals(testBlob.getSHA1())) {
                        printSet.add(file.getKey() + " (modified)");
                    }
                }
            }
            for (Map.Entry<String, String> file : currStorge.getAddStorge().entrySet()) {
                if (!fileSet.contains(file.getKey())) {
                    printSet.add(file.getKey() + " (deleted)");
                    continue;
                }
                Blob testBlob = new Blob(file.getKey());
                if (!file.getValue().equals(testBlob.getSHA1())) {
                    printSet.add(file.getKey() + " (modified)");
                }
            }
            for (String output : printSet) {
                System.out.println(output);
            }
        }
        printSet.clear();
        System.out.println();
        System.out.println("=== Untracked Files ===");
        if (fileSet != null) {
            for (String file : fileSet) {
                if (!currCommit.getBlobs().containsKey(file) && !currStorge.getAddStorge().containsKey(file)) {
                    printSet.add(file);
                }
            }
            for (String output : printSet) {
                System.out.println(output);
            }
        }
        System.out.println();
    }

    public static void branch(String args) {
        List<String> branchSet = Utils.plainFilenamesIn(Repository.BRANCHES_DIR);
        if (branchSet == null) {
            return;
        }
        if (branchSet.contains(args)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        Head currHead = Head.loadHead();
        Branch newBranch = new Branch(args, currHead.getCurrCommit());
        newBranch.save();
    }

    public static void checkout_File(String args1, String args2) {
        if (args1.length() < 40) {
            List<String> commitSet = Utils.plainFilenamesIn(Repository.COMMITS_DIR);
            for (String commit : commitSet) {
                if (commit.startsWith(args1)) {
                    args1 = commit;
                    break;
                }
            }
        }
        Commit aimCommit = Commit.loadCommit(args1);
        if (aimCommit == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        if (!aimCommit.getBlobs().containsKey(args2)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        Blob aimBlob = Blob.loadBlob(aimCommit.getBlobs().get(args2));
        Utils.writeContents(Utils.join(Repository.CWD, args2), aimBlob.getContent());
    }

    public static void checkout_Branch(String args) {
        Branch aimBranch = Branch.branchLoad(args);
        if (aimBranch == null) {
            System.out.println("No such branch exists.");
            return;
        }
        if (args.equals(Head.loadHead().getCurrBranch())) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Commit aimCommit = Commit.loadCommit(aimBranch.getBranchHead());
        Commit currCommit = Commit.loadCommit(Head.loadHead().getCurrCommit());
        checkout_Commit(aimCommit, currCommit);
        Head currHead = Head.loadHead();
        currHead.changeBranch(aimBranch.getBranchName());
        currHead.changeCommit(aimCommit.getCommitID());
        currHead.save();
        Storge currStorge = Storge.loadStorge();
        currStorge.clearStorge();
        currStorge.saveStorge();
    }

    public static boolean checkout_Commit(Commit aimCommit, Commit currCommit) {
        List<String> fileList = Utils.plainFilenamesIn(Repository.CWD);
        if (fileList != null) {
            Set<String> fileSet = new TreeSet<String>();
            for (String file : fileList) {
                if (!currCommit.getBlobs().containsKey(file)) {
                    fileSet.add(file);
                }
            }
            for (String file : fileSet) {
                if (aimCommit.getBlobs().containsKey(file)) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    return false;
                }
            }
            for (String file : currCommit.getBlobs().keySet()) {
                File deleteFile = Utils.join(Repository.CWD, file);
                if (!deleteFile.exists()) {
                    continue;
                }
                Utils.restrictedDelete(deleteFile);
            }
            for (Map.Entry<String, String> file : aimCommit.getBlobs().entrySet()) {
                Utils.writeContents(Utils.join(Repository.CWD, file.getKey()), Blob.loadBlob(file.getValue()).getContent());
            }
            return true;
        }
        return false;
    }

    public static void rm_branch(String args) {
        Branch aimBranch = Branch.branchLoad(args);
        if (aimBranch == null) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (aimBranch.getBranchName().equals(Head.loadHead().getCurrBranch())) {
            System.out.println("Cannot remove the current branch.");
        }
        Utils.join(Repository.BRANCHES_DIR, aimBranch.getBranchName()).delete();
    }

    public static void reset(String args) {
        if (args.length() < 40) {
            List<String> commitSet = Utils.plainFilenamesIn(Repository.COMMITS_DIR);
            for (String commit : commitSet) {
                if (commit.startsWith(args)) {
                    args = commit;
                    break;
                }
            }
        }
        Commit aimCommit = Commit.loadCommit(args);
        if (aimCommit == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Head currHead = Head.loadHead();
        Commit currCommit = Commit.loadCommit(currHead.getCurrCommit());
        checkout_Commit(aimCommit, currCommit);
        Branch aimBranch = Branch.branchLoad(aimCommit.getBranchName());
        aimBranch.changeHead(aimCommit.getCommitID());
        aimBranch.save();
        Storge currStorge = Storge.loadStorge();
        currStorge.clearStorge();
        currStorge.saveStorge();
        currHead.changeCommit(aimCommit.getCommitID());
        if (!currHead.getCurrBranch().equals(aimCommit.getBranchName())) {
            currHead.changeBranch(aimBranch.getBranchName());
        }
        currHead.save();
    }

    private static int changeType(Commit textCommit, String fileName, String commonKey) {
        if (!textCommit.getBlobs().containsKey(fileName)) {
            return 1;
        }
        if (!textCommit.getBlobs().get(fileName).equals(commonKey)) {
            return 2;
        }
        if (textCommit.getBlobs().get(fileName).equals(commonKey)) {
            return 3;
        }
        return 0;
    }

    public static void merge(String args) {
        Branch aimBranch = Branch.branchLoad(args);
        if (aimBranch == null) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        Branch currBranch = Branch.branchLoad(Head.loadHead().getCurrBranch());
        if (currBranch.getBranchName().equals(aimBranch.getBranchName())) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        Storge currStorge = Storge.loadStorge();
        if (!currStorge.getAddStorge().isEmpty() || !currStorge.getRemoveStorge().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        Commit aimCommit = Commit.loadCommit(aimBranch.getBranchHead());
        Commit currCommit = Commit.loadCommit(currBranch.getBranchHead());
        Queue<String> bfsQueue = new LinkedList<>();
        HashSet<String> currParentSet = new HashSet<>();
        bfsQueue.add(currCommit.getCommitID());
        currParentSet.add(currCommit.getCommitID());
        while (!bfsQueue.isEmpty()) {
            Commit qCommit = Commit.loadCommit(bfsQueue.poll());
            if (qCommit.getParent1() != null) {
                currParentSet.add(qCommit.getParent1());
                bfsQueue.add(qCommit.getParent1());
            }
            if (qCommit.getParent2() != null) {
                currParentSet.add(qCommit.getParent2());
                bfsQueue.add(qCommit.getParent2());
            }
        }
        bfsQueue.add(aimCommit.getCommitID());
        Commit commonCommit = null;
        while (!bfsQueue.isEmpty()) {
            Commit qCommit = Commit.loadCommit(bfsQueue.poll());
            if (currParentSet.contains(qCommit.getCommitID())) {
                commonCommit = Commit.loadCommit(qCommit.getCommitID());
                break;
            } else {
                if (qCommit.getParent1() != null) {
                    bfsQueue.add(qCommit.getParent1());
                }
                if (qCommit.getParent2() != null) {
                    bfsQueue.add(qCommit.getParent2());
                }
            }
        }
        if (commonCommit.getCommitID().equals(aimCommit.getCommitID())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        if (commonCommit.getCommitID().equals(currCommit.getCommitID())) {
            checkout_Branch(aimCommit.getBranchName());
            System.out.println("Current branch fast-forwarded");
            return;
        }
        for (Map.Entry<String, String> file : aimCommit.getBlobs().entrySet()) {
            if (!commonCommit.getBlobs().containsKey(file.getKey()) && !currCommit.getBlobs().containsKey(file.getKey())) {
                currStorge.unsafeAdd(file.getKey(), file.getValue());
                continue;
            }
            String commonCommit_fileKey = commonCommit.getBlobs().get(file.getKey());
            if (commonCommit.getBlobs().containsKey(file.getKey())) {
                if (file.getValue().equals(commonCommit_fileKey) && !currCommit.getBlobs().containsKey(file.getKey())) {
                    continue;
                }
                if (!file.getValue().equals(commonCommit_fileKey) && currCommit.getBlobs().containsKey(file.getKey()) && currCommit.getBlobs().get(file.getKey()).equals(commonCommit_fileKey)) {
                    currStorge.unsafeAdd(file.getKey(), file.getValue());
                    continue;
                }
            }
        }
        for (Map.Entry<String, String> file : currCommit.getBlobs().entrySet()) {
            if (!commonCommit.getBlobs().containsKey(file.getKey()) && !aimCommit.getBlobs().containsKey(file.getKey())) {
                continue;
            }
            String commonCommit_fileKey = commonCommit.getBlobs().get(file.getKey());
            if (commonCommit.getBlobs().containsKey(file.getKey()) && file.getValue().equals(commonCommit_fileKey) && !aimCommit.getBlobs().containsKey(file.getKey())) {
                currStorge.remove(file.getKey());
                continue;
            }
            if (!file.getValue().equals(commonCommit_fileKey) && aimCommit.getBlobs().containsKey(file.getKey()) && aimCommit.getBlobs().get(file.getKey()).equals(commonCommit_fileKey)) {
                continue;
            }
        }
        HashSet<String> ConflictSet = new HashSet<>();
        for (Map.Entry<String, String> file : commonCommit.getBlobs().entrySet()) {
            int status_curr = changeType(currCommit, file.getKey(), file.getValue());
            int status_aim = changeType(aimCommit, file.getKey(), file.getValue());
            if (status_curr == status_aim) {
                if (status_curr == 1 || status_curr == 3) {
                    continue;
                }
                if (status_curr == 2) {
                    if (!currCommit.getBlobs().get(file.getKey()).equals(aimCommit.getBlobs().get(file.getKey()))) {
                        ConflictSet.add(file.getKey());
                    }
                }
            } else {
                if (status_curr != 3 && status_aim != 3) {
                    ConflictSet.add(file.getKey());
                }
            }
        }
        HashSet<Blob> toSave = new HashSet<>();
        for (String file : ConflictSet) {
            Blob newBlob;
            if (currCommit.getBlobs().containsKey(file)) {
                newBlob = Blob.loadBlob(currCommit.getBlobs().get(file));
            } else {
                newBlob = Blob.loadBlob(aimCommit.getBlobs().get(file));
            }
            String currContent = "";
            if (currCommit.getBlobs().containsKey(file)) {
                currContent = new String(Blob.loadBlob(currCommit.getBlobs().get(file)).getContent());
            }
            String aimContent = "";
            if (aimCommit.getBlobs().containsKey(file)) {
                aimContent = new String(Blob.loadBlob(aimCommit.getBlobs().get(file)).getContent());
            }
            String newContent = "<<<<<<< HEAD\n" + currContent + "=======\n" + aimContent + ">>>>>>>\n";
            newBlob.changeContent(newContent.getBytes(StandardCharsets.UTF_8));
            currStorge.unsafeAdd(file, newBlob.getSHA1());
            toSave.add(newBlob);
        }
        String message = "Merged" + " " + aimBranch.getBranchName() + " " + "into" + " " + currBranch.getBranchName() + ".";
        currStorge.saveStorge();
        for (Blob blob : toSave) {
            blob.save();
        }
        Commit mergeCommit = new Commit(message, currCommit.getCommitID(), aimCommit.getCommitID(), currBranch.getBranchName());
        if (checkout_Commit(mergeCommit, currCommit)) {
            if (!ConflictSet.isEmpty()) {
                System.out.println("Encountered a merge conflict.");
            }

            mergeCommit.saveCommit();
            currBranch.changeHead(mergeCommit.getCommitID());
            currBranch.save();
            Head currHead = Head.loadHead();
            currHead.changeCommit(mergeCommit.getCommitID());
            currHead.save();
            currStorge.clearStorge();
            currStorge.saveStorge();
        } else {
            for (Blob blob : toSave) {
                Utils.restrictedDelete(Utils.join(Repository.BLOBS_DIR, blob.getSHA1()));
            }
            currStorge.clearStorge();
            currStorge.saveStorge();
            reset(currCommit.getCommitID());
        }
    }

    public static void addRemote(String args1, String args2) {
        List<String> remoteNames = Utils.plainFilenamesIn(Repository.REMOTE_DIR);
        if (remoteNames.contains(args1)) {
            System.out.println("A remote with that name already exists.");
            return;
        }
        args2 = args2.replace("/", File.separator);
        Remote newRemote = new Remote(args1, args2);
        newRemote.save();
    }

    public static void removeRemote(String args) {
        List<String> remoteNames = Utils.plainFilenamesIn(Repository.REMOTE_DIR);
        if (!remoteNames.contains(args)) {
            System.out.println("A remote with that name does not exist.");
            return;
        }
        Utils.join(Repository.REMOTE_DIR, args).delete();
    }

    public static void push(String args1, String args2) {
        List<String> remoteNames = Utils.plainFilenamesIn(Repository.REMOTE_DIR);
        if (!remoteNames.contains(args1)) {
            System.out.println("A remote with that name does not exist.");
            return;
        }
        Remote aimRemote = Remote.load(args1);
        File aimPath = new File(aimRemote.getRemotePath());
        if (!aimPath.exists()) {
            System.out.println("Remote directory not found.");
            return;
        }
        File remoteBranch_DIR = Utils.join(aimPath, "Branches");
        File remoteObjects_DIR = Utils.join(aimPath, "Objects");
        File remoteCommits_DIR = Utils.join(remoteObjects_DIR, "Commits");
        File remoteBlobs_DIR = Utils.join(remoteObjects_DIR, "Blobs");
        Branch remoteBranch = Utils.readObject(Utils.join(remoteBranch_DIR, args2), Branch.class);
        Head currHead = Head.loadHead();
        List<String> CommitSet = new ArrayList<>();
        Commit currCommit = Commit.loadCommit(currHead.getCurrCommit());
        Commit ptrCommit=currCommit;
        boolean flag=false;
        while(ptrCommit.getParent1()!=null) {
            if(ptrCommit.getCommitID().equals(remoteBranch.getBranchHead())){
                flag=true;
                break;
            }
            CommitSet.add(ptrCommit.getCommitID());
            ptrCommit=Commit.loadCommit(ptrCommit.getParent1());
        }
        if (!flag) {
            System.out.println("Please pull down remote changes before pushing.");
            return;
        }
        for(String toAdd : CommitSet) {
            Commit toAddCommit = Commit.loadCommit(toAdd);
            File newCommit=Utils.join(remoteCommits_DIR,toAddCommit.getCommitID());
            Utils.writeObject(newCommit,currCommit);
            for(Map.Entry<String,String> blob:toAddCommit.getBlobs().entrySet()) {
                File newBlob=Utils.join(remoteBlobs_DIR,blob.getValue());
                Blob replaceBlob=Blob.loadBlob(blob.getValue());
                Utils.writeObject(newBlob,replaceBlob);
            }
        }
        remoteBranch.changeHead(currCommit.getCommitID());
        Utils.writeObject(Utils.join(remoteBranch_DIR, args2),Branch.class);
        Head remoteHead=Utils.readObject(Utils.join(aimPath, "Head"), Head.class);
        remoteHead.changeCommit(currCommit.getCommitID());
        remoteHead.changeBranch(args2);
        Utils.writeObject(Utils.join(aimPath,"Head"),Head.class);
    }

    public static void fetch(String args1, String args2) {
        Remote aimRemote = Remote.load(args1);
        File aimPath = new File(aimRemote.getRemotePath());
        if (!aimPath.exists()) {
            System.out.println("Remote directory not found.");
            return;
        }
        File remoteBranch_DIR = Utils.join(aimPath, "Branches");
        File remoteObjects_DIR = Utils.join(aimPath, "Objects");
        File remoteCommits_DIR = Utils.join(remoteObjects_DIR, "Commits");
        File remoteBlobs_DIR = Utils.join(remoteObjects_DIR, "Blobs");
        List<String> RemoteBranches = Utils.plainFilenamesIn(remoteBranch_DIR);
        if(!RemoteBranches.contains(args2)){
            System.out.println("That remote does not have that branch.");
            return;
        }

    }
}
