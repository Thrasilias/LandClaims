package net.pl3x.bukkit.claims.event;

import net.pl3x.bukkit.claims.claim.Claim;
import org.bukkit.entity.Player;

public class CreateClaimEvent extends ClaimEvent {
    public CreateClaimEvent(Player player, Claim claim) {
        super(player, claim);
    }
}
