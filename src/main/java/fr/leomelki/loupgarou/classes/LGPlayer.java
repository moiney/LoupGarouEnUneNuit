package fr.leomelki.loupgarou.classes;

import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerChat;
import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerTitle;
import fr.leomelki.loupgarou.MainLg;
import fr.leomelki.loupgarou.classes.chat.LGChat;
import fr.leomelki.loupgarou.classes.chat.LGChat.LGChatCallback;
import fr.leomelki.loupgarou.classes.chat.LGNoChat;
import fr.leomelki.loupgarou.roles.Role;
import fr.leomelki.loupgarou.roles.RoleType;
import fr.leomelki.loupgarou.roles.RoleWinType;
import fr.leomelki.loupgarou.scoreboard.CustomScoreboard;
import fr.leomelki.loupgarou.utils.VariableCache;
import fr.leomelki.loupgarou.utils.VariousUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_15_R1.DimensionManager;
import net.minecraft.server.v1_15_R1.EnumGamemode;
import net.minecraft.server.v1_15_R1.PacketPlayOutRespawn;
import net.minecraft.server.v1_15_R1.WorldType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LGPlayer extends LGHasRole {
    private static HashMap<Player, LGPlayer> cachedPlayers = new HashMap<Player, LGPlayer>();
    public boolean canSelectDead;
    @Getter
    boolean muted;
    long lastChoose;
    @Getter
    @Setter
    private int place;
    @Getter
    private Player player;
    @Getter
    private Role startingRole;
    @Setter
    @Getter
    private Role currentRole;
    private LGChooseCallback chooseCallback;
    private List<LGPlayer> blacklistedChoice = new ArrayList<>(0);
    @Getter
    private VariableCache cache = new VariableCache();
    @Getter
    @Setter
    private LGGame game;
    @Getter
    @Setter
    private String latestObjective;
    @Getter
    private CustomScoreboard scoreboard;
    private String name;
    @Getter
    private LGChat chat;

    public LGPlayer(Player player) {
        this.player = player;
    }

    public LGPlayer(String name) {
        this.name = name;
    }

    public static LGPlayer thePlayer(Player player) {
        return cachedPlayers.computeIfAbsent(player, k -> new LGPlayer(player));
    }

    public static LGPlayer removePlayer(Player player) {
        return cachedPlayers.remove(player);//.remove();
    }

    public void setStartingRole(Role startingRole) {
        this.startingRole = startingRole;
        this.currentRole = startingRole;
    }

    public void setScoreboard(CustomScoreboard scoreboard) {
        if (player != null) {
            if (this.scoreboard != null)
                this.scoreboard.hide();

            this.scoreboard = scoreboard;

            if (scoreboard != null)
                scoreboard.show();
        }
    }

    public void sendActionBarMessage(String msg) {
        if (this.player != null) {
            WrapperPlayServerChat chat = new WrapperPlayServerChat();
            chat.setPosition((byte) 2);
            chat.setMessage(WrappedChatComponent.fromText(msg));
            chat.sendPacket(getPlayer());
        }
    }

    public void sendMessage(String msg) {
        if (this.player != null)
            getPlayer().sendMessage(MainLg.getPrefix() + msg);
    }

    public void sendTitle(String title, String subTitle, int stay) {
        if (this.player != null) {
            WrapperPlayServerTitle titlePacket = new WrapperPlayServerTitle();
            titlePacket.setAction(TitleAction.TIMES);
            titlePacket.setFadeIn(10);
            titlePacket.setStay(stay);
            titlePacket.setFadeOut(10);
            titlePacket.sendPacket(player);

            titlePacket = new WrapperPlayServerTitle();
            titlePacket.setAction(TitleAction.TITLE);
            titlePacket.setTitle(WrappedChatComponent.fromText(title));
            titlePacket.sendPacket(player);

            titlePacket = new WrapperPlayServerTitle();
            titlePacket.setAction(TitleAction.SUBTITLE);
            titlePacket.setTitle(WrappedChatComponent.fromText(subTitle));
            titlePacket.sendPacket(player);
        }
    }

    public void remove() {
        this.player = null;
    }

    public String getName() {
        return player != null ? getPlayer().getName() : name;
    }

    public boolean join(LGGame game) {
        if (getPlayer().getGameMode() == GameMode.SPECTATOR) {
            sendMessage("§cÉtant en mode spectateur, vous ne rejoignez pas la partie !");
            return false;
        }
        if (game.tryToJoin(this)) {
            //To update the skin
            updateOwnSkin();
            getPlayer().setWalkSpeed(0.2f);
            //		sendMessage("§2Vous venez de rejoindre une partie de Loup-Garou. §aBon jeu!");
            return true;
        }
        return false;
    }

    public void choose(LGChooseCallback callback, LGPlayer... blacklisted) {
        this.blacklistedChoice = blacklisted == null ? new ArrayList<LGPlayer>(0) : Arrays.asList(blacklisted);
        this.chooseCallback = callback;
        //sendMessage("§7§oTIP: Regardez un joueur et tapez le afin de le sélectionner.");
    }

    public void stopChoosing() {
        this.blacklistedChoice = null;
        this.chooseCallback = null;
    }

    @Override
    public Location getLocation() {
        return getPlayer().getLocation();
    }

    public void showView() {
        if (getGame() != null && player != null)
            for (LGPlayer lgp : getGame().getInGame())
                if (lgp != this && lgp.getPlayer() != null)
                    getPlayer().showPlayer(lgp.getPlayer());
                else {
                    WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
                    team.setMode(2);
                    team.setName(lgp.getName());
                    team.setPrefix(WrappedChatComponent.fromText(""));
                    team.setPlayers(Arrays.asList(lgp.getName()));
                    team.sendPacket(getPlayer());

                    WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
                    ArrayList<PlayerInfoData> infos = new ArrayList<PlayerInfoData>();
                    info.setAction(PlayerInfoAction.ADD_PLAYER);
                    infos.add(new PlayerInfoData(new WrappedGameProfile(getPlayer().getUniqueId(), getName()), 0, NativeGameMode.ADVENTURE, WrappedChatComponent.fromText(getName())));
                    info.setData(infos);
                    info.sendPacket(getPlayer());
                }


        getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 2, false, false));
    }

    //TODO Update prefix for only one guy
    public void updatePrefix() {
        if (getGame() != null && player != null) {
            List<String> meList = Arrays.asList(getName());
            for (LGPlayer lgp : getGame().getInGame()) {
                WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
                ArrayList<PlayerInfoData> infos = new ArrayList<PlayerInfoData>();
                info.setAction(PlayerInfoAction.ADD_PLAYER);
                infos.add(new PlayerInfoData(new WrappedGameProfile(getPlayer().getUniqueId(), getName()), 0, NativeGameMode.ADVENTURE, WrappedChatComponent.fromText(getName())));
                info.setData(infos);
                info.sendPacket(lgp.getPlayer());

                WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
                team.setMode(2);
                team.setName(getName());
                team.setPrefix(WrappedChatComponent.fromText(""));
                team.setPlayers(meList);
                team.sendPacket(lgp.getPlayer());
            }
        }
    }

    public void hideView() {
        if (getGame() != null && player != null) {
            WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
            ArrayList<PlayerInfoData> infos = new ArrayList<PlayerInfoData>();
            info.setAction(PlayerInfoAction.ADD_PLAYER);
            for (LGPlayer lgp : getGame().getInGame())
                if (lgp != this && lgp.getPlayer() != null) {
                    infos.add(new PlayerInfoData(new WrappedGameProfile(lgp.getPlayer().getUniqueId(), lgp.getName()), 0, NativeGameMode.ADVENTURE, WrappedChatComponent.fromText(lgp.getName())));
                    getPlayer().hidePlayer(lgp.getPlayer());
                }
            info.setData(infos);
            info.sendPacket(getPlayer());
        }

        getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999, 1, false, false));
    }

    public void updateSkin() {
        if (getGame() != null && player != null) {
            for (LGPlayer lgp : getGame().getInGame()) {
                if (lgp == this) {
                    WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
                    ArrayList<PlayerInfoData> infos = new ArrayList<PlayerInfoData>();
                    info.setAction(PlayerInfoAction.ADD_PLAYER);
                    infos.add(new PlayerInfoData(new WrappedGameProfile(getPlayer().getUniqueId(), getName()), 0, NativeGameMode.ADVENTURE, WrappedChatComponent.fromText(getName())));
                    info.setData(infos);
                    info.sendPacket(getPlayer());
                } else if (lgp.getPlayer() != null) {
                    lgp.getPlayer().hidePlayer(getPlayer());
                    lgp.getPlayer().showPlayer(getPlayer());
                }
            }
        }
    }

    public void updateOwnSkin() {
        if (player != null) {
            //On change son skin avec un packet de PlayerInfo (dans le tab)
            WrapperPlayServerPlayerInfo infos = new WrapperPlayServerPlayerInfo();
            infos.setAction(PlayerInfoAction.ADD_PLAYER);
            WrappedGameProfile gameProfile = new WrappedGameProfile(getPlayer().getUniqueId(), getPlayer().getName());
            infos.setData(Arrays.asList(new PlayerInfoData(gameProfile, 10, NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(getPlayer().getName()))));
            infos.sendPacket(getPlayer());
            //Pour qu'il voit son skin changer (sa main et en f5), on lui dit qu'il respawn (alors qu'il n'est pas mort mais ça marche quand même mdr)
            PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(DimensionManager.OVERWORLD, 0, WorldType.NORMAL, EnumGamemode.ADVENTURE);
            ((CraftPlayer) getPlayer()).getHandle().playerConnection.sendPacket(respawn);
            //Enfin, on le téléporte à sa potion actuelle car sinon il se verra dans le vide
            getPlayer().teleport(getPlayer().getLocation());
            float speed = getPlayer().getWalkSpeed();
            getPlayer().setWalkSpeed(0.2f);
            new BukkitRunnable() {

                @Override
                public void run() {
                    getPlayer().updateInventory();
                    getPlayer().setWalkSpeed(speed);
                }
            }.runTaskLater(MainLg.getInstance(), 5);
            //Et c'est bon, le joueur se voit avec un nouveau skin avec quasiment aucun problème visible à l'écran :D
        }
    }

    public <T extends LGHasRole> T getTargetOnCursor(List<T> targets) {
        Location loc = getPlayer().getLocation();
        for (int i = 0; i < 50; i++) {
            loc.add(loc.getDirection());
            for (T target : targets) {
                if (target != this && isCursorOnLocation(loc, target.getLocation())) {
                    return target;
                }
            }
        }
        return null;
    }

    private boolean isCursorOnLocation(Location cursorLoc, Location targetLocation) {
        return VariousUtils.distanceSquaredXZ(cursorLoc, targetLocation) < 0.35 && Math.abs(cursorLoc.getY() - targetLocation.getY()) < 2;
    }

    public RoleType getRoleType() {
        return getCurrentRole().getType(this);
    }

    public RoleWinType getRoleWinType() {
        return getCurrentRole().getWinType(this);
    }

    public RoleWinType getRoleWinOnDeath() {
        return this.getCurrentRole().getRoleWinOnDeath();
    }

    public void die() {
        setMuted();
    }

    private void setMuted() {
        if (player != null)
            for (LGPlayer lgp : getGame().getInGame())
                if (lgp != this && lgp.getPlayer() != null)
                    lgp.getPlayer().hidePlayer(getPlayer());
        muted = true;
    }

    public void resetMuted() {
        muted = false;
    }

    public void joinChat(LGChat chat, LGChatCallback callback) {
        joinChat(chat, callback, false);
    }

    public void joinChat(LGChat chat) {
        joinChat(chat, null, false);
    }

    public void joinChat(LGChat chat, boolean muted) {
        joinChat(chat, null, muted);
    }

    public void joinChat(LGChat chat, LGChatCallback callback, boolean muted) {
        if (this.chat != null && !muted)
            this.chat.leave(this);

        if (!muted)
            this.chat = chat;

        if (chat != null && player != null)
            chat.join(this, callback == null ? chat.getDefaultCallback() : callback);
    }


    public void leaveChat() {
        joinChat(new LGNoChat(), null);
    }

    public void onChat(String message) {
        if (chat != null) {
            chat.sendMessage(this, message);
        }
    }


    public void playAudio(LGSound sound, double volume) {
        if (player != null)
            getPlayer().playSound(getPlayer().getLocation(), sound.getSound(), (float) volume, 1);
    }

    public void stopAudio(LGSound sound) {
        if (player != null)
            getPlayer().stopSound(sound.getSound());
    }

    public void chooseAction() {
        long now = System.currentTimeMillis();
        if (lastChoose + 200 < now) {
            if (chooseCallback != null)
                chooseCallback.callback(getTargetOnCursor(getGame().getInGame()), getTargetOnCursor(getGame().getCenterCards()));
            lastChoose = now;
        }
    }

    @Override
    public String toString() {
        return super.toString() + " (" + getName() + ")";
    }

    public interface LGChooseCallback {
        void callback(LGPlayer choosenPlayer, LGCenterCard choosenCenterCard);
    }
}
