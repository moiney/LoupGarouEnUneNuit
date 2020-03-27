package fr.leomelki.loupgarou.events;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;

public class LGPlayerKilledEvent extends LGEvent implements Cancellable{
	public LGPlayerKilledEvent(LGGame game, LGPlayer killed, Reason reason) {
		super(game);
		this.killed = killed;
		this.reason = reason;
	}

	@Getter @Setter boolean cancelled;
    
    @Getter @Setter private LGPlayer killed;
    @Getter @Setter private Reason reason;

	@RequiredArgsConstructor
	public static enum Reason {
		VOTE("§7§l%s§4 a été victime du vote"),
		CHASSEUR("§7§l%s§4 est mort sur le coup");

		@Getter
		private final String message;
	}
	
}
