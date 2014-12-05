package com.mcjty.rftools.blocks.shield;

public class ShieldBlockNOpaquePass0NN extends AbstractShieldBlock {

    public ShieldBlockNOpaquePass0NN() {
        super();
        setBlockName("shieldBlockNOpaquePass0NN");
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
}
