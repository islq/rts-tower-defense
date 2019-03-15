package io.anuke.mindustry.world.blocks.defense.turrets;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.content.fx.BlockFx;
import io.anuke.mindustry.type.Liquid;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.consumers.ConsumeLiquidFilter;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.util.Mathf;

import static io.anuke.mindustry.Vars.tilesize;

public class CooledTurret extends Turret{
    /**How much reload is lowered by for each unit of liquid of heat capacity 1.*/
    protected float coolantMultiplier = 1f;
    /**Max coolant used per tick.*/
    protected float maxCoolantUsed = 1f;
    protected Effect coolEffect = BlockFx.fuelburn;

    public CooledTurret(String name){
        super(name);
        hasLiquids = true;
        liquidCapacity = 20f;

        consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability < 0.1f, 0.01f)).update(false).optional(true);
    }

    @Override
    protected void updateShooting(Tile tile){
        super.updateShooting(tile);

        TurretEntity entity = tile.entity();

        Tile core = Vars.state.getTeamCore(entity.getTeam());
        if (core == null) return;

        final float[] liquidAmount = {liquidCapacity - entity.liquids.total()};
        if (liquidAmount[0] > 0.1) {
            core.entity.liquids.forEach((liquid, amount) -> {
                if (amount < 1000) return;
                if (!acceptLiquid(tile, core, liquid, 0.1f)) return;
                float n = Math.min(amount, liquidAmount[0]);
                entity.liquids.add(liquid, n);
                core.entity.liquids.remove(liquid, n);
                liquidAmount[0] -= n;
            });
        }

        Liquid liquid = entity.liquids.current();

        float used = Math.min(Math.min(entity.liquids.get(liquid), maxCoolantUsed * Timers.delta()), Math.max(0, ((reload - entity.reload) / coolantMultiplier) / liquid.heatCapacity));
        entity.reload += (used * liquid.heatCapacity) / liquid.heatCapacity;
        entity.liquids.remove(liquid, used);

        if(Mathf.chance(0.06 * used)){
            Effects.effect(coolEffect, tile.drawx() + Mathf.range(size * tilesize / 2f), tile.drawy() + Mathf.range(size * tilesize / 2f));
        }
    }

    @Override
    public boolean acceptLiquid(Tile tile, Tile source, Liquid liquid, float amount){
        return super.acceptLiquid(tile, source, liquid, amount) && liquid.temperature <= 0.5f;
    }
}
