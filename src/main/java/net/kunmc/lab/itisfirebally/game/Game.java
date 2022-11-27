package net.kunmc.lab.itisfirebally.game;

import net.kunmc.lab.itisfirebally.Config;
import net.kunmc.lab.itisfirebally.entity.UnacceleratableFireball;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Game implements Listener {
    private final Plugin plugin;
    private final Config config;
    private final List<BukkitTask> tasks = new ArrayList<>();
    private final List<Fireball> fireballs = Collections.synchronizedList(new ArrayList<>());
    private final BossBar bossBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
    private boolean isRunning = false;
    private final Objective hitBacksObjective;
    private int summonedCount = 0;
    private int firedBallsCount = 0;

    public Game(Plugin plugin, Config config) {
        this.plugin = plugin;
        this.config = config;

        Bukkit.getPluginManager()
              .registerEvents(this, plugin);

        Scoreboard scoreboard = Bukkit.getScoreboardManager()
                                      .getMainScoreboard();
        scoreboard.getEntries()
                  .forEach(scoreboard::resetScores);

        Objective hitBack = scoreboard.getObjective("hitBack");
        if (hitBack == null) {
            hitBack = scoreboard.registerNewObjective("hitBack",
                                                      "dummy",
                                                      Component.text("打ち返した数"),
                                                      RenderType.INTEGER);
        }
        hitBack.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        this.hitBacksObjective = hitBack;
    }

    public void start() {
        if (isRunning) {
            return;
        }
        isRunning = true;

        Bukkit.getOnlinePlayers()
              .forEach(x -> {
                  hitBacksObjective.getScoreboard()
                                   .getEntries()
                                   .forEach(hitBacksObjective.getScoreboard()::resetScores);
              });
        summonedCount = 0;
        firedBallsCount = 0;

        tasks.add(new GameTask().runTaskTimer(plugin, 0, 0));
    }

    public void stop() {
        if (!isRunning) {
            return;
        }
        isRunning = false;

        Bukkit.getOnlinePlayers()
              .forEach(x -> {
                  x.sendTitle("終了", String.format("着弾数: %d/%d", firedBallsCount, summonedCount), 20, 80, 20);
                  bossBar.removePlayer(x);
              });
        fireballs.forEach(Entity::remove);
        tasks.forEach(BukkitTask::cancel);
        tasks.clear();
    }

    @EventHandler
    private void onFireballAttacked(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Fireball)) {
            return;
        }

        if (fireballs.stream()
                     .anyMatch(x -> x == e.getDamager())) {
            e.setCancelled(true);
            return;
        }

        boolean isBall = fireballs.contains(e.getEntity());
        if (!isBall) {
            return;
        }

        String name = e.getDamager()
                       .getName();
        if (e.getDamager() instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) e.getDamager()).getShooter();
            if (shooter instanceof Entity) {
                name = ((Entity) shooter).getName();
            }
        }
        Score score = hitBacksObjective.getScore(name);
        score.setScore(score.getScore() + 1);
        fireballs.remove(e.getEntity());
    }

    @EventHandler
    private void onFireballHit(ProjectileHitEvent e) {
        if (fireballs.contains(e.getEntity())) {
            firedBallsCount++;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    private class GameTask extends BukkitRunnable {
        private int progressTick = 0;
        private final ThreadLocalRandom random = ThreadLocalRandom.current();

        public GameTask() {
            bossBar.setTitle(title());
            bossBar.setProgress(1.0);
        }

        private String title() {
            return String.format("残り時間: %.2f", config.gameTickTime.minus(progressTick) / 20.0);
        }

        @Override
        public void run() {
            Bukkit.getOnlinePlayers()
                  .forEach(x -> {
                      bossBar.addPlayer(x);
                      x.sendActionBar(String.format("着弾数: %d/%d", firedBallsCount, summonedCount));
                  });

            progressTick++;
            bossBar.setTitle(title());
            bossBar.setProgress(Math.max(0.0,
                                         Math.min(1.0,
                                                  (config.gameTickTime.value() - progressTick) / (double) config.gameTickTime.value())));
            if (bossBar.getProgress() == 0.0) {
                stop();
                cancel();
                return;
            }

            if (progressTick % config.intervalTick.value() != 1) {
                return;
            }

            int amount = random.nextInt(config.amountRange.getLeft(), config.amountRange.getRight());
            summonedCount += amount;
            for (int i = 0; i < amount; i++) {
                Location origin = config.origin.value();
                double speed = random.nextDouble(config.speedRange.getLeft(), config.speedRange.getRight());
                ((CraftWorld) origin.getWorld()).addEntity(new UnacceleratableFireball(calcSpawnLocation(),
                                                                                       new Vector(0, -speed, 0)),
                                                           CreatureSpawnEvent.SpawnReason.CUSTOM,
                                                           x -> {
                                                               fireballs.add(((Fireball) x));
                                                               ((Fireball) x).setYield(((float) random.nextDouble(config.explosionPowerRange.getLeft(),
                                                                                                                  config.explosionPowerRange.getRight())));
                                                           });
            }
        }

        private Location calcSpawnLocation() {
            Location origin = config.origin.value()
                                           .clone();
            double range = random.nextDouble(0, config.radius.value());
            double radian = Math.toRadians(random.nextDouble(0.0, 360.0));
            double height = random.nextDouble(config.heightRange.getLeft(), config.heightRange.getRight());

            return origin.add(Math.cos(radian) * range, height, Math.sin(radian) * range);
        }
    }
}
