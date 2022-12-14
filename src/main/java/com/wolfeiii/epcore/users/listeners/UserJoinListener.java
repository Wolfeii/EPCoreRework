package com.wolfeiii.epcore.users.listeners;

import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.api.events.user.UserLoadEvent;
import com.wolfeiii.epcore.statistics.goal.StatisticGoal;
import com.wolfeiii.epcore.statistics.objects.Statistic;
import com.wolfeiii.epcore.statistics.objects.StatisticType;
import com.wolfeiii.epcore.users.UserHandler;
import com.wolfeiii.epcore.users.object.CoreUser;
import com.wolfeiii.epcore.settings.Setting;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Map;

public class UserJoinListener implements Listener {

    @EventHandler
    public void onUserPreJoin(AsyncPlayerPreLoginEvent event) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(
                EPCore.getPlugin(),
                () -> {
                    UserHandler userHandler = EPCore.getPlugin().getUserHandler();
                    CoreUser coreUser = userHandler.getUser(event.getUniqueId());

                    if (coreUser == null) {
                        // User is null, which means the player is not currently online, so we fetch it from the database.
                        // This code should always be executed, but is a safety if something goes wrong, so we don't get two of the same user.
                        coreUser = userHandler.createCoreUser(userHandler.loadFromUUID(event.getUniqueId()), true);
                        if (coreUser == null) {
                            // User is still null, which means it doesn't exist in the database, so it's a new player connecting.
                            Map<Setting, Boolean> settings = EPCore.getPlugin().getSettingsHandler()
                                    .retrieveSettings(event.getUniqueId());

                            Map<StatisticType, Statistic> statistics = EPCore.getPlugin().getStatisticsHandler()
                                    .retrieveStatistics(event.getUniqueId());

                            Map<StatisticGoal, Boolean> goals = EPCore.getPlugin().getStatisticsHandler()
                                    .getGoalHandler()
                                    .retrieveGoals(event.getUniqueId());

                            coreUser = new CoreUser(
                                    event.getUniqueId(),
                                    event.getName(),
                                    UserLoadEvent.UserLoadType.NEW
                            );

                            coreUser.setStatistics(statistics);
                            coreUser.setSettings(settings);
                            coreUser.setStatisticGoals(goals);

                            CoreUser finalUser = coreUser;
                            Bukkit.getScheduler().runTask(EPCore.getPlugin(), () -> {
                                UserLoadEvent userLoadEvent = new UserLoadEvent(finalUser);
                                EPCore.getPlugin().getServer().getPluginManager().callEvent(userLoadEvent);
                            });
                        } else {
                            CoreUser finalUser = coreUser;
                            Bukkit.getScheduler().runTask(EPCore.getPlugin(), () -> {
                                UserLoadEvent userLoadEvent = new UserLoadEvent(finalUser);
                                EPCore.getPlugin().getServer().getPluginManager().callEvent(userLoadEvent);
                            });
                        }

                    }
                }
        );
    }
}
