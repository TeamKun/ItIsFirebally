package net.kunmc.lab.itisfirebally;

import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.value.DoubleValue;
import net.kunmc.lab.configlib.value.IntegerValue;
import net.kunmc.lab.configlib.value.LocationValue;
import net.kunmc.lab.configlib.value.tuple.Double2DoublePairValue;
import net.kunmc.lab.configlib.value.tuple.Integer2IntegerPairValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Config extends BaseConfig {
    public final IntegerValue gameTickTime = new IntegerValue(3600, 1, Integer.MAX_VALUE);
    public final LocationValue origin = new LocationValue();
    public final DoubleValue radius = new DoubleValue(50.0);
    public final Double2DoublePairValue heightRange = new Double2DoublePairValue(20.0, 40.0).setValidator(x -> {
        return x.getLeft() < x.getRight();
    });
    public final IntegerValue intervalTick = new IntegerValue(200, 2, Integer.MAX_VALUE);
    public final Integer2IntegerPairValue amountRange = new Integer2IntegerPairValue(5, 20).setValidator(x -> {
        return x.getLeft() < x.getRight();
    });
    public final Double2DoublePairValue speedRange = new Double2DoublePairValue(0.1, 0.3).setValidator(x -> {
        return x.getLeft() < x.getRight();
    });
    public final Double2DoublePairValue explosionPowerRange = new Double2DoublePairValue(1.0, 4.0).setValidator(x -> {
        return x.getLeft() < x.getRight();
    });

    public Config(@NotNull Plugin plugin) {
        super(plugin);
    }
}
