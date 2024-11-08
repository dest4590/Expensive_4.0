package ru.expensive.implement.features.modules.combat.killaura.attack;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.expensive.common.QuickImports;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClickScheduler implements QuickImports {
    final int[] clickIntervals = {12, 13, 11, 12, 12, 12, 12, 12, 12, 12, 12, 13, 12, 12, 12, 12, 12, 10,
            12, 13, 13, 12, 12, 11, 13, 11, 12, 12, 12, 11, 12, 11, 13, 13, 13, 11, 12, 11, 12, 12, 11, 12, 14, 13,
            12, 12, 11, 12, 11, 12, 13, 12, 12, 11, 13, 12, 12, 11, 13, 12, 12, 13, 12, 12, 12, 12, 11, 13, 11, 11,
            13, 13, 12, 11, 13, 11, 12, 11, 13};
    int ticks = 0;
    long delay = 0;
    long lastClickTime = System.currentTimeMillis();

    public boolean isCooldownComplete(boolean dynamicCooldown) {
        long delay = dynamicCooldown ? calculateAverageCooldown() * 45L : this.delay;
        float cooldownProgress = mc.player.getAttackCooldownProgress(0.5F);
        long currentTime = System.currentTimeMillis();
        long time = currentTime - lastClickTime;

        return (time >= delay) && cooldownProgress > 0.9F;
    }

    public boolean hasTicksElapsedSinceLastClick(int ticks) {
        return lastClickPassed() >= (ticks * 50L);
    }

    public long lastClickPassed() {
        return System.currentTimeMillis() - lastClickTime;
    }

    public void recalculate(long delay) {
        lastClickTime = System.currentTimeMillis();
        this.delay = delay;
    }

    int calculateAverageCooldown() {
        if (ticks >= clickIntervals.length) {
            ticks = 0;
        }
        return clickIntervals[ticks++];
    }
}