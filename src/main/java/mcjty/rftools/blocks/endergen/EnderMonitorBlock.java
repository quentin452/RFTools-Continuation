package mcjty.rftools.blocks.endergen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.lib.container.EmptyContainer;
import mcjty.rftools.RFTools;
import mcjty.rftools.blocks.logic.LogicSlabBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

public class EnderMonitorBlock extends LogicSlabBlock {

    public EnderMonitorBlock() {
        super(Material.iron, "enderMonitorBlock", EnderMonitorTileEntity.class);
        setCreativeTab(RFTools.tabRfTools);
    }

    @Override
    public int getGuiID() {
        return RFTools.GUI_ENDERMONITOR;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        EnderMonitorTileEntity enderMonitorTileEntity = (EnderMonitorTileEntity) tileEntity;
        return new GuiEnderMonitor(enderMonitorTileEntity, new EmptyContainer(entityPlayer));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            int mode = tagCompound.getInteger("mode");
            String smode = EnderMonitorMode.values()[mode].getDescription();
            list.add(EnumChatFormatting.GREEN + "Mode: " + smode);
        }
    }

    @Override
    public String getIdentifyingIconName() {
        return "machineEnderMonitorTop";
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        // We don't want to do what LogicSlabBlock does as we don't react on redstone input.
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new EnderMonitorTileEntity();
    }
}
