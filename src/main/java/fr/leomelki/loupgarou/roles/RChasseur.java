package fr.leomelki.loupgarou.roles;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent;
import fr.leomelki.loupgarou.events.LGResumeFinishEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.stream.Collectors;

public class RChasseur extends Role {

    private boolean notInTime = false;

    public RChasseur(LGGame game) {
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
        return "§8§lChasseur";
    }

    @Override
    public String getFriendlyName() {
        return "du " + getName();
    }

    @Override
    public String getShortDescription() {
        return "Tu te venges à ta mort.";
    }

    @Override
    public String getDescription() {
        return "Tu gagnes avec le §a§lVillage§f. Si tu meurs lors du vote, tu peux rediriger ta mort sur un autre joueur.";
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
        return RoleWinType.VILLAGE;
    }

    @EventHandler
    public void onPlayerKill(LGPlayerKilledEvent e) {
        if (notInTime) {
            return;
        }
        List<LGPlayer> hunt = getGame().getInGame().stream().filter(player -> player.getCurrentRole() == this).collect(Collectors.toList());
        if (hunt.isEmpty()) return;
        LGPlayer chasseur = hunt.get(0);

        if (e.getKilled().getCurrentRole() != this) {
            return;
        }
        e.setCancelled(true);
        getGame().broadcastMessage("Le " + getName() + "§f a été désigné. Il peut rediriger sa mort.");
        chasseur.sendTitle(getName(), "C'est TOI !", 40);
        chasseur.sendMessage("Tu es " + getName() + " !!!");
        chasseur.sendMessage("Choisis une nouvelle cible !");
        chasseur.sendActionBarMessage("Choisis une nouvelle cible !");
        chasseur.stopChoosing();
        chasseur.choose((choosenPlayer, choosenCenterCard) -> {
            if (choosenPlayer == null || choosenPlayer == chasseur) {
                return;
            }
            getGame().cancelWait();
            chasseur.sendActionBarMessage("§e§l" + choosenPlayer.getName() + "§6 était " + choosenPlayer.getCurrentRole().getName() + "§6. Tu l'as tué.");
            chasseur.sendMessage("§e§l" + choosenPlayer.getName() + "§6 était " + choosenPlayer.getCurrentRole().getName() + "§6. Tu l'as tué.");
            getGame().broadcastMessage("§e§l" + chasseur.getName() + " a tué §e§l" + choosenPlayer.getName() + "§6. Il était " + choosenPlayer.getCurrentRole().getName());
            chasseur.stopChoosing();
            Bukkit.getPluginManager().callEvent(new LGResumeFinishEvent(getGame(), chasseur, choosenPlayer));
        });
        getGame().wait(10, () -> {
            getGame().broadcastMessage("Le " + getName() + " (joueur : " + chasseur.getName() + ") n'a pas tué à temps.");
            notInTime = true;
        });
    }

}
