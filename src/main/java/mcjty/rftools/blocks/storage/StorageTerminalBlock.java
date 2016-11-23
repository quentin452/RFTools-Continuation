package mcjty.rftools.blocks.storage;

import mcjty.lib.api.IModuleSupport;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.tools.ChatTools;
import mcjty.lib.tools.ItemStackTools;
import mcjty.lib.varia.ModuleSupport;
import mcjty.rftools.RFTools;
import mcjty.rftools.blocks.logic.generic.LogicSlabBlock;
import mcjty.rftools.blocks.screens.ScreenSetup;
import mcjty.rftools.blocks.storagemonitor.GuiStorageScanner;
import mcjty.rftools.blocks.storagemonitor.StorageScannerContainer;
import mcjty.rftools.blocks.storagemonitor.StorageScannerTileEntity;
import mcjty.rftools.craftinggrid.CraftingGridProvider;
import mcjty.rftools.varia.RFToolsTools;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class StorageTerminalBlock extends LogicSlabBlock<StorageTerminalTileEntity, StorageTerminalContainer> {

    public static final PropertyBool MODULE = PropertyBool.create("module");

    public StorageTerminalBlock() {
        super(Material.IRON, "storage_terminal", StorageTerminalTileEntity.class, StorageTerminalContainer.class, true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return GuiStorageTerminal.class;
    }

    @Override
    protected IModuleSupport getModuleSupport() {
        return new ModuleSupport(StorageTerminalContainer.SLOT_MODULE) {
            @Override
            public boolean isModule(ItemStack itemStack) {
                return itemStack.getItem() == ScreenSetup.storageControlModuleItem;
            }
        };
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "This terminal can be retrofitted with");
            list.add(TextFormatting.WHITE + "a Storage Control Module so that");
            list.add(TextFormatting.WHITE + "you can access a Storage Scanner");
        } else {
            list.add(TextFormatting.WHITE + RFTools.SHIFT_MESSAGE);
        }
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        ItemStack module = getModule(world.getTileEntity(data.getPos()));
        if (ItemStackTools.isEmpty(module)) {
            probeInfo.text(TextFormatting.GREEN + "Install storage control module first");
        } else {
            probeInfo.text(TextFormatting.GREEN + "Use wrench to remove module");
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        ItemStack module = getModule(accessor.getTileEntity());
        if (ItemStackTools.isEmpty(module)) {
            currenttip.add(TextFormatting.GREEN + "Install storage control module first");
        } else {
            currenttip.add(TextFormatting.GREEN + "Use wrench to remove module");
        }
        return currenttip;
    }


    @Override
    public boolean hasRedstoneOutput() {
        return false;
    }

    @Override
    public boolean needsRedstoneCheck() {
        return false;
    }

    @Override
    public int getGuiID() {
        return RFTools.GUI_STORAGE_TERMINAL;
    }

    private static ItemStack getModule(TileEntity tileEntity) {
        if (tileEntity instanceof StorageTerminalTileEntity) {
            StorageTerminalTileEntity terminalTileEntity = (StorageTerminalTileEntity) tileEntity;
            return terminalTileEntity.getStackInSlot(StorageTerminalContainer.SLOT_MODULE);
        }
        return null;
    }

    @Override
    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        if (!entityPlayer.isSneaking()) {
            if (tileEntity instanceof StorageTerminalTileEntity) {
                StorageTerminalTileEntity terminalTileEntity = (StorageTerminalTileEntity) tileEntity;
                ItemStack module = terminalTileEntity.getStackInSlot(StorageTerminalContainer.SLOT_MODULE);
                if (ItemStackTools.isValid(module)) {
                    int dimension = RFToolsTools.getDimensionFromModule(module);
                    BlockPos pos = RFToolsTools.getPositionFromModule(module);
                    WorldServer world = DimensionManager.getWorld(dimension);
                    if (!RFToolsTools.chunkLoaded(world, pos)) {
                        ChatTools.addChatMessage(entityPlayer, new TextComponentString(TextFormatting.YELLOW + "Storage scanner out of range!"));
                        return null;
                    }
                    TileEntity scannerTE = world.getTileEntity(pos);
                    if (!(scannerTE instanceof StorageScannerTileEntity)) {
                        ChatTools.addChatMessage(entityPlayer, new TextComponentString(TextFormatting.YELLOW + "Storage scanner is missing!"));
                        return null;
                    }

                    return new StorageScannerContainer(entityPlayer, (IInventory) scannerTE, terminalTileEntity);
                }
            }
        }
        return super.createServerContainer(entityPlayer, tileEntity);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        if (!entityPlayer.isSneaking()) {
            ItemStack module = getModule(tileEntity);
            if (ItemStackTools.isValid(module)) {
                int monitordim = RFToolsTools.getDimensionFromModule(module);
                BlockPos pos = RFToolsTools.getPositionFromModule(module);
                StorageScannerTileEntity te = new StorageScannerTileEntity(entityPlayer, monitordim) {
                    @Override
                    public BlockPos getCraftingGridContainerPos() {
                        // We are a storage terminal so the position we return here is the one
                        // for this terminal itself.
                        return tileEntity.getPos();
                    }

                    @Override
                    public CraftingGridProvider getCraftingGridProvider() {
                        return (CraftingGridProvider) tileEntity;
                    }

                    @Override
                    public BlockPos getStorageScannerPos() {
                        return pos;
                    }
                };
                // The position of the actual storage scanner is set on the dummy te
                te.setPos(pos);
                return new GuiStorageScanner(te, new StorageScannerContainer(entityPlayer, te, (CraftingGridProvider) tileEntity));
            }
        }
        return super.createClientGui(entityPlayer, tileEntity);
    }

    @Override
    protected boolean wrenchUse(World world, BlockPos pos, EnumFacing side, EntityPlayer player) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof StorageTerminalTileEntity) {
                StorageTerminalTileEntity storageTerminalTileEntity = (StorageTerminalTileEntity) te;
                ItemStack module = storageTerminalTileEntity.getStackInSlot(StorageTerminalContainer.SLOT_MODULE);
                if (ItemStackTools.isValid(module)) {
                    storageTerminalTileEntity.setInventorySlotContents(StorageTerminalContainer.SLOT_MODULE, null);
                    storageTerminalTileEntity.markDirtyClient();
                    if (!player.inventory.addItemStackToInventory(module)) {
                        player.entityDropItem(module, 1.05f);
                    }
                    ChatTools.addChatMessage(player, new TextComponentString("Removed module"));
                }
            }
        }
        return true;
    }

    @Override
    protected boolean openGui(World world, int x, int y, int z, EntityPlayer player) {
        if (world.isRemote) {
            return true;
        } else {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if(isBlockContainer && !tileEntityClass.isInstance(te)) {
                return false;
            } else if(checkAccess(world, player, te)) {
                return true;
            } else {
                if (player.isSneaking()) {
                    player.openGui(this.modBase, this.getGuiID(), world, x, y, z);
                } else {
                    player.openGui(this.modBase, RFTools.GUI_STORAGE_TERMINAL_SCANNER, world, x, y, z);
                }
                return true;
            }
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        ItemStack module = getModule(world.getTileEntity(pos));
        return super.getActualState(state, world, pos).withProperty(MODULE, ItemStackTools.isValid(module));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LOGIC_FACING, META_INTERMEDIATE, OUTPUTPOWER, MODULE);
    }
}
