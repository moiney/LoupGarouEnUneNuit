package fr.leomelki.loupgarou.roles;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;

public class RSoulard extends Role {
    public RSoulard(LGGame game) {
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
        return "§2§lSoulard";
    }

    @Override
    public String getFriendlyName() {
        return "du " + getName();
    }

    @Override
    public String getShortDescription() {
        return "Tu échanges avec le centre.";
    }

    @Override
    public String getDescription() {
        return "Tu échanges ton rôle avec l'un de ceux du centre. Ton camp pourrait potentiellement changer.";
    }

    @Override
    public String getTask() {
        return "Choisis une carte du centre, et échange son rôle avec le tien.";
    }

    @Override
    public String getBroadcastedTask() {
        return "Le soulard échange son rôle avec l'un du centre.";
    }

    @Override
    public RoleWinType getRoleWinOnDeath() {
        return RoleWinType.VILLAGE;
    }

    @Override
    protected void onNightTurn(LGPlayer player, Runnable callback) {
        player.showView();
        player.choose((choosenPlayer, choosenCenterCard) -> {
            if (choosenCenterCard == null) {
                return;
            }
            player.sendActionBarMessage("Tu as pris une carte du centre.");
            player.sendMessage("Tu as pris une carte du centre.");
            player.stopChoosing();
            player.hideView();
            Role roleToGive = player.getCurrentRole();
            player.setCurrentRole(choosenCenterCard.getRole());
            choosenCenterCard.setRole(roleToGive);
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
