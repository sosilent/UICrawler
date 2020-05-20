package util.uipath;

public class MetadataUtil {

    /**
     *
     * @param activityName
     * @return metadata by yml format
     */
    public static String genMetadata(String activityName) {
        StringBuilder metadata = new StringBuilder();
        metadata.append("activity_name: ").append(activityName).append("\n");
        return metadata.toString();
    }
}
