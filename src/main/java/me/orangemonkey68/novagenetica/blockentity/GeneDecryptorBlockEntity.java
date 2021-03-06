package me.orangemonkey68.novagenetica.blockentity;

import me.orangemonkey68.novagenetica.NovaGenetica;
import me.orangemonkey68.novagenetica.abilities.Ability;
import me.orangemonkey68.novagenetica.gui.Generic1x1GuiDescription;
import me.orangemonkey68.novagenetica.helper.item.ItemHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class GeneDecryptorBlockEntity extends BaseMachineBlockEntity{
    public GeneDecryptorBlockEntity() {
        super(NovaGenetica.GENE_DECRYPTOR_BLOCK_ENTITY_TYPE, "block.novagenetica.gene_decryptor", NovaGenetica.GENE_DECRYPTOR_ID, 2);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if(side == Direction.UP){
            return new int[]{0};
        } else if (side == Direction.DOWN){
            return new int[]{1};
        } else {
            return new int[0];
        }
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return isValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 1 && dir == Direction.DOWN;
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new Generic1x1GuiDescription(syncId, player.inventory, ScreenHandlerContext.create(world, pos));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void doneProcessing() {
        ItemStack input = itemStacks.get(0);
        CompoundTag tag = input.getTag();
        if(tag != null){
            Identifier abilityId = new Identifier(tag.getString("ability"));
            if(NovaGenetica.ABILITY_REGISTRY.containsId(abilityId)){
                NovaGenetica.LOGGER.info("Ability exists");
                Ability ability = NovaGenetica.ABILITY_REGISTRY.get(abilityId);
                ItemStack stack = ItemHelper.getGene(null, abilityId, false, true, ability.getColor());

                itemStacks.set(0, ItemStack.EMPTY);
                itemStacks.set(1, stack);
                markDirty();
            }
        }
    }

    @Override
    public boolean isInventoryValid() {
        return
                isValid(0, itemStacks.get(0)) &&
                isValid(1, itemStacks.get(1));
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if(slot == 0){
            CompoundTag tag = stack.getTag();

            if(tag != null && stack.getItem() == NovaGenetica.GENE_ITEM){
                if(tag.contains("ability") && tag.contains("identified")){
                    return !tag.getBoolean("identified");
                }
            }
        }else if (slot == 1){
            return (stack == ItemStack.EMPTY || stack.getItem() == Items.AIR);
        }
        return false;
    }
}
