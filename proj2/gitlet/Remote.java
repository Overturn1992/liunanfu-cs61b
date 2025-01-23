package gitlet;

import java.io.Serializable;

public class Remote implements Serializable {
    private String remoteName;
    private String remotePath;
    public Remote(String remoteName, String remotePath) {
        this.remoteName = remoteName;
        this.remotePath = remotePath;
    }

    public String getRemoteName() {
        return remoteName;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void save(){
        Utils.writeObject(Utils.join(Repository.REMOTE_DIR, remoteName), this);
    }

    public static Remote load(String remoteName){
        return Utils.readObject(Utils.join(Repository.REMOTE_DIR, remoteName), Remote.class);
    }
}
