package mcjty.rftools.blocks.dimletconstruction;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.rftools.RFTools;
import mcjty.rftools.blocks.GenericRFToolsBlock;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class MaterialAbsorberBlock extends GenericRFToolsBlock {

    public MaterialAbsorberBlock() {
        super(Material.iron, MaterialAbsorberTileEntity.class, false);
        setBlockName("materialAbsorberBlock");
        setCreativeTab(RFTools.tabRfTools);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        NBTTagCompound tagCompound = accessor.getNBTData();
        if (tagCompound != null) {
            int blockID = tagCompound.getInteger("block");
            if (blockID != -1) {
                Block block = (Block) Block.blockRegistry.getObjectById(blockID);
                if (block != null) {
                    int meta = tagCompound.getInteger("meta");
                    int absorbing = tagCompound.getInteger("absorbing");
                    int pct = ((DimletConstructionConfiguration.maxBlockAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxBlockAbsorbtion;
                    currenttip.add(EnumChatFormatting.GREEN + "Block: " + new ItemStack(block, 1, meta).getDisplayName() + " (" + pct + "%)");
                }
            }
        }
        return currenttip;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            int blockID = tagCompound.getInteger("block");
            if (blockID != -1) {
                Block block = (Block) Block.blockRegistry.getObjectById(blockID);
                if (block != null) {
                    int meta = tagCompound.getInteger("meta");
                    list.add(EnumChatFormatting.GREEN + "Block: " + new ItemStack(block, 1, meta).getDisplayName());
                    int absorbing = tagCompound.getInteger("absorbing");
                    int pct = ((DimletConstructionConfiguration.maxBlockAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxBlockAbsorbtion;
                    list.add(EnumChatFormatting.GREEN + "Absorbed: " + pct + "%");
                }
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(EnumChatFormatting.WHITE + "Place this block on top of another block and it will");
            list.add(EnumChatFormatting.WHITE + "gradually absorb all identical blocks in the area.");
            list.add(EnumChatFormatting.WHITE + "You can use the end result in the Dimlet Workbench.");
        } else {
            list.add(EnumChatFormatting.WHITE + RFTools.SHIFT_MESSAGE);
        }
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        iconSide = iconRegister.registerIcon(RFTools.MODID + ":" + getSideIconName());
    }

    @Override
    public String getSideIconName() {
        return "materialAbsorber";
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderBlockPass() {
        return 0;
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new MaterialAbsorberTileEntity();
    }
}
