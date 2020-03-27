package fr.leomelki.loupgarou.roles;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;

public class RVoleur extends Role {

    public RVoleur(LGGame game) {
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
        return "§5§lVoleur";
    }

    @Override
    public String getFriendlyName() {
        return "du " + getName();
    }

    @Override
    public String getShortDescription() {
        return "Tu échanges ton rôle.";
    }

    @Override
    public String getDescription() {
        return "Tu regardes le rôle d'un autre joueur puis échange ton rôle avec lui. Tu changes de camp dans le cas nécessaire. Si tu obtiens la carte du chasseur, tu pourras l'utiliser.";
    }

    @Override
    public String getTask() {
        return "Choisis un joueur, tu prendras son rôle, et lui récupèreras le tien.";
    }

    @Override
    public String getBroadcastedTask() {
        return "Le " + getName() + "§9 peut échanger son rôle avec celui d'un autre joueur.";
    }

    @Override
    public RoleWinType getRoleWinOnDeath() {
        return RoleWinType.VILLAGE;
    }

    @Override
    protected void onNightTurn(LGPlayer player, Runnable callback) {
        player.showView();
        player.choose((choosenPlayer, choosenCenterCard) -> {
            if (choosenPlayer == null || choosenPlayer == player) {
                return;
            }
            player.sendActionBarMessage("§e§l" + choosenPlayer.getName() + "§6 était " + choosenPlayer.getCurrentRole().getName() + "§6. Tu as pris son rôle, et il a dorénavant le tien.");
            player.sendMessage("§6Tu découvres que §7§l" + choosenPlayer.getName() + "§6 était " + choosenPlayer.getCurrentRole().getName() + "§6. Tu as pris son rôle, et il a dorénavant le tien.");
            player.stopChoosing();
            player.hideView();
            Role roleToGive = player.getCurrentRole();
            player.setCurrentRole(choosenPlayer.getCurrentRole());
            choosenPlayer.setCurrentRole(roleToGive);
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
