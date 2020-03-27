package fr.leomelki.loupgarou.classes;

import fr.leomelki.loupgarou.events.LGCustomItemChangeEvent;
import fr.leomelki.loupgarou.roles.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringJoiner;

public class LGCustomItems {
	static HashMap<Class<? extends Role>, HashMap<String, Material>> mappings = new HashMap<Class<? extends Role>, HashMap<String,Material>>();
	static {
		JSONParser parser = new JSONParser();
		try {
			JSONObject mappings = (JSONObject) parser.parse("{\"LoupGarou\":{\"\":\"OAK_PLANKS\",\"infecte\":\"DRAGON_BREATH\",\"infecte_mort\":\"GOLDEN_PICKAXE\",\"infecte_maire\":\"WITHER_SKELETON_SKULL\",\"maire\":\"TRAPPED_CHEST\",\"maire_mort\":\"BRICK_WALL\",\"mort\":\"MELON\",\"infecte_maire_mort\":\"DARK_OAK_LEAVES\"}, \"Villageois\":{\"\":\"DEAD_BUSH\",\"infecte_mort\":\"DIAMOND_AXE\",\"infecte\":\"CYAN_BED\",\"infecte_maire\":\"PINK_CONCRETE\",\"maire\":\"LEAD\",\"maire_mort\":\"LIME_CONCRETE\",\"mort\":\"DEAD_BRAIN_CORAL\",\"infecte_maire_mort\":\"SUNFLOWER\"},\"Voleur\":{\"infecte\":\"BLUE_STAINED_GLASS\",\"\":\"RED_MUSHROOM_BLOCK\",\"infecte_mort\":\"SPAWNER\",\"infecte_maire\":\"STRIPPED_DARK_OAK_LOG\",\"maire\":\"DROPPER\",\"maire_mort\":\"COBBLESTONE_SLAB\",\"mort\":\"ENDER_CHEST\",\"infecte_maire_mort\":\"IRON_INGOT\"}, \"Tanneur\":{\"\":\"POLISHED_GRANITE_SLAB\",\"infecte\":\"BROWN_BED\",\"infecte_mort\":\"MOSSY_STONE_BRICK_WALL\",\"infecte_maire\":\"DEAD_FIRE_CORAL_WALL_FAN\",\"maire\":\"SANDSTONE\",\"maire_mort\":\"POLISHED_DIORITE\",\"mort\":\"NETHER_BRICK_SLAB\",\"infecte_maire_mort\":\"BONE_MEAL\"}, \"Chasseur\":{\"infecte_mort\":\"COAL\",\"infecte\":\"PRISMARINE_SLAB\",\"\":\"DIORITE\",\"infecte_maire\":\"BLUE_CONCRETE\",\"maire\":\"DEAD_TUBE_CORAL_BLOCK\",\"maire_mort\":\"MAGENTA_TERRACOTTA\",\"mort\":\"CAKE\",\"infecte_maire_mort\":\"YELLOW_WOOL\"}, \"Voyante\":{\"infecte_mort\":\"OAK_DOOR\",\"\":\"RED_CONCRETE_POWDER\",\"infecte\":\"END_STONE_BRICKS\",\"infecte_maire\":\"FARMLAND\",\"maire\":\"POPPY\",\"maire_mort\":\"PINK_CONCRETE_POWDER\",\"mort\":\"MUSHROOM_STEM\",\"infecte_maire_mort\":\"WHITE_BANNER\"}, \"Sbire\":{\"infecte\":\"WITHER_ROSE\",\"\":\"GRAY_CONCRETE_POWDER\",\"infecte_mort\":\"ACACIA_DOOR\",\"infecte_maire\":\"ENCHANTING_TABLE\",\"maire\":\"DEAD_HORN_CORAL_FAN\",\"maire_mort\":\"WOODEN_AXE\",\"mort\":\"HOPPER\",\"infecte_maire_mort\":\"RED_STAINED_GLASS\"}, \"Insomniaque\":{\"\":\"SCUTE\",\"infecte\":\"ORANGE_CONCRETE\",\"infecte_mort\":\"WOODEN_SHOVEL\",\"infecte_maire\":\"SOUL_SAND\",\"maire\":\"EXPERIENCE_BOTTLE\",\"mort\":\"STRING\",\"maire_mort\":\"DARK_OAK_SIGN\",\"infecte_maire_mort\":\"PURPLE_SHULKER_BOX\"}, \"FrancMacon\":{\"\":\"GLASS_PANE\",\"infecte_mort\":\"RED_BED\",\"infecte\":\"MAGENTA_WOOL\",\"infecte_maire\":\"CHEST\",\"maire\":\"FEATHER\",\"mort\":\"REDSTONE_TORCH\",\"maire_mort\":\"DARK_OAK_BUTTON\",\"infecte_maire_mort\":\"GREEN_WOOL\"}, \"Noiseuse\":{\"\":\"NETHERRACK\",\"infecte_mort\":\"OAK_SIGN\",\"infecte\":\"CONDUIT\",\"infecte_maire\":\"IRON_HORSE_ARMOR\",\"maire\":\"STRIPPED_OAK_LOG\",\"mort\":\"HORN_CORAL_BLOCK\",\"maire_mort\":\"COMPOSTER\",\"infecte_maire_mort\":\"BIRCH_FENCE_GATE\"}, \"Soulard\":{\"infecte\":\"BIRCH_PLANKS\",\"\":\"LECTERN\",\"infecte_mort\":\"GREEN_CONCRETE_POWDER\",\"infecte_maire\":\"RABBIT_FOOT\",\"maire\":\"FIRE_CORAL_BLOCK\",\"maire_mort\":\"STRIPPED_OAK_WOOD\",\"mort\":\"DEAD_HORN_CORAL_WALL_FAN\",\"infecte_maire_mort\":\"LAPIS_BLOCK\"}}");
			for (Object key : mappings.keySet()) {
				HashMap<String, Material> map = new HashMap<String, Material>();
				JSONObject array = (JSONObject) mappings.get(key);
				for (Object key2 : array.keySet())
					map.put((String) key2, Material.valueOf((String) array.get(key2)));
				try {
					LGCustomItems.mappings.put((Class<? extends Role>) Class.forName("fr.leomelki.loupgarou.roles.R" + key), map);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Material getItem(Role role) {
		return mappings.get(role.getClass()).get("");
	}

	public static Material getItem(LGPlayer player, ArrayList<String> constraints) {
		Bukkit.getPluginManager().callEvent(new LGCustomItemChangeEvent(player.getGame(), player, constraints));

		Collections.sort(constraints);
		HashMap<String, Material> mapps = mappings.get(player.getStartingRole().getClass());
		if (mapps == null)
			return Material.AIR;//Lors du développement de rôles.
		StringJoiner sj = new StringJoiner("_");
		for (String s : constraints)
			sj.add(s);
		return mapps.get(sj.toString());
	}
	public static Material getItem(LGPlayer player) {
		return getItem(player, new ArrayList<String>());
	}
	
	public static void updateItem(LGPlayer lgp) {
		lgp.getPlayer().getInventory().setItemInOffHand(new ItemStack(getItem(lgp)));
		lgp.getPlayer().updateInventory();
	}

	public static void updateItem(LGPlayer lgp, ArrayList<String> constraints) {
		lgp.getPlayer().getInventory().setItemInOffHand(new ItemStack(getItem(lgp, constraints)));
		lgp.getPlayer().updateInventory();
	}
	
	@RequiredArgsConstructor
	public static enum LGCustomItemsConstraints{
		INFECTED("infecte"),
		MAYOR("maire"),
		DEAD("mort");
		@Getter private final String name;
	}
	
}
