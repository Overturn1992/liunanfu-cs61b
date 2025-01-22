package gitlet;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.print("Please enter a command.");
            return;
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "merge":
                if(!isInit()){
                    System.out.print("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length != 2) {
                    System.out.print("Incorrect operands.");
                    return;
                }
                API.merge(args[1]);
                break;
            case "reset":
                if(!isInit()){
                    System.out.print("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length != 2) {
                    System.out.print("Incorrect operands.");
                    return;
                }
                API.reset(args[1]);
                break;
            case "rm-branch":
                if(!isInit()){
                    System.out.print("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length != 2) {
                    System.out.print("Incorrect operands.");
                    return;
                }
                API.rm_branch(args[1]);
                break;
            case "checkout":
                if(!isInit()){
                    System.out.print("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length < 2) {
                    System.out.print("Incorrect operands.");
                    return;
                }
                if (args.length == 3) {
                    if (!args[1].equals("--")) {
                        System.out.print("Incorrect operands.");
                        return;
                    }
                    API.checkout_File(Head.loadHead().getCurrCommit(), args[2]);
                }
                if (args.length == 4) {
                    if (!args[2].equals("--")) {
                        System.out.print("Incorrect operands.");
                        return;
                    }
                    API.checkout_File(args[1], args[3]);
                }
                if (args.length == 2) {
                    API.checkout_Branch(args[1]);
                }
                break;
            case "branch":
                if(!isInit()){
                    System.out.print("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length != 2) {
                    System.out.print("Incorrect operands.");
                    return;
                }
                API.branch(args[1]);
                break;
            case "status":
                if(!isInit()){
                    System.out.print("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length != 1) {
                    System.out.print("Incorrect operands.");
                    return;
                }
                API.status();
                break;
            case "find":
                if(!isInit()){
                    System.out.print("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length != 2) {
                    System.out.print("Incorrect operands.");
                    return;
                }
                API.find(args[1]);
                break;
            case "global-log":
                if(!isInit()){
                    System.out.print("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length != 1) {
                    System.out.print("Incorrect operands.");
                    return;
                }
                API.global_log();
                break;
            case "log":
                if(!isInit()){
                    System.out.print("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length != 1) {
                    System.out.print("Incorrect operands.");
                    return;
                }
                API.log();
                break;
            case "rm":
                if(!isInit()){
                    System.out.print("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length != 2) {
                    System.out.print("Incorrect operands.");
                    return;
                }
                API.remove(args[1]);
                break;
            case "commit":
                if(!isInit()){
                    System.out.print("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length != 2) {
                    System.out.print("Incorrect operands.");
                    return;
                }
                API.commit(args[1]);
                break;
            case "init":
                if (args.length != 1) {
                    System.out.print("Incorrect operands.");
                    return;
                }
                API.init();
                break;
            case "add":
                if(!isInit()){
                    System.out.print("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length != 2) {
                    System.out.print("Incorrect operands.");
                    return;
                }
                API.add(args[1]);
                break;
            default:
                System.out.print("No command with that name exists.");
        }
    }

    private static boolean isInit() {
        return Repository.GITLET_DIR.exists();
    }
}
