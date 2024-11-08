package ru.expensive.api.file;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.expensive.api.file.impl.friend.FriendFile;
import ru.expensive.api.file.impl.macro.MacroFile;
import ru.expensive.api.file.impl.module.ModuleFile;
import ru.expensive.core.Expensive;

import java.util.ArrayList;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileRepository {
    List<ClientFile> clientFiles = new ArrayList<>();

    public void setup(Expensive expensive) {
        register(
                new MacroFile(expensive.getMacroRepository()),
                new FriendFile(),
                new ModuleFile(expensive.getModuleRepository()));
    }

    public void register(ClientFile... clientFIle) {
        clientFiles.addAll(List.of(clientFIle));
    }
}
