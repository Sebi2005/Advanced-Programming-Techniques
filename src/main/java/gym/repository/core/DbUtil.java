package gym.repository.core;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DbUtil {
    private DbUtil() {}
    public static String sqliteUrl(String location, String dataBaseFileName) {
        try {
            Path directory = (location == null || location.isBlank())
                    ? Path.of(".")
                    : Path.of(location.trim());
            if (Files.notExists(directory)) {
                Files.createDirectories(directory);
            }
            String file = (dataBaseFileName == null || dataBaseFileName.isBlank()) ? "gym.db" : dataBaseFileName.trim();

            Path dbPath = directory.resolve(file); //joins the directory and file
            return "jdbc:sqlite:" + dbPath.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to prepare DB directory", e);
        }
    }
}
