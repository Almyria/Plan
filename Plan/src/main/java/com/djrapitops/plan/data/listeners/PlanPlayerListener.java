package main.java.com.djrapitops.plan.data.listeners;

import com.djrapitops.plugin.utilities.player.Fetch;
import main.java.com.djrapitops.plan.Plan;
import main.java.com.djrapitops.plan.data.Session;
import main.java.com.djrapitops.plan.data.cache.DataCache;
import main.java.com.djrapitops.plan.data.handling.DBCommitProcessor;
import main.java.com.djrapitops.plan.data.handling.player.*;
import main.java.com.djrapitops.plan.utilities.MiscUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * Event Listener for PlayerJoin, PlayerQuit and PlayerKickEvents.
 *
 * @author Rsl1122
 * @since 2.0.0
 */
public class PlanPlayerListener implements Listener {

    private final Plan plugin;
    private final DataCache cache;

    /**
     * Class Constructor.
     * <p>
     * Copies the references to multiple handlers from Current instance of cache.
     *
     * @param plugin Current instance of Plan
     */
    public PlanPlayerListener(Plan plugin) {
        this.plugin = plugin;
        cache = plugin.getDataCache();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        PlayerLoginEvent.Result result = event.getResult();
        UUID uuid = event.getPlayer().getUniqueId();
        if (result == PlayerLoginEvent.Result.KICK_BANNED) {
            plugin.addToProcessQueue(new BanProcessor(uuid, true));
        } else {
            plugin.addToProcessQueue(new BanProcessor(uuid, false));
        }
    }

    /**
     * PlayerJoinEvent Listener.
     * <p>
     * Adds processing information to the Queue.
     *
     * @param event The Fired event.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getNotificationCenter().checkNotifications(Fetch.wrapBukkit(player));

        UUID uuid = player.getUniqueId();
        long time = MiscUtils.getTime();

        String world = player.getWorld().getName();
        String gm = player.getGameMode().name();

        String ip = player.getAddress().getAddress().toString();

        String playerName = player.getName();
        String displayName = player.getDisplayName();

        int playersOnline = plugin.getTpsCountTimer().getLatestPlayersOnline();

        cache.cacheSession(uuid, Session.start(time, world, gm));

        plugin.addToProcessQueue(
                new RegisterProcessor(uuid, time, playersOnline), //TODO Add required variables after UsersTable is done.
                new IPUpdateProcessor(uuid, ip),
                new NameProcessor(uuid, playerName, displayName),
                new DBCommitProcessor(plugin.getDB())
        );
    }

    /**
     * PlayerQuitEvent Listener.
     * <p>
     * Adds a LogoutInfo to the processing Queue.
     *
     * @param event Fired event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

        long time = MiscUtils.getTime();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        plugin.addToProcessQueue(
                new BanProcessor(uuid, player.isBanned()),
                new EndSessionProcessor(uuid, time)
        );
    }

    /**
     * PlayerKickEvent Listener.
     * <p>
     * After KickEvent, the QuitEvent is automatically called.
     *
     * @param event Fired event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.isCancelled()) {
            return;
        }
        UUID uuid = event.getPlayer().getUniqueId();
        plugin.addToProcessQueue(new KickProcessor(uuid));
    }
}
