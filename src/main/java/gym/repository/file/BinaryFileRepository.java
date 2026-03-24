package gym.repository.file;
import gym.common.RepositoryException;
import gym.domain.Identifiable;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
public class BinaryFileRepository<ID, T extends Identifiable<ID>> extends FileRepository<ID, T> {
    public BinaryFileRepository(String filePath) {super(filePath);}

    @SuppressWarnings("unchecked")
    @Override
    protected void loadFromFile() { //deserialization
        try (java.io.InputStream fis = java.nio.file.Files.newInputStream(path)) {

            // If file is empty, nothing to load
            if (fis.available() == 0) {
                return;
            }


            try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(fis)) {
                Object obj = ois.readObject();
                if (obj instanceof java.util.Map<?, ?> map) {
                    this.data.clear();
                    this.data.putAll((java.util.Map<ID, T>) map);
                }
            }

        } catch (java.io.FileNotFoundException ignored) {

        } catch (java.io.EOFException ignored) {
            //
        } catch (Exception e) {
            throw new gym.common.RepositoryException("Failed to read binary file: " + path, e);
        }
    }
    @Override
    protected void saveToFile() { //serialization
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            objectOutputStream.writeObject(new HashMap<>(this.data));
        } catch (Exception e) {
            throw new RepositoryException("Failed to write binary file: "+path, e);
        }
    }

}
