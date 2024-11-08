package ru.expensive.api.file.impl.friend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.expensive.api.file.ClientFile;
import ru.expensive.api.file.exception.FileLoadException;
import ru.expensive.api.file.exception.FileSaveException;
import ru.expensive.api.repository.friend.Friend;
import ru.expensive.api.repository.friend.FriendRepository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendFile extends ClientFile {

    public FriendFile() {
        super("friends");
    }

    @Override
    public void saveToFile(File path) throws FileSaveException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(path, getName() + ".json");

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(FriendRepository.getFriends(), writer);
        } catch (JsonIOException | IOException e) {
            throw new FileSaveException(String.format("Failed to save %s to file", getName()), e);
        }
    }

    @Override
    public void loadFromFile(File path) throws FileLoadException {
        Gson gson = new Gson();
        File file = new File(path, getName() + ".json");

        try (FileReader reader = new FileReader(file)) {
            Friend[] friends = gson.fromJson(reader, Friend[].class);
            FriendRepository.clear();
            FriendRepository.getFriends().addAll(Arrays.asList(friends));
        } catch (IOException e) {
            throw new FileLoadException(String.format("Failed to load %s from file", getName()), e);
        } catch (JsonSyntaxException e) {
            throw new FileLoadException(String.format("JSON syntax error, %s config cannot be loaded", getName()), e);
        } catch (JsonIOException e) {
            throw new FileLoadException(String.format("JSON IO error, %s config cannot be loaded", getName()), e);
        }
    }
}
