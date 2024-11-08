package ru.expensive.common.util.player;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class NicknameGenerator {
    private final String[] PREFIXES = {
            "Dark", "Mystic", "Shadow", "Epic", "Legend", "Iron", "Fire", "Frost", "Silent", "Thunder",
            "Crystal", "Golden", "Silver", "Storm", "Night", "Blaze", "Phantom", "Inferno", "Venom", "Solar",
            "Arcane", "Celestial", "Demonic", "Eternal", "Furious", "Galactic", "Heroic", "Infernal", "Jade", "Lunar",
            "Majestic", "Nebula", "Omega", "Psycho", "Quantum", "Radiant", "Sapphire", "Titan", "Ultraviolet", "Vortex",
            "Electric", "Starlit", "Shadowy", "Vicious", "Frozen", "Furious", "Terrifying", "Ethereal", "Seraphic",
            "Enchanted",
            "Ghostly", "Fierce", "Glowing", "Mysterious", "Savage", "Wild", "Blessed", "Burning", "Divine", "Forsaken",
            "Grim", "Hallowed", "Vengeful", "Whispering", "Zealous", "Vasiliy"
    };

    private final String[] BASES = {
            "Warrior", "Mage", "Knight", "Hunter", "Ranger", "Ninja", "Samurai", "Druid", "Paladin", "Assassin",
            "Berserker", "Valkyrie", "Sorcerer", "Gladiator", "Monk", "Pirate", "Rogue", "Cleric", "Bard", "Shaman",
            "Templar", "Necromancer", "Witch", "Sentinel", "Champion", "Crusader", "Mercenary", "Hunter", "Scout",
            "Elementalist",
            "Illusionist", "Summoner", "Mystic", "Swordsman", "Alchemist", "Beastmaster", "Warlord", "Ravager",
            "Seeker", "Outlaw",
            "Warlock", "Guardian", "Protector", "Hunter", "Invoker", "Reaper", "Marauder", "Exorcist", "Vigilante",
            "Blademaster",
            "Bloodrider", "Draconian", "Frostweaver", "Geomancer", "Hellbringer", "Luminary", "Manhunter",
            "Nightstalker", "Oracle", "Prowler",
            "Runemaster", "Soulbinder", "Stormcaller", "Thane", "Voidwalker", "Windspeaker", "Zephyr", "Zealot",
            "Pupkin"
    };

    public String generateGameNickname() {
        Random random = new Random();
        String prefix = PREFIXES[random.nextInt(PREFIXES.length)];
        String base = BASES[random.nextInt(BASES.length)];

        int number = random.nextInt(30);

        return String.format("%s%s%d", prefix, base, number).substring(0,
                Math.min(16, String.format("%s%s%d", prefix, base, number).length()));
    }
}
