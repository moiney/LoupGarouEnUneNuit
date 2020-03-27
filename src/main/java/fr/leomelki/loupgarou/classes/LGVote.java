package fr.leomelki.loupgarou.classes;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import fr.leomelki.com.comphenix.packetwrapper.*;
import fr.leomelki.loupgarou.MainLg;
import fr.leomelki.loupgarou.classes.LGGame.TextGenerator;
import fr.leomelki.loupgarou.classes.LGPlayer.LGChooseCallback;
import fr.leomelki.loupgarou.events.LGVoteLeaderChange;
import lombok.Getter;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

public class LGVote {
    static {
        try {
            Field f = Entity.class.getDeclaredField("az");
            f.setAccessible(true);
            az = (DataWatcherObject<Optional<IChatBaseComponent>>) f.get(null);
            f = Entity.class.getDeclaredField("aA");
            f.setAccessible(true);
            aA = (DataWatcherObject<Boolean>) f.get(null);
            f = Entity.class.getDeclaredField("T");
            f.setAccessible(true);
            T = (DataWatcherObject<Byte>) f.get(null);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private final LGGame game;
    private final TextGenerator generator;
    @Getter
    private final HashMap<LGPlayer, List<LGPlayer>> votes = new HashMap<LGPlayer, List<LGPlayer>>();
    @Getter
    List<LGPlayer> choosen;
    WrappedDataWatcherObject invisible = new WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)),
            noGravity = new WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)),
            customNameVisible = new WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)),
            customName = new WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.get(IChatBaseComponent.class)),
            item = new WrappedDataWatcherObject(7, WrappedDataWatcher.Registry.get(net.minecraft.server.v1_15_R1.ItemStack.class));
    private int timeout, initialTimeout, littleTimeout;
    private Runnable callback;
    @Getter
    private List<LGPlayer> participants;
    private boolean ended;
    private int votesSize = 0;
    private ArrayList<LGPlayer> latestTop = new ArrayList<LGPlayer>();

    private static DataWatcherObject<Optional<IChatBaseComponent>> az;
    private static DataWatcherObject<Boolean> aA;
    private static DataWatcherObject<Byte> T;
    private static final EntityArmorStand eas = new EntityArmorStand(null, 0, 0, 0);

    public LGVote(int timeout, int littleTimeout, LGGame game, TextGenerator generator) {
        this.littleTimeout = littleTimeout;
        this.initialTimeout = timeout;
        this.timeout = timeout;
        this.game = game;
        this.generator = generator;
    }

    public void start(List<LGPlayer> participants, Runnable callback) {
        this.callback = callback;
        this.participants = participants;
        game.wait(timeout, this::end, generator);
        for (LGPlayer player : participants)
            player.choose(getChooseCallback(player));
    }

    private void end() {
        ended = true;
        for (LGPlayer lgp : participants)
            showVoting(lgp, null);
        for (LGPlayer lgp : votes.keySet())
            updateVotes(lgp, true);
        int max = 0;
        for (Entry<LGPlayer, List<LGPlayer>> entry : votes.entrySet()) {
            if (entry.getValue().size() > max) {
                max = entry.getValue().size();
            }
        }
        for (LGPlayer player : participants) {
            player.getCache().remove("vote");
            player.stopChoosing();
        }
        ArrayList<LGPlayer> choosable = new ArrayList<LGPlayer>();
        for (Entry<LGPlayer, List<LGPlayer>> entry : votes.entrySet())
            if (entry.getValue().size() == max)
                choosable.add(entry.getKey());
        choosen = choosable;
        game.cancelWait();
        callback.run();
    }

    public LGChooseCallback getChooseCallback(LGPlayer who) {
        return new LGChooseCallback() {

            @Override
            public void callback(LGPlayer choosen, LGCenterCard choosenCenterCard) {
                if (choosen != null)
                    vote(who, choosen);
            }
        };
    }

    public void vote(LGPlayer voter, LGPlayer voted) {
        if (voted == voter.getCache().get("vote"))
            voted = null;

        if (voted != null && voter.getPlayer() != null)
            votesSize++;
        if (voter.getCache().has("vote"))
            votesSize--;

        if (votesSize == participants.size() && timeout > littleTimeout) {
            votesSize = 999;
            game.wait(littleTimeout, initialTimeout, this::end, generator);
        }
        String italic = game.isDay() ? "" : "§o";
        boolean changeVote = false;
        if (voter.getCache().has("vote")) {//On enlève l'ancien vote
            LGPlayer devoted = voter.getCache().get("vote");
            if (votes.containsKey(devoted)) {
                List<LGPlayer> voters = votes.get(devoted);
                if (voters != null) {
                    voters.remove(voter);
                    if (voters.size() == 0)
                        votes.remove(devoted);
                }
            }
            voter.getCache().remove("vote");
            updateVotes(devoted);
            changeVote = true;
        }

        if (voted != null) {//Si il vient de voter, on ajoute le nouveau vote
            //voter.sendTitle("", "§7Tu as voté pour §7§l"+voted.getName(), 40);
            if (votes.containsKey(voted))
                votes.get(voted).add(voter);
            else
                votes.put(voted, new ArrayList<LGPlayer>(Arrays.asList(voter)));
            voter.getCache().set("vote", voted);
            updateVotes(voted);
        }

        if (voter.getPlayer() != null) {
            showVoting(voter, voted);
            String message;
            if (voted != null) {
                if (changeVote) {
                    message = "§7§l" + voter.getName() + "§6 a changé son vote pour §7§l" + voted.getName() + "§6.";
                    voter.sendMessage("§6Tu as changé de vote pour §7§l" + voted.getName() + "§6.");
                } else {
                    message = "§7§l" + voter.getName() + "§6 a voté pour §7§l" + voted.getName() + "§6.";
                    voter.sendMessage("§6Tu as voté pour §7§l" + voted.getName() + "§6.");
                }
            } else {
                message = "§7§l" + voter.getName() + "§6 a annulé son vote.";
                voter.sendMessage("§6Tu as annulé ton vote.");
            }

            for (LGPlayer player : participants)
                if (player != voter)
                    player.sendMessage(message);
        }
    }

    public List<LGPlayer> getVotes(LGPlayer voted) {
        return votes.containsKey(voted) ? votes.get(voted) : new ArrayList<LGPlayer>(0);
    }

    private void updateVotes(LGPlayer voted) {
        updateVotes(voted, false);
    }

    private void updateVotes(LGPlayer voted, boolean kill) {
        int entityId = Integer.MIN_VALUE + voted.getPlayer().getEntityId();
        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(new int[]{entityId});
        for (LGPlayer lgp : participants)
            destroy.sendPacket(lgp.getPlayer());

        if (!kill) {
            int max = 0;
            for (Entry<LGPlayer, List<LGPlayer>> entry : votes.entrySet())
                if (entry.getValue().size() > max)
                    max = entry.getValue().size();
            ArrayList<LGPlayer> last = latestTop;
            latestTop = new ArrayList<LGPlayer>();
            for (Entry<LGPlayer, List<LGPlayer>> entry : votes.entrySet())
                if (entry.getValue().size() == max)
                    latestTop.add(entry.getKey());
            Bukkit.getPluginManager().callEvent(new LGVoteLeaderChange(game, this, last, latestTop));
        }

        if (votes.containsKey(voted) && !kill) {
            Location loc = voted.getPlayer().getLocation();

            WrapperPlayServerSpawnEntityLiving spawn = new WrapperPlayServerSpawnEntityLiving();
            spawn.setEntityID(entityId);
            spawn.setType(EntityType.DROPPED_ITEM);
            //spawn.setMetadata(new WrappedDataWatcher(Arrays.asList(new WrappedWatchableObject(0, (byte)0x20), new WrappedWatchableObject(5, true))));
            spawn.setX(loc.getX());
            spawn.setY(loc.getY() + 0.3);
            spawn.setZ(loc.getZ());


            int votesNbr = votes.get(voted).size();
			/*WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
			meta.setEntityID(entityId);
			meta.setMetadata(Arrays.asList(new WrappedWatchableObject(invisible, (byte)0x20), new WrappedWatchableObject(noGravity, true), new WrappedWatchableObject(customNameVisible, true), new WrappedWatchableObject(customName, IChatBaseComponent.ChatSerializer.b("§6§l"+votesNbr+"§e vote"+(votesNbr > 1 ? "s" : "")))));
			*/
            DataWatcher datawatcher = new DataWatcher(eas);
            datawatcher.register(T, (byte) 0x20);
            datawatcher.register(az, Optional.ofNullable(IChatBaseComponent.ChatSerializer.a("{\"text\":\"§6§l" + votesNbr + "§e vote" + (votesNbr > 1 ? "s" : "") + "\"}")));
            datawatcher.register(aA, true);
            PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(entityId, datawatcher, true);

            for (LGPlayer lgp : participants) {
                spawn.sendPacket(lgp.getPlayer());
                ((CraftPlayer) lgp.getPlayer()).getHandle().playerConnection.sendPacket(meta);
            }
        }
    }

    private void showVoting(LGPlayer to, LGPlayer ofWho) {
        int entityId = -to.getPlayer().getEntityId();
        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(new int[]{entityId});
        destroy.sendPacket(to.getPlayer());
        if (ofWho != null) {
            WrapperPlayServerSpawnEntityLiving spawn = new WrapperPlayServerSpawnEntityLiving();
            spawn.setEntityID(entityId);
            spawn.setType(EntityType.DROPPED_ITEM);
            //spawn.setMetadata(new WrappedDataWatcher(Arrays.asList(new WrappedWatchableObject(0, (byte)0x20), new WrappedWatchableObject(5, true))));
            Location loc = ofWho.getPlayer().getLocation();
            spawn.setX(loc.getX());
            spawn.setY(loc.getY() + 1.3);
            spawn.setZ(loc.getZ());
            spawn.setHeadPitch(0);
            Location toLoc = to.getPlayer().getLocation();
            double diffX = loc.getX() - toLoc.getX(),
                    diffZ = loc.getZ() - toLoc.getZ();
            float yaw = 180 - ((float) Math.toDegrees(Math.atan2(diffX, diffZ)));

            spawn.setYaw(yaw);
            spawn.sendPacket(to.getPlayer());

            WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
            meta.setEntityID(entityId);
            meta.setMetadata(Arrays.asList(new WrappedWatchableObject(invisible, (byte) 0x20), new WrappedWatchableObject(noGravity, true)));
            meta.sendPacket(to.getPlayer());

            WrapperPlayServerEntityLook look = new WrapperPlayServerEntityLook();
            look.setEntityID(entityId);
            look.setPitch(0);
            look.setYaw(yaw);
            look.sendPacket(to.getPlayer());

            new BukkitRunnable() {

                @Override
                public void run() {
                    WrapperPlayServerEntityEquipment equip = new WrapperPlayServerEntityEquipment();
                    equip.setEntityID(entityId);
                    equip.setSlot(ItemSlot.HEAD);
                    ItemStack skull = new ItemStack(Material.EMERALD);
                    equip.setItem(skull);
                    equip.sendPacket(to.getPlayer());
                }
            }.runTaskLater(MainLg.getInstance(), 2);
        }
    }

    private void showArrow(LGPlayer to, LGPlayer ofWho, int entityId) {
        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(new int[]{entityId});
        destroy.sendPacket(to.getPlayer());
        if (ofWho != null) {
            WrapperPlayServerSpawnEntityLiving spawn = new WrapperPlayServerSpawnEntityLiving();
            spawn.setEntityID(entityId);
            spawn.setType(EntityType.DROPPED_ITEM);
            //spawn.setMetadata(new WrappedDataWatcher());
            Location loc = ofWho.getPlayer().getLocation();
            spawn.setX(loc.getX());
            spawn.setY(loc.getY() + 1.3);
            spawn.setZ(loc.getZ());
            spawn.setHeadPitch(0);
            Location toLoc = to.getPlayer().getLocation();
            double diffX = loc.getX() - toLoc.getX(),
                    diffZ = loc.getZ() - toLoc.getZ();
            float yaw = 180 - ((float) Math.toDegrees(Math.atan2(diffX, diffZ)));

            spawn.setYaw(yaw);
            spawn.sendPacket(to.getPlayer());

            WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
            meta.setEntityID(entityId);
            meta.setMetadata(Arrays.asList(new WrappedWatchableObject(invisible, (byte) 0x20), new WrappedWatchableObject(noGravity, true)));
            meta.sendPacket(to.getPlayer());

            WrapperPlayServerEntityLook look = new WrapperPlayServerEntityLook();
            look.setEntityID(entityId);
            look.setPitch(0);
            look.setYaw(yaw);
            look.sendPacket(to.getPlayer());

            new BukkitRunnable() {

                @Override
                public void run() {
                    WrapperPlayServerEntityEquipment equip = new WrapperPlayServerEntityEquipment();
                    equip.setEntityID(entityId);
                    equip.setSlot(ItemSlot.HEAD);
                    ItemStack skull = new ItemStack(Material.EMERALD);
                    equip.setItem(skull);
                    equip.sendPacket(to.getPlayer());
                }
            }.runTaskLater(MainLg.getInstance(), 2);
        }
    }

    public void remove(LGPlayer killed) {
        participants.remove(killed);
        if (!ended) {
            votes.remove(killed);
            latestTop.remove(killed);
        }
    }
}
