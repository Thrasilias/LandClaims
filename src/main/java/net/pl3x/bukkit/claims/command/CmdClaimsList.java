package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CmdClaimsList implements TabExecutor {
    private final LandClaims plugin;

    public CmdClaimsList(LandClaims plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(Bukkit.getOfflinePlayers())
                    .filter(target -> target.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("adminclaimslist")) {
            if (!sender.hasPermission("command.user.claimslist.admin")) {
                Lang.send(sender, command.getPermissionMessage());
                return true;
            }

            Lang.send(sender, Lang.CLAIMSLIST_HEADER
                    .replace("{owner}", "Admin"));
            plugin.getClaimManager().getTopLevelClaims().stream()
                    .filter(Claim::isAdminClaim)
                    .forEach(claim -> Lang.send(sender, Lang.CLAIMSLIST_CLAIM_ADMIN
                            .replace("{clean-location}", cleanLocation(claim.getCoordinates().getMinLocation()))
                            .replace("{area}", Integer.toString(claim.getCoordinates().getArea()))));
            return true;
        }

        OfflinePlayer target;
        if (args.length > 0) {
            if (!sender.hasPermission("command.user.claimslist.others")) {
                Lang.send(sender, command.getPermissionMessage());
                return true;
            }

            target = Bukkit.getOfflinePlayer(args[0]);
            if (target == null) {
                Lang.send(sender, Lang.COMMAND_PLAYER_NOT_FOUND);
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                Lang.send(sender, Lang.PLAYER_COMMAND);
                return true;
            }
            target = (Player) sender;
        }

        Pl3xPlayer pl3xTarget = plugin.getPlayerManager().getPlayer(target.getUniqueId());
        Collection<Claim> claims = pl3xTarget.getClaims();

        Lang.send(sender, Lang.CLAIM_BLOCK_COUNTS
                .replace("{accrued}", Integer.toString((int) pl3xTarget.getClaimBlocks()))
                .replace("{bonus}", Integer.toString((int) pl3xTarget.getBonusBlocks()))
                .replace("{total}", Integer.toString((int) (pl3xTarget.getClaimBlocks() + pl3xTarget.getBonusBlocks()))));

        if (claims.size() > 0) {
            Lang.send(sender, Lang.CLAIMSLIST_HEADER
                    .replace("{owner}", target.getName() + "'s"));
            claims.forEach(claim -> Lang.send(sender, Lang.CLAIMSLIST_CLAIM
                    .replace("{clean-location}", cleanLocation(claim.getCoordinates().getMinLocation()))
                    .replace("{area}", Integer.toString(claim.getCoordinates().getArea()))));
            Lang.send(sender, Lang.CLAIMSLIST_FOOTER
                    .replace("{remaining}", Integer.toString(pl3xTarget.getRemainingClaimBlocks())));
        }

        return true;
    }

    private String cleanLocation(Location location) {
        return Lang.CLEAN_LOCATION
                .replace("{world}", location.getWorld().getName())
                .replace("{x}", Integer.toString(location.getBlockX()))
                .replace("{y}", Integer.toString(location.getBlockY()))
                .replace("{z}", Integer.toString(location.getBlockZ()));
    }
}
