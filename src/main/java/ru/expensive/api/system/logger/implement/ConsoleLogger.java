package ru.expensive.api.system.logger.implement;

import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import ru.expensive.api.system.logger.Logger;

public class ConsoleLogger implements Logger {
    private final org.apache.logging.log4j.Logger logger = LogManager.getLogger("Expensive");

    @Override
    public void log(Object message) {
        logger.info("[" + "Expensive" + "] " + message);
    }

    @Override
    public void minecraftLog(Text... components) {

    }
}
