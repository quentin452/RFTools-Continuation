package mcjty.rftools.blocks.dimlets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.rftools.RFTools;
import mcjty.rftools.blocks.GenericRFToolsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class EnergyExtractorBlock extends GenericRFToolsBlock {

    public EnergyExtractorBlock() {
        super(Material.iron, EnergyExtractorTileEntity.class, false);
        setBlockName("energyExtractorBlock");
        setCreativeTab(RFTools.tabRfTools);
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    public String getIdentifyingIconName() {
        return "machineEnergyExtractor";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(EnumChatFormatting.WHITE + "This device can be used to extract energy out of");
            list.add(EnumChatFormatting.WHITE + "the current dimension. Be careful with this as");
            list.add(EnumChatFormatting.WHITE + "the dimension needs that energy too!");
            list.add(EnumChatFormatting.WHITE + "This device only works in RFTools dimensions.");
        } else {
            list.add(EnumChatFormatting.WHITE + RFTools.SHIFT_MESSAGE);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new EnergyExtractorTileEntity();
    }
}
