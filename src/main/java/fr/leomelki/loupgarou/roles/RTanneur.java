package fr.leomelki.loupgarou.roles;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.classes.LGWinType;
import fr.leomelki.loupgarou.events.LGGameEndEvent;
import org.bukkit.event.EventHandler;

public class RTanneur extends Role {
    public RTanneur(LGGame game) {
        super(game);
    }

    @Override
    public RoleType getType() {
        return RoleType.TANNEUR;
    }

    @Override
    public RoleWinType getWinType() {
        return RoleWinType.TANNEUR;
    }

    @Override
    public String getName() {
        return "§1§lTanneur";
    }

    @Override
    public String getFriendlyName() {
        return "du " + getName();
    }

    @Override
    public String getShortDescription() {
        return "Tu gagnes en mourant.";
    }

    @Override
    public String getDescription() {
        return "Tu gagnes en mourant. Attire l'attention sur toi lors de votes, mais n'éveille pas trop de soupçons.";
    }

    @Override
    public String getTask() {
        return "";
    }

    @Override
    public String getBroadcastedTask() {
        return "";
    }

    @Override
    public int getTimeout() {
        return -1;
    }

    @Override
    public RoleWinType getRoleWinOnDeath() {
        return RoleWinType.TANNEUR;
    }

    @EventHandler
    public void onGameEnd(LGGameEndEvent e) {
        if (e.getGame() == getGame() && e.getWinTypes().contains(LGWinType.TANNEUR))
            for (LGPlayer lgp : getGame().getInGame())
                if (lgp.getRoleWinType() == RoleWinType.TANNEUR)//Changed to wintype
                    e.getWinners().add(lgp);
    }

}
