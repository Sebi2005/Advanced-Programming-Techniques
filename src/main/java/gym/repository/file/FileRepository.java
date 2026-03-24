package gym.repository.file;
import gym.common.RepositoryException;
import gym.domain.Identifiable;
import gym.repository.core.FilteredRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public abstract class FileRepository<ID, T extends Identifiable<ID>> extends FilteredRepository<ID, T>{
    protected final Path path;
    protected FileRepository(String filePath) {
        try {
            this.path = Path.of(filePath); //sets up the file path
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent() == null ? Path.of(".") : path.getParent()); //ensures to create either the parent folder or current folder(.)
                Files.createFile(path);
            }
            loadFromFile();
        } catch(Exception e) {
            throw new RepositoryException("Failed to initialize file repository: "+filePath, e);
        }
    }
    protected abstract void loadFromFile();
    protected abstract void saveToFile();
    @Override public void create(T entity) {super.create(entity); saveToFile();}
    @Override public void update(T entity) {super.update(entity); saveToFile();}
    @Override public Optional<T> delete(ID id) {
        Optional<T> removed = super.delete(id);
        saveToFile();
        return removed;}
}
