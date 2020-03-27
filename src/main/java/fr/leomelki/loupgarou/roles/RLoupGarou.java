package fr.leomelki.loupgarou.roles;

import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import fr.leomelki.loupgarou.classes.LGCustomSkin;
import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.classes.LGWinType;
import fr.leomelki.loupgarou.classes.chat.LGChat;
import fr.leomelki.loupgarou.events.*;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class RLoupGarou extends Role {

    boolean showSkins = false;
    @Getter
    private LGChat chat = new LGChat((sender, message) -> {
        return "§c" + sender.getName() + " §6» §f" + message;
    });

    public RLoupGarou(LGGame game) {
        super(game);
    }

    @Override
    public String getName() {
        return "§c§lLoup-Garou";
    }

    @Override
    public String getFriendlyName() {
        return "des §c§lLoups-Garous";
    }

    @Override
    public String getShortDescription() {
        return "Tu gagnes avec les §c§lLoups-Garous";
    }

    @Override
    public String getDescription() {
        return "Tu gagnes avec les §c§lLoups-Garous§f. Tu dois éliminer un villageois lors du vote.";
    }

    @Override
    public String getTask() {
        return "Reconnaissez-vous !";
    }

    @Override
    public String getBroadcastedTask() {
        return "Les §c§lLoups-Garous§9 se reconnaissent.";
    }

    @Override
    public RoleType getType() {
        return RoleType.LOUP_GAROU;
    }

    @Override
    public RoleWinType getWinType() {
        return RoleWinType.LOUP_GAROU;
    }

    @Override
    public int getTimeout() {
        return 8;
    }

    @Override
    public void join(LGPlayer player, boolean sendMessage) {
        super.join(player, sendMessage);
        //On peut créer des cheats grâce à ça (qui permettent de savoir qui est lg/inf)
        for (LGPlayer p : getPlayers())
            p.updatePrefix();
    }

    @Override
    public RoleWinType getRoleWinOnDeath() {
        return RoleWinType.LOUP_GAROU;
    }

    @Override
    public void onNightTurn(Runnable callback) {
        for (LGPlayer lgp : getGame().getInGame())
            if (lgp.getRoleType() == RoleType.LOUP_GAROU)
                lgp.showView();
        for (LGPlayer player : getPlayers()) {
            player.sendMessage("§6" + getTask());
            player.joinChat(chat);
        }
        if (getPlayers().size() == 1) {

            LGPlayer soloLg = getPlayers().get(0);
            soloLg.sendMessage("Tu es seul, tu peux donc regarder le rôle d'une des cartes du centre.");
            soloLg.choose((uselessPlayer, centerCard) -> {
                if (centerCard == null) {
                    return;
                }
                soloLg.sendActionBarMessage("§e§l Cette carte §6 était " + centerCard.getRole().getName());
                soloLg.sendMessage("§e§l Cette carte §6 était " + centerCard.getRole().getName());
                soloLg.stopChoosing();
                soloLg.hideView();
            });
        }
        getGame().wait(getTimeout(), () -> {
            for (LGPlayer player : getPlayers()) {
                player.hideView();
            }
            callback.run();
        });
    }

    @EventHandler
    public void onGameJoin(LGGameEndEvent e) {
        if (e.getGame() == getGame()) {
            WrapperPlayServerScoreboardTeam teamDelete = new WrapperPlayServerScoreboardTeam();
            teamDelete.setMode(1);
            teamDelete.setName("loup_garou_list");

            for (LGPlayer lgp : getGame().getInGame())
                teamDelete.sendPacket(lgp.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSkinChange(LGSkinLoadEvent e) {
        if (e.getGame() == getGame())
            if (getPlayers().contains(e.getPlayer()) && getPlayers().contains(e.getTo()) && showSkins) {
                e.getProfile().getProperties().removeAll("textures");
                e.getProfile().getProperties().put("textures", LGCustomSkin.WEREWOLF.getProperty());
            }
    }

    @EventHandler
    public void onGameEnd(LGGameEndEvent e) {
        if (e.getGame() == getGame() && e.getWinType() == LGWinType.LOUPGAROU)
            for (LGPlayer lgp : getGame().getInGame())
                if (lgp.getRoleWinType() == RoleWinType.LOUP_GAROU)//Changed to wintype
                    e.getWinners().add(lgp);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUpdatePrefix(LGUpdatePrefixEvent e) {
        if (e.getGame() == getGame())
            if (getPlayers().contains(e.getTo()) && getPlayers().contains(e.getPlayer()))
                e.setPrefix(e.getPrefix() + "§c");
    }

    @EventHandler
    public void onDay(LGNightEndEvent e) {
        if (e.getGame() == getGame()) {
            showSkins = false;
            for (LGPlayer player : getPlayers())
                player.updateOwnSkin();
        }
    }

    @EventHandler
    public void onNight(LGDayEndEvent e) {
        if (e.getGame() == getGame()) {
            showSkins = true;
            for (LGPlayer player : getPlayers())
                player.updateOwnSkin();
        }
    }

}
