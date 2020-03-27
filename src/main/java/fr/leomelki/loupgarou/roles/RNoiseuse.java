package fr.leomelki.loupgarou.roles;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;

public class RNoiseuse extends Role {

    public RNoiseuse(LGGame game) {
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
        return "§9§lNoiseuse";
    }

    @Override
    public String getFriendlyName() {
        return "de la " + getName();
    }

    @Override
    public String getShortDescription() {
        return "Tu échanges le rôle de deux autres joueurs.";
    }

    @Override
    public String getDescription() {
        return "Tu échanges le rôle de deux autres joueurs.";
    }

    @Override
    public String getTask() {
        return "Choisis deux joueurs, et échange leurs rôles. Tu ne peux pas te cibler.";
    }

    @Override
    public String getBroadcastedTask() {
        return "La noiseuse peut échanger le rôle de deux joueurs.";
    }

    @Override
    public RoleWinType getRoleWinOnDeath() {
        return RoleWinType.VILLAGE;
    }

    @Override
    protected void onNightTurn(LGPlayer player, Runnable callback) {
        player.showView();
        player.sendMessage("Cible le premier joueur à échanger.");
        player.choose((choosenPlayer, choosenCenterCard) -> {
            if (choosenPlayer == null || choosenPlayer == player) {
                return;
            }
            player.sendActionBarMessage("Tu as ciblé §e§l" + choosenPlayer.getName());
            player.sendMessage("Tu as ciblé §e§l" + choosenPlayer.getName());
            player.stopChoosing();
            player.sendMessage("Cible le second joueur à échanger.");
            player.choose((choosenPlayer2, choosenCenterCard2) -> {
                if (choosenPlayer2 == null || choosenPlayer2 == player || choosenPlayer2 == choosenPlayer) {
                    return;
                }
                player.sendActionBarMessage("Tu as ciblé §e§l" + choosenPlayer2.getName());
                player.sendMessage("Tu as ciblé §e§l" + choosenPlayer2.getName());
                player.sendMessage("Tu as échangé les rôles de §e§l" + choosenPlayer.getName() + "§f et de §e§l" + choosenPlayer2.getName());
                player.stopChoosing();
                player.hideView();
                Role roleToGive = choosenPlayer.getCurrentRole();
                choosenPlayer.setCurrentRole(choosenPlayer2.getCurrentRole());
                choosenPlayer2.setCurrentRole(roleToGive);
            });
        });
    }


    @Override
    public int getTimeout() {
        return 12;
    }

    @Override
    protected void onNightTurnTimeout(LGPlayer player) {
        player.stopChoosing();
        player.hideView();
    }

}
