package fr.leomelki.loupgarou.roles;

import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.events.LGGameEndEvent;
import fr.leomelki.loupgarou.events.LGUpdatePrefixEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class RSbire extends Role {

    public RSbire(LGGame game) {
        super(game);
    }

    @Override
    public String getName() {
        return "§4§lSbire";
    }

    @Override
    public String getFriendlyName() {
        return "du §c§l" + getName();
    }

    @Override
    public String getShortDescription() {
        return "Tu gagnes avec les §c§lLoups-Garous.";
    }

    @Override
    public String getDescription() {
        return "Tu gagnes avec les §c§lLoups-Garous§f. Tu dois éliminer un villageois lors du vote. Essaie d'attirer les suspicions sur toi, car tu es considéré comme un villageois pendant les votes.";
    }

    @Override
    public String getTask() {
        return "Reconnais les loups-garous !";
    }

    @Override
    public String getBroadcastedTask() {
        return "Le sbire reconnaît les §c§lLoups-Garous§9.";
    }

    @Override
    public RoleType getType() {
        return RoleType.VILLAGER;
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
    protected void onNightTurn(LGPlayer player, Runnable callback) {
        player.showView();
    }

    @Override
    protected void onNightTurnTimeout(LGPlayer player) {
        player.stopChoosing();
        player.hideView();
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUpdatePrefix(LGUpdatePrefixEvent e) {
        if (e.getGame() == getGame())
            if (getPlayers().contains(e.getTo()) && e.getPlayer().getRoleWinType() == RoleWinType.LOUP_GAROU)
                e.setPrefix(e.getPrefix() + "§c");
    }

}
