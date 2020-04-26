package fr.leomelki.loupgarou.roles;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;

public class RVoyante extends Role {


    public RVoyante(LGGame game) {
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
        return "§d§lVoyante";
    }

    @Override
    public String getFriendlyName() {
        return "de la " + getName();
    }

    @Override
    public String getShortDescription() {
        return "Tu regardes un autre rôle.";
    }

    @Override
    public String getDescription() {
        return "Tu regardes le rôle d'un autre joueur ou bien celui d'une carte du centre.";
    }

    @Override
    public String getTask() {
        return "Choisis un joueur ou bien une carte au centre, et découvre son rôle.";
    }

    @Override
    public String getBroadcastedTask() {
        return "La " + getName() + "§f peut découvrir le rôle d'un autre joueur ou d'une carte au centre.";
    }

    @Override
    public RoleWinType getRoleWinOnDeath() {
        return RoleWinType.VILLAGE;
    }

    @Override
    protected void onNightTurn(LGPlayer player, Runnable callback) {
        player.showView();
        player.choose((choosenPlayer, choosenCenterCard) -> {
            if (choosenPlayer != null && choosenPlayer != player) {
                player.sendActionBarMessage("§e§l" + choosenPlayer.getName() + "§6 est §e§l" + choosenPlayer.getCurrentRole().getName());
                player.sendMessage("§6Tu découvres que §7§l" + choosenPlayer.getName() + "§6 est " + choosenPlayer.getCurrentRole().getName());
            } else if (choosenCenterCard != null) {
                player.sendActionBarMessage("Cette carte du centre est " + choosenCenterCard.getRole().getName());
                player.sendMessage("Cette carte du centre est " + choosenCenterCard.getRole().getName());
            } else {
                return;
            }
            player.stopChoosing();
            player.hideView();
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

}
