import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ArgParser {

    private boolean hasArgsAssigned = false;
    private HashMap<String, String> arguments;

    private static ArgParser instance = null;


    public static ArgParser getInstance() {
        return getInstance(null);
    }

    public static ArgParser getInstance(String[] args) {
        if (instance == null) {
            instance = new ArgParser(args);
        }
        return instance;
    }

    public static boolean hasArgs() {
        return getInstance().hasArgsAssigned;
    }

    private ArgParser(String[] args) {
        if (args != null) {
            hasArgsAssigned = true;
            arguments = new HashMap<>();

            if (args.length % 2 != 0) {
                throw new IllegalArgumentException();
            }

            for (int k = 0; k < args.length / 2; k++) {
                String key = args[2*k].split("-")[1];
                arguments.put(key, args[2*k+1]);
            }
        }
    }

    /**
     * Fetches hesh values by their key
     * @param key
     * @return
     */
    public String getHashValue(String key) {
        return arguments.get(key);
    }

    public static String getDatasetName() {
        return getInstance().getHashValue("d");
    }

    public static SimilarityTask.Types getSimTask() {
        String taskName = getInstance().getHashValue("task");
        return SimilarityTask.Types.TypeById(Integer.parseInt(taskName));
    }

    public static boolean useColorCues() {
        String useColorState = getInstance().getHashValue("color");
        if (useColorState == null || !useColorState.equals("1")) {
            return false;
        }
        return true;
    }

    public static boolean useLocalVariance() {
        String useVarState = getInstance().getHashValue("var");
        if (useVarState == null || !useVarState.equals("1")) {
            return false;
        }
        return true;
    }

    public String toString() {
        String msg = "";
        Iterator it = arguments.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            msg += "-" + pair.getKey() + " " + pair.getValue() + " ";
        }
        return msg.trim();
    }

    public static void reportUsedParameters() {
        System.out.print("Provided runtime args: ");
        System.out.println(getInstance().toString());
        System.out.println("Using the following runtime parameter setting:");
        System.out.println("+ Using dataset: " + getDatasetName());
        System.out.println("+ Running task: " + getSimTask().name());
        System.out.println("+ Using local variances: " + useLocalVariance());
        System.out.println("+ Using color cues: " + useColorCues());
    }


}
