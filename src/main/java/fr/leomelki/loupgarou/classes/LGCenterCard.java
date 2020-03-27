package fr.leomelki.loupgarou.classes;

import fr.leomelki.loupgarou.roles.Role;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class LGCenterCard extends LGHasRole {
    private static HashMap<Player, LGPlayer> cachedPlayers = new HashMap<Player, LGPlayer>();
    private final Location location;
    @Setter
    @Getter
    private Role role;

    public LGCenterCard(Location location) {
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
