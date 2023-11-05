package mcjty.rftools.blocks.teleporter;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.lib.varia.Logging;
import mcjty.rftools.RFTools;
import mcjty.rftools.blocks.logic.LogicSlabBlock;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class SimpleDialerBlock extends LogicSlabBlock {
    public SimpleDialerBlock() {
        super(Material.iron, "simpleDialerBlock", SimpleDialerTileEntity.class);
        setCreativeTab(RFTools.tabRfTools);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            if (tagCompound.hasKey("transX")) {
                int transX = tagCompound.getInteger("transX");
                int transY = tagCompound.getInteger("transY");
                int transZ = tagCompound.getInteger("transZ");
                int dim = tagCompound.getInteger("transDim");
                list.add(EnumChatFormatting.GREEN + "Transmitter at: " + transX + "," + transY + "," + transZ + " (dim " + dim + ")");
            }
            if (tagCompound.hasKey("receiver")) {
                int receiver = tagCompound.getInteger("receiver");
                list.add(EnumChatFormatting.GREEN + "Receiver: " + receiver);
            }
            if (tagCompound.getBoolean("once")) {
                list.add(EnumChatFormatting.GREEN + "Dial Once mode enabled");
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(EnumChatFormatting.WHITE + "When this block gets a redstone signal it");
            list.add(EnumChatFormatting.WHITE + "dials or interrupts a transmitter.");
        } else {
            list.add(EnumChatFormatting.WHITE + RFTools.SHIFT_MESSAGE);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        NBTTagCompound tagCompound = accessor.getNBTData();
        if (tagCompound != null) {
            if (tagCompound.hasKey("transX")) {
                int transX = tagCompound.getInteger("transX");
                int transY = tagCompound.getInteger("transY");
                int transZ = tagCompound.getInteger("transZ");
                int dim = tagCompound.getInteger("transDim");
                currenttip.add(EnumChatFormatting.GREEN + "Transmitter at: " + transX + "," + transY + "," + transZ + " (dim " + dim + ")");
            }
            if (tagCompound.hasKey("receiver")) {
                int receiver = tagCompound.getInteger("receiver");
                currenttip.add(EnumChatFormatting.GREEN + "Receiver: " + receiver);
            }
            if (tagCompound.getBoolean("once")) {
                currenttip.add(EnumChatFormatting.GREEN + "Dial Once mode enabled");
            }
        }
        return currenttip;
    }

    @Override
    protected boolean wrenchUse(World world, int x, int y, int z, EntityPlayer player) {
        if (!world.isRemote) {
            SimpleDialerTileEntity simpleDialerTileEntity = (SimpleDialerTileEntity) world.getTileEntity(x, y, z);
            boolean onceMode = !simpleDialerTileEntity.isOnceMode();
            simpleDialerTileEntity.setOnceMode(onceMode);
            if (onceMode) {
                Logging.message(player, "Enabled 'dial once' mode");
            } else {
                Logging.message(player, "Disabled 'dial once' mode");
            }
        }
        return true;
    }



    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        super.onNeighborBlockChange(world, x, y, z, block);
        TileEntity te = world.getTileEntity(x, y, z);
        te.updateEntity();
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    public String getIdentifyingIconName() {
        return "machineSimpleDialer";
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new SimpleDialerTileEntity();
    }
}
