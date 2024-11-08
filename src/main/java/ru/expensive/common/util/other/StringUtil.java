package ru.expensive.common.util.other;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.UserApiService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.report.ReporterEnvironment;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.client.util.Session;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.asm.mixins.accessors.MinecraftClientAccessor;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.minecraft.client.util.InputUtil.Type.*;

public class StringUtil {
    public static String randomString(int length) {
        return IntStream.range(0, length)
                .mapToObj(operand -> String.valueOf((char) new Random().nextInt('a', 'z' + 1)))
                .collect(Collectors.joining());
    }

    public static void setSession(Session session) throws AuthenticationException {
        MinecraftClient mc = MinecraftClient.getInstance();
        MinecraftClientAccessor mca = (MinecraftClientAccessor) mc;
        mca.setSession(session);
        UserApiService apiService;
        apiService = mca.getAuthenticationService().createUserApiService(session.getAccessToken());
        mca.setUserApiService(apiService);
        mca.setSocialInteractionsManager(new SocialInteractionsManager(mc, apiService));
        mca.setProfileKeys(ProfileKeys.create(apiService, session, mc.runDirectory.toPath()));
        mca.setAbuseReportContext(AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), apiService));
    }

    public static String getBindName(int key) {
        InputUtil.Key isMouse = key < 8 ? MOUSE.createFromCode(key) : KEYSYM.createFromCode(key);

        InputUtil.Key code = key == -1
                ? SCANCODE.createFromCode(key)
                : isMouse;

        return key == -1 ? "N/A"
                : code
                .getTranslationKey()
                .replace("key.keyboard.", "")
                .replace("key.mouse.", "mouse ")
                .replace(".", " ")
                .toUpperCase();
    }

    public static String wrap(String input, int width, int size) {
        String[] words = input.split(" ");
        StringBuilder output = new StringBuilder();
        float lineWidth = 0;
        for (String word : words) {
            float wordWidth = Fonts.getSize(size).getStringWidth(word);
            if (lineWidth + wordWidth > width) {
                output.append("\n");
                lineWidth = 0;
            } else if (lineWidth > 0) {
                output.append(" ");
                lineWidth += Fonts.getSize(size).getStringWidth(" ");
            }
            output.append(word);
            lineWidth += wordWidth;
        }
        return output.toString();
    }
}
