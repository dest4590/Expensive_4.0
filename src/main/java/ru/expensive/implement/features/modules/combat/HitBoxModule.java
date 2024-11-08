package ru.expensive.implement.features.modules.combat;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.BooleanSetting;
import ru.expensive.api.feature.module.setting.implement.ValueSetting;
import ru.expensive.api.repository.friend.FriendRepository;
import ru.expensive.implement.events.player.BoundingBoxControlEvent;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HitBoxModule extends Module {

    ValueSetting xzExpandSetting = new ValueSetting("XZ Expand", "Allows the box to be extended in the XZ axis")
            .setValue(0.2F)
            .range(0.0F, 3.0F);

    ValueSetting yExpandSetting = new ValueSetting("Y Expand", "Allows the box to be extended in the Y axis")
            .setValue(0.0F)
            .range(0.0F, 3.0F);

    BooleanSetting ignoreFriendsSetting = new BooleanSetting("Ignore Friends", "Doesn't expand the box at friends")
            .setValue(true);

    public HitBoxModule() {
        super("HitBox", ModuleCategory.COMBAT);
        setup(xzExpandSetting, yExpandSetting, ignoreFriendsSetting);
    }

    @EventHandler
    public void onBoundingBoxControl(BoundingBoxControlEvent event) {
        Box box = event.getBox();
        Entity entity = event.getEntity();

        float xzExpand = xzExpandSetting.getValue();
        float yExpand = yExpandSetting.getValue();

        Box changedBox = new Box(
                box.minX - xzExpand / 2.0f,
                box.minY - yExpand / 2.0f,
                box.minZ - xzExpand / 2.0f,
                box.maxX + xzExpand / 2.0f,
                box.maxY + yExpand / 2.0f,
                box.maxZ + xzExpand / 2.0f);

        if (isValid(entity)) {
            event.setChangedBox(changedBox);
            event.cancel();
        }
    }

    private boolean isValid(Entity entity) {
        if (mc.player != null && mc.player.getId() == entity.getId()) {
            return false;
        }
        if (FriendRepository.isFriend(entity.getEntityName()) && ignoreFriendsSetting.isValue()) {
            return false;
        }

        return entity instanceof PlayerEntity;
    }
}
