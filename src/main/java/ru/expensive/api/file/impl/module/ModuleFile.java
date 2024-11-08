package ru.expensive.api.file.impl.module;

import com.google.gson.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleRepository;
import ru.expensive.api.feature.module.setting.Setting;
import ru.expensive.api.feature.module.setting.implement.*;
import ru.expensive.api.file.ClientFile;
import ru.expensive.api.file.exception.FileLoadException;
import ru.expensive.api.file.exception.FileSaveException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModuleFile extends ClientFile {
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    ModuleRepository moduleRepository;

    public ModuleFile(ModuleRepository moduleRepository) {
        super("autoCfg");
        this.moduleRepository = moduleRepository;
    }

    @Override
    public void saveToFile(File path) throws FileSaveException {
        saveToFile(path, getName() + ".json");
    }

    @Override
    public void loadFromFile(File path) throws FileLoadException {
        loadFromFile(path, getName() + ".json");
    }

    @Override
    public void saveToFile(File path, String fileName) throws FileSaveException {
        JsonObject functionObject = createJsonObjectFromModules();
        File file = new File(path, fileName);
        writeJsonToFile(functionObject, file);
        super.saveToFile(path, fileName);
    }

    @Override
    public void loadFromFile(File path, String fileName) throws FileLoadException {
        File file = new File(path, fileName);
        JsonObject functionObject = readJsonFromFile(file);
        if (functionObject != null) {
            updateModulesFromJsonObject(functionObject);
        }
        super.loadFromFile(path, fileName);
    }

    private JsonObject createJsonObjectFromModules() {
        JsonObject functionObject = new JsonObject();
        for (Module module : moduleRepository.modules()) {
            JsonObject moduleObject = new JsonObject();
            moduleObject.addProperty("bind", module.getKey());
            moduleObject.addProperty("state", module.isState());
            module.settings().forEach(setting -> addSettingToJsonObject(moduleObject, setting));
            functionObject.add(module.getName().toLowerCase(), moduleObject);
        }

        return functionObject;
    }

    private void addSettingToJsonObject(JsonObject moduleObject, Setting setting) {
        if (setting instanceof BooleanSetting booleanSetting) {
            moduleObject.addProperty(setting.getName(), booleanSetting.isValue());
        }
        if (setting instanceof ValueSetting valueSetting) {
            moduleObject.addProperty(setting.getName(), valueSetting.getValue());
        }
        if (setting instanceof ColorSetting colorSetting) {
            moduleObject.addProperty(setting.getName(), colorSetting.getColor());
        }
        if (setting instanceof BindSetting bindSetting) {
            moduleObject.addProperty(setting.getName(), bindSetting.getKey());
        }
        if (setting instanceof TextSetting textSetting) {
            moduleObject.addProperty(setting.getName(), textSetting.getText());
        }
        if (setting instanceof SelectSetting selectSetting) {
            moduleObject.addProperty(setting.getName(), selectSetting.getSelected());
        }
        if (setting instanceof MultiSelectSetting multiSelectSetting) {
            List<String> selected = multiSelectSetting.getSelected();
            String selectedAsString = String.join(",", selected);
            moduleObject.addProperty(setting.getName(), selectedAsString);
        }
        if (setting instanceof GroupSetting groupSetting) {
            JsonObject groupObject = new JsonObject();
            groupObject.addProperty("state", groupSetting.isValue());
            for (Setting subSetting : groupSetting.getSubSettings()) {
                addSettingToJsonObject(groupObject, subSetting);
            }
            moduleObject.add(setting.getName(), groupObject);
        }
    }

    private void writeJsonToFile(JsonObject functionObject, File file) throws FileSaveException {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(functionObject, writer);
        } catch (IOException e) {
            throw new FileSaveException("Failed to save module to file", e);
        }
    }

    private JsonObject readJsonFromFile(File file) throws FileLoadException {
        try (FileReader reader = new FileReader(file)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            throw new FileLoadException("Failed to load module from file", e);
        } catch (JsonSyntaxException | JsonIOException e) {
            throw new FileLoadException("Failed to parse JSON from file", e);
        }
    }

    private void updateModulesFromJsonObject(JsonObject functionObject) {
        for (Module module : moduleRepository.modules()) {
            JsonObject moduleObject = functionObject.getAsJsonObject(module.getName().toLowerCase());
            if (moduleObject == null)
                continue;

            if (moduleObject.has("bind") && moduleObject.has("state")) {
                module.setKey(moduleObject.get("bind").getAsInt());
                module.setState(moduleObject.get("state").getAsBoolean());
            }
            module.settings().forEach(setting -> updateSettingFromJsonObject(moduleObject, setting));
        }
    }

    private void updateSettingFromJsonObject(JsonObject moduleObject, Setting setting) {
        JsonElement settingElement = moduleObject.get(setting.getName());
        if (settingElement == null || settingElement.isJsonNull())
            return;

        if (setting instanceof BooleanSetting booleanSetting) {
            booleanSetting.setValue(settingElement.getAsBoolean());
        }
        if (setting instanceof ValueSetting valueSetting) {
            valueSetting.setValue(settingElement.getAsFloat());
        }
        if (setting instanceof ColorSetting colorSetting) {
            colorSetting.setColor(settingElement.getAsInt());
        }
        if (setting instanceof BindSetting bindSetting) {
            bindSetting.setKey(settingElement.getAsInt());
        }
        if (setting instanceof TextSetting textSetting) {
            textSetting.setText(settingElement.getAsString());
        }
        if (setting instanceof SelectSetting selectSetting) {
            selectSetting.setSelected(settingElement.getAsString());
        }
        if (setting instanceof MultiSelectSetting multiSelectSetting) {
            String asString = settingElement.getAsString();
            List<String> selectedList = new ArrayList<>(Arrays.asList(asString.split(",")));
            selectedList.removeIf(s -> !multiSelectSetting.getList().contains(s));
            multiSelectSetting.setSelected(selectedList);
        }
        if (setting instanceof GroupSetting groupSetting) {
            JsonObject groupObject = settingElement.getAsJsonObject();

            if (groupObject.has("state")) {
                groupSetting.setValue(groupObject.get("state").getAsBoolean());
            }
            for (Setting subSetting : groupSetting.getSubSettings()) {
                updateSettingFromJsonObject(groupObject, subSetting);
            }
        }
    }
}
