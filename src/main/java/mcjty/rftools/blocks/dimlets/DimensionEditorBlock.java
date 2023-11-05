package mcjty.rftools.blocks.dimlets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.lib.api.Infusable;
import mcjty.lib.varia.BlockTools;
import mcjty.rftools.RFTools;
import mcjty.rftools.blocks.GenericRFToolsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class DimensionEditorBlock extends GenericRFToolsBlock implements Infusable {

    private IIcon iconFrontEmpty;
    private IIcon iconFrontBusy1;
    private IIcon iconFrontBusy2;

    public DimensionEditorBlock() {
        super(Material.iron, DimensionEditorTileEntity.class, true);
        setBlockName("dimensionEditorBlock");
        setHorizRotation(true);
        setCreativeTab(RFTools.tabRfTools);
    }

    @Override
    public int getGuiID() {
        return RFTools.GUI_DIMENSION_EDITOR;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        super.registerBlockIcons(iconRegister);
        iconFrontEmpty = iconRegister.registerIcon(RFTools.MODID + ":" + "machineDimensionEditor_empty");
        iconFrontBusy1 = iconRegister.registerIcon(RFTools.MODID + ":" + "machineDimensionEditor_busy1");
        iconFrontBusy2 = iconRegister.registerIcon(RFTools.MODID + ":" + "machineDimensionEditor_busy2");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(EnumChatFormatting.WHITE + "This machine allows you to inject certain types");
            list.add(EnumChatFormatting.WHITE + "of dimlets into an existing dimension. This cannot");
            list.add(EnumChatFormatting.WHITE + "be undone and the dimlet is lost so be careful!");
            list.add(EnumChatFormatting.YELLOW + "Infusing bonus: reduced power consumption.");
        } else {
            list.add(EnumChatFormatting.WHITE + RFTools.SHIFT_MESSAGE);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        DimensionEditorTileEntity dimensionEditorTileEntity = (DimensionEditorTileEntity) tileEntity;
        DimensionEditorContainer dimensionEditorContainer = new DimensionEditorContainer(entityPlayer, dimensionEditorTileEntity);
        return new GuiDimensionEditor(dimensionEditorTileEntity, dimensionEditorContainer);
    }

    @Override
    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        return new DimensionEditorContainer(entityPlayer, (DimensionEditorTileEntity) tileEntity);
    }


    @Override
    public String getIdentifyingIconName() {
        return "machineDimensionEditor";
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        int state = BlockTools.getState(meta);
        if (state == 0) {
            return 10;
        } else {
            return getLightValue();
        }
    }

    @Override
    public IIcon getIconInd(IBlockAccess blockAccess, int x, int y, int z, int meta) {
        int state = BlockTools.getState(meta);
        switch (state) {
            case 0: return iconInd;
            case 1: return iconFrontEmpty;
            case 2: return iconFrontBusy1;
            case 3: return iconFrontBusy2;
            default: return iconInd;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new DimensionEditorTileEntity();
    }
}
