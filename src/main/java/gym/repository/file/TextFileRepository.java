package gym.repository.file;
import gym.common.RepositoryException;
import gym.domain.Identifiable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.util.StringJoiner;


public abstract class TextFileRepository<ID, T extends Identifiable<ID>> extends FileRepository<ID, T> {
    protected TextFileRepository(String filePath) {super(filePath);}
    protected abstract T fromLine(String line);
    protected abstract String toLine(T entity);
    @Override
    protected void loadFromFile() {
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            bufferedReader.lines().filter(line -> !line.isBlank()).forEach(line -> {
                T entity = fromLine(line.trim());
                data.put(entity.getId(), entity);
            });
        } catch (Exception e) {
            throw new RepositoryException("Failed to read the text file: "+path, e);
        }
    }
    @Override
    protected void saveToFile() {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
            StringJoiner stringJoiner = new StringJoiner(System.lineSeparator()); //lineSeparator assures that data added will be on new line
            for (T entity : data.values()) stringJoiner.add(toLine(entity));
            bufferedWriter.write(stringJoiner.toString());
        } catch (Exception e) {
            throw new RepositoryException("Failed to write text file: "+path, e);
        }
    }
}
