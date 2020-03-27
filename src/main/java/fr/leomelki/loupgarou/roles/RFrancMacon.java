package fr.leomelki.loupgarou.roles;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.classes.chat.LGChat;
import fr.leomelki.loupgarou.events.LGUpdatePrefixEvent;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class RFrancMacon extends Role {

    @Getter
    private LGChat chat = new LGChat((sender, message) -> {
        return "§a" + sender.getName() + " §6» §f" + message;
    });

    public RFrancMacon(LGGame game) {
        super(game);
    }

    @Override
    public RoleType getType() {
        return RoleType.VILLAGER;
    }

    @Override
    public RoleWinType getWinType() {
        return RoleWinType.VILLAGE;
    }

    @Override
    public String getName() {
        return "§a§lFrancMacon";
    }

    @Override
    public String getFriendlyName() {
        return "des " + getName() + "s.";
    }

    @Override
    public String getShortDescription() {
        return "Tu gagnes avec le §a§lVillage§f";
    }

    @Override
    public String getDescription() {
        return "Tu gagnes avec le §a§lVillage§f. Vous êtes plusieurs franc-maçons et pouvez vous reconnaître.";
    }

    @Override
    public String getTask() {
        return "Reconnaissez-vous !";
    }

    @Override
    public String getBroadcastedTask() {
        return "Les " + getName() + "s§9 se reconnaissent.";
    }

    @Override
    public RoleWinType getRoleWinOnDeath() {
        return RoleWinType.VILLAGE;
    }

    @Override
    public void onNightTurn(Runnable callback) {
        for (LGPlayer player : getPlayers()) {
            player.showView();
            player.sendMessage("§6" + getTask());
            player.joinChat(chat);
        }
        getGame().wait(getTimeout(), () -> {
            for (LGPlayer player : getPlayers()) {
                player.hideView();
            }
            callback.run();
        });
    }


    @Override
    public int getTimeout() {
        return 10;
    }

    @Override
    protected void onNightTurnTimeout(LGPlayer player) {
        player.stopChoosing();
        player.hideView();
    }

    @Override
    public void join(LGPlayer player, boolean sendMessage) {
        super.join(player, sendMessage);
        //On peut créer des cheats grâce à ça (qui permettent de savoir qui est lg/inf)
        for (LGPlayer p : getPlayers())
            p.updatePrefix();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUpdatePrefix(LGUpdatePrefixEvent e) {
        if (e.getGame() == getGame())
            if (getPlayers().contains(e.getTo()) && getPlayers().contains(e.getPlayer()))
                e.setPrefix(e.getPrefix() + "§a");
    }

}
