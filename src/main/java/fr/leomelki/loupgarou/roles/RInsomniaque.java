package fr.leomelki.loupgarou.roles;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;

public class RInsomniaque extends Role {

    public RInsomniaque(LGGame game) {
        super(game);
    }

    @Override
    public String getName() {
        return "§3§ll'Insomniaque";
    }

    @Override
    public String getFriendlyName() {
        return "de " + getName();
    }

    @Override
    public String getShortDescription() {
        return "Tu découvres ton rôle final";
    }

    @Override
    public String getDescription() {
        return "Tu peux voir ton rôle en fin de nuit. Tu gagnes avec le camp associé à ce rôle.";
    }

    @Override
    public String getTask() {
        return "Découvre ton nouveau rôle.";
    }

    @Override
    public String getBroadcastedTask() {
        return "§6§lL'insomniaque découvre son nouveau rôle.";
    }

    @Override
    public RoleWinType getRoleWinOnDeath() {
        return RoleWinType.VILLAGE;
    }

    @Override
    public RoleType getType() {
        return RoleType.VILLAGER;
    }

    @Override
    public int getTimeout() {
        return 8;
    }

    @Override
    protected void onNightTurnTimeout(LGPlayer player) {
        player.stopChoosing();
        player.hideView();
    }

    @Override
    protected void onNightTurn(LGPlayer player, Runnable callback) {
        player.showView();
        player.sendMessage("Ton rôle à la fin de la nuit est " + player.getCurrentRole().getName() + ".");
    }
}
