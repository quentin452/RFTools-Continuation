package mcjty.rftools.blocks.dimletconstruction;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.rftools.RFTools;
import mcjty.rftools.blocks.GenericRFToolsBlock;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class BiomeAbsorberBlock extends GenericRFToolsBlock {

    public BiomeAbsorberBlock() {
        super(Material.iron, BiomeAbsorberTileEntity.class, false);
        setBlockName("biomeAbsorberBlock");
        setCreativeTab(RFTools.tabRfTools);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        NBTTagCompound tagCompound = accessor.getNBTData();
        if (tagCompound != null) {
            int biomeID = tagCompound.getInteger("biome");
            if (biomeID != -1) {
                BiomeGenBase biome = BiomeGenBase.getBiome(biomeID);
                if (biome != null) {
                    int absorbing = tagCompound.getInteger("absorbing");
                    int pct = ((DimletConstructionConfiguration.maxBiomeAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxBiomeAbsorbtion;
                    currenttip.add(EnumChatFormatting.GREEN + "Biome: " + biome.biomeName + " (" + pct + "%)");
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
            int biomeID = tagCompound.getInteger("biome");
            if (biomeID != -1) {
                BiomeGenBase biome = BiomeGenBase.getBiome(biomeID);
                if (biome != null) {
                    list.add(EnumChatFormatting.GREEN + "Biome: " + biome.biomeName);
                    int absorbing = tagCompound.getInteger("absorbing");
                    int pct = ((DimletConstructionConfiguration.maxBiomeAbsorbtion - absorbing) * 100) / DimletConstructionConfiguration.maxBiomeAbsorbtion;
                    list.add(EnumChatFormatting.GREEN + "Absorbed: " + pct + "%");
                }
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(EnumChatFormatting.WHITE + "Place this block in an area and it will");
            list.add(EnumChatFormatting.WHITE + "gradually absorb the essence of the biome it is in.");
            list.add(EnumChatFormatting.WHITE + "You can use the end result in the Dimlet Workbench.");
        } else {
            list.add(EnumChatFormatting.WHITE + RFTools.SHIFT_MESSAGE);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {
        // We don't want what GenericBlock does.
        restoreBlockFromNBT(world, x, y, z, itemStack);
        if (!world.isRemote) {
            BiomeAbsorberTileEntity biomeAbsorberTileEntity = (BiomeAbsorberTileEntity) world.getTileEntity(x, y, z);
            biomeAbsorberTileEntity.placeDown();
        }
        setOwner(world, x, y, z, entityLivingBase);
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        iconSide = iconRegister.registerIcon(RFTools.MODID + ":" + getSideIconName());
    }

    @Override
    public String getSideIconName() {
        return "biomeAbsorber";
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
        return new BiomeAbsorberTileEntity();
    }
}
