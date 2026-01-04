package fr.mrbaguette07.slconnector.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class FoliaScheduler {
    
    private static Boolean isFolia = null;
    private static Method getAsyncSchedulerMethod;
    private static Method getGlobalRegionSchedulerMethod;
    private static Method getRegionSchedulerMethod;
    private static Method getEntitySchedulerMethod;
    private static Method asyncRunNowMethod;
    private static Method globalRunMethod;
    private static Method regionalRunMethod;
    private static Method entityRunMethod;
    private static Method asyncRunDelayedMethod;
    private static Method globalRunDelayedMethod;
    
    static {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
            
            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
            getAsyncSchedulerMethod = bukkitClass.getMethod("getAsyncScheduler");
            getGlobalRegionSchedulerMethod = bukkitClass.getMethod("getGlobalRegionScheduler");
            getRegionSchedulerMethod = bukkitClass.getMethod("getRegionScheduler");
            
            Class<?> entityClass = Class.forName("org.bukkit.entity.Entity");
            getEntitySchedulerMethod = entityClass.getMethod("getScheduler");
            
            Class<?> asyncSchedulerClass = Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
            asyncRunNowMethod = asyncSchedulerClass.getMethod("runNow", Plugin.class, Consumer.class);
            asyncRunDelayedMethod = asyncSchedulerClass.getMethod("runDelayed", Plugin.class, Consumer.class, long.class, TimeUnit.class);
            
            Class<?> globalSchedulerClass = Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            globalRunMethod = globalSchedulerClass.getMethod("run", Plugin.class, Consumer.class);
            globalRunDelayedMethod = globalSchedulerClass.getMethod("runDelayed", Plugin.class, Consumer.class, long.class);
            
            Class<?> regionSchedulerClass = Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
            regionalRunMethod = regionSchedulerClass.getMethod("run", Plugin.class, Location.class, Consumer.class);
            
            Class<?> entitySchedulerClass = Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
            entityRunMethod = entitySchedulerClass.getMethod("run", Plugin.class, Consumer.class, Runnable.class);
            
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            isFolia = false;
        }
    }
    
    public static boolean isFolia() {
        return isFolia != null && isFolia;
    }
    
    public static void runAsync(Plugin plugin, Runnable task) {
        if (isFolia()) {
            try {
                Object asyncScheduler = getAsyncSchedulerMethod.invoke(null);
                Consumer<Object> consumer = scheduledTask -> task.run();
                asyncRunNowMethod.invoke(asyncScheduler, plugin, consumer);
            } catch (Exception e) {
                plugin.getLogger().severe("Erreur lors de l'exécution de la tâche asynchrone Folia: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }
    
    public static void runSync(Plugin plugin, Runnable task) {
        if (isFolia()) {
            try {
                Object globalScheduler = getGlobalRegionSchedulerMethod.invoke(null);
                Consumer<Object> consumer = scheduledTask -> task.run();
                globalRunMethod.invoke(globalScheduler, plugin, consumer);
            } catch (Exception e) {
                plugin.getLogger().severe("Erreur lors de l'exécution de la tâche synchrone Folia: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            if (Bukkit.isPrimaryThread()) {
                task.run();
            } else {
                Bukkit.getScheduler().runTask(plugin, task);
            }
        }
    }
    
    public static void runAtEntity(Plugin plugin, Entity entity, Runnable task) {
        if (isFolia()) {
            try {
                Object entityScheduler = getEntitySchedulerMethod.invoke(entity);
                Consumer<Object> consumer = scheduledTask -> task.run();
                entityRunMethod.invoke(entityScheduler, plugin, consumer, null);
            } catch (Exception e) {
                plugin.getLogger().severe("Erreur lors de l'exécution de la tâche sur l'entité Folia: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            if (Bukkit.isPrimaryThread()) {
                task.run();
            } else {
                Bukkit.getScheduler().runTask(plugin, task);
            }
        }
    }
    
    public static void runAtLocation(Plugin plugin, Location location, Runnable task) {
        if (isFolia()) {
            try {
                Object regionScheduler = getRegionSchedulerMethod.invoke(null);
                Consumer<Object> consumer = scheduledTask -> task.run();
                regionalRunMethod.invoke(regionScheduler, plugin, location, consumer);
            } catch (Exception e) {
                plugin.getLogger().severe("Erreur lors de l'exécution de la tâche régionale Folia: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            if (Bukkit.isPrimaryThread()) {
                task.run();
            } else {
                Bukkit.getScheduler().runTask(plugin, task);
            }
        }
    }
    
    public static void runAsyncLater(Plugin plugin, Runnable task, long delay, TimeUnit unit) {
        if (isFolia()) {
            try {
                Object asyncScheduler = getAsyncSchedulerMethod.invoke(null);
                Consumer<Object> consumer = scheduledTask -> task.run();
                asyncRunDelayedMethod.invoke(asyncScheduler, plugin, consumer, delay, unit);
            } catch (Exception e) {
                plugin.getLogger().severe("Erreur lors de l'exécution de la tâche asynchrone retardée Folia: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            long ticks = unit.toMillis(delay) / 50;
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, ticks);
        }
    }
    
    public static void runSyncLater(Plugin plugin, Runnable task, long delay, TimeUnit unit) {
        if (isFolia()) {
            try {
                Object globalScheduler = getGlobalRegionSchedulerMethod.invoke(null);
                Consumer<Object> consumer = scheduledTask -> task.run();
                long ticks = unit.toMillis(delay) / 50;
                globalRunDelayedMethod.invoke(globalScheduler, plugin, consumer, ticks);
            } catch (Exception e) {
                plugin.getLogger().severe("Erreur lors de l'exécution de la tâche synchrone retardée Folia: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            long ticks = unit.toMillis(delay) / 50;
            Bukkit.getScheduler().runTaskLater(plugin, task, ticks);
        }
    }
}
