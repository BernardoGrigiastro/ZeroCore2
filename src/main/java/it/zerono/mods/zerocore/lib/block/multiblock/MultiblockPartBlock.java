/*
 *
 * MultiblockPartBlock.java
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * DO NOT REMOVE OR EDIT THIS HEADER
 *
 */

package it.zerono.mods.zerocore.lib.block.multiblock;

import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.multiblock.validation.ValidationError;
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockVariant;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.Optional;

@SuppressWarnings("WeakerAccess")
public class    MultiblockPartBlock<Controller extends IMultiblockController<Controller>,
                                 PartType extends Enum<PartType> & IMultiblockPartType>
        extends ModBlock {

    public MultiblockPartBlock(final MultiblockPartProperties<PartType> properties) {

        super(properties._blockProperties);
        this._partType = properties._partType;
        this._multiblockVariant = properties._multiblockVariant;
    }

    public PartType getPartType() {
        return this._partType;
    }

    public Optional<IMultiblockVariant> getMultiblockVariant() {
        return Optional.ofNullable(this._multiblockVariant);
    }

    protected boolean openGui(final ServerPlayerEntity player, final AbstractModBlockEntity mbe) {
        return mbe.openGui(player);
    }

    /**
     * Called throughout the code as a replacement for block instanceof BlockContainer
     * Moving this to the Block base class allows for mods that wish to extend vanilla
     * blocks, and also want to have a tile entity on that block, may.
     * <p>
     * Return true from this function to specify this block has a tile entity.
     *
     * @param state State of the current block
     * @return True if block has a tile entity, false otherwise
     */
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    /**
     * Called throughout the code as a replacement for ITileEntityProvider.createNewTileEntity
     * Return the same thing you would from that function.
     * This will fall back to ITileEntityProvider.createNewTileEntity(World) if this block is a ITileEntityProvider
     *
     * @param world the block world
     * @param state the block state
     * @return A instance of a class extending TileEntity
     */
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return this.getPartType().createTileEntity(state, world);
    }

    /**
     * Called when the block is right clicked by a player.
     */
    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos position, PlayerEntity player,
                                             Hand hand, BlockRayTraceResult hit) {

       if (this.hasTileEntity(state) && !player.isSneaking() && CodeHelper.calledByLogicalServer(world)) {

            final Optional<IMultiblockPart<Controller>> part = WorldHelper.getMultiblockPartFrom(world, position);

            // If the player's hands are empty and they right-click on a multiblock, they get a
            // multiblock-debugging message if the machine is not assembled.

            if (part.isPresent()) {

                final ItemStack heldItem = player.getHeldItem(hand);

                if (heldItem.isEmpty() && (hand == Hand.OFF_HAND)) {

                    final Optional<Controller> controller = part.flatMap(IMultiblockPart::getMultiblockController);
                    final ITextComponent message;

                    if (controller.isPresent()) {

                        message = controller
                                .flatMap(IMultiblockController::getLastError)
                                .map(ValidationError::getChatMessage)
                                .orElse(null);

                    } else {

                        message = new TranslationTextComponent("multiblock.validation.block_not_connected");
                    }

                    if (null != message) {

                        CodeHelper.sendStatusMessage(player, message);
                        return ActionResultType.SUCCESS;
                    }
                }

                if (heldItem.isEmpty() && (hand == Hand.MAIN_HAND) &&
                        part.filter(p -> p instanceof INamedContainerProvider && p instanceof AbstractModBlockEntity)
                            .map(p -> (AbstractModBlockEntity)p)
                            .filter(mbe -> mbe.canOpenGui(world, position, state))
                            .map(mbe -> this.openGui((ServerPlayerEntity) player, mbe))
                            .orElse(false)) {
                    return ActionResultType.SUCCESS;
                }
            }
        }

        return ActionResultType.PASS;
    }
/*
    private ActionResultType onPartActivated(BlockState state, World world, BlockPos position, PlayerEntity player,
                                             Hand hand, BlockRayTraceResult hit) {

        if (this.hasTileEntity(state) && !player.isSneaking() && CodeHelper.calledByLogicalServer(world)) {

            final Optional<IMultiblockPart<Controller>> part = this.getMultiblockPart(world, position);

            // If the player's hands are empty and they rightclick on a multiblock, they get a
            // multiblock-debugging message if the machine is not assembled.

            if (part.isPresent()) {

                final ItemStack heldItem = player.getHeldItem(hand);

                if ((ItemHelper.stackIsEmpty(heldItem)) && (hand == Hand.OFF_HAND)) {

                    final Optional<Controller> controller = part.flatMap(IMultiblockPart::getMultiblockController);
                    final ITextComponent message;

                    if (controller.isPresent()) {

                        message = controller
                                .flatMap(IMultiblockController::getLastError)
                                .map(ValidationError::getChatMessage)
                                .orElse(null);

                    } else {

                        message = new TranslationTextComponent("multiblock.validation.block_not_connected");
                    }

                    if (null != message) {

                        CodeHelper.sendStatusMessage(player, message);
                        return ActionResultType.SUCCESS;
                    }
                }

                if (part.filter(p -> p instanceof INamedContainerProvider && p instanceof AbstractModBlockEntity)
                        .map(p -> (AbstractModBlockEntity)p)
                        .filter(mbe -> mbe.canOpenGui(world, position, state))
                        .map(mbe -> this.openGui((ServerPlayerEntity) player, mbe))
                        .orElse(false)) {
                    return ActionResultType.SUCCESS;
                }
            }
        }

        return ActionResultType.PASS;
    }
*/

    public static class MultiblockPartProperties<PartType extends Enum<PartType> & IMultiblockPartType> {

        public static <PartType extends Enum<PartType> & IMultiblockPartType> MultiblockPartProperties<PartType> create(
                final PartType partType, final Block.Properties blockProperties) {
            return new MultiblockPartProperties<>(partType, blockProperties);
        }

        public MultiblockPartProperties<PartType> variant(final IMultiblockVariant variant) {

            this._multiblockVariant = variant;
            return this;
        }

        //region internals

        private MultiblockPartProperties(final PartType partType, final Block.Properties blockProperties) {

            this._blockProperties = blockProperties;
            this._partType = partType;
            this._multiblockVariant = null;
        }

        private final Block.Properties _blockProperties;
        private final PartType _partType;
        private IMultiblockVariant _multiblockVariant;

        //endregion
    }

    //region internals

    private final PartType _partType;
    private final IMultiblockVariant _multiblockVariant;

    //endregion
}
