package fr.leomelki.loupgarou.events;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.classes.LGWinType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

import java.util.List;
import java.util.Set;

public class LGGameEndEvent extends LGEvent implements Cancellable {
	@Getter
	@Setter
	private boolean cancelled;
	@Getter
	private final Set<LGWinType> winTypes;
	@Getter
	private final List<LGPlayer> winners;

	public LGGameEndEvent(LGGame game, Set<LGWinType> winTypes, List<LGPlayer> winners) {
		super(game);
		this.winTypes = winTypes;
		this.winners = winners;
	}
}