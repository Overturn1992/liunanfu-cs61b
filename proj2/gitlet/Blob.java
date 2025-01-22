package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.join;

public class Blob implements Serializable {
    private String fileName;
    private byte[] content;

    public Blob(String fileName) {
        this.fileName = fileName;
        this.content = readContent();
    }

    private byte[] readContent() {
        File aimFile = join(Repository.CWD, this.fileName);
        return Utils.readContents(aimFile);
    }

    public static Blob loadBlob(String fileName) {
        return Utils.readObject(join(Repository.BLOBS_DIR, fileName), Blob.class);
    }

    public String getFileName() {
        return this.fileName;
    }

    public byte[] getContent() {
        return this.content;
    }

    public String getSHA1() {
        return Utils.sha1(this.fileName, this.content);
    }

    public void save() {
        File outputFile = join(Repository.BLOBS_DIR, getSHA1());
        Utils.writeObject(outputFile, this);
    }

    public static Blob load(String blobID){
        if(!join(Repository.BLOBS_DIR,blobID).exists()){
            return null;
        }
        return Utils.readObject(join(Repository.BLOBS_DIR, blobID), Blob.class);
    }

    public void changeContent(byte[] content) {
        this.content = content;
    }
}
