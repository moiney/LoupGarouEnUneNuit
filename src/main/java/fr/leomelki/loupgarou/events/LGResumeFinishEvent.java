package fr.leomelki.loupgarou.events;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import lombok.Getter;

public class LGResumeFinishEvent extends LGEvent {

    @Getter
    private final LGPlayer chasseur;
    @Getter
    private final LGPlayer killed;

    public LGResumeFinishEvent(LGGame game, LGPlayer chasseur, LGPlayer killed) {
        super(game);
        this.chasseur = chasseur;
        this.killed = killed;
    }

}
