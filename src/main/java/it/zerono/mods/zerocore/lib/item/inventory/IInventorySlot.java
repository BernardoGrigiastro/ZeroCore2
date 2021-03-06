/*
 *
 * IInventorySlot.java
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

package it.zerono.mods.zerocore.lib.item.inventory;

import net.minecraft.item.ItemStack;

public interface IInventorySlot {

    /**
     Get this slot index in the parent inventory
     */
    int getIndex();

    /**
     * Returns the ItemStack in the slot.
     *
     * The result's stack size may be greater than the itemstacks max size.
     *
     * If the result is ItemStack.EMPTY, then the slot is empty.
     * If the result is not ItemStack.EMPTY but the stack size is zero, then it represents
     * an empty slot that will only accept a specific itemstack.
     *
     * <p/>
     * IMPORTANT: This ItemStack MUST NOT be modified. This method is not for
     * altering an inventories contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     * <p/>
     * SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK
     *
     * @return ItemStack in given slot.
     **/
    ItemStack getStackInSlot();

    /**
     * Inserts an ItemStack into the slot and return the remainder.
     * The ItemStack should not be modified in this function!
     *
     * @param stack    ItemStack to insert.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty stack).
     *         May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     **/
    ItemStack insertItem(ItemStack stack, boolean simulate);

    /**
     * Extracts an ItemStack from the slot. The returned value must be EMPTY if nothing is extracted,
     * otherwise it's stack size must not be greater than amount or the itemstacks getMaxStackSize().
     *
     * @param amount   Amount to extract (may be greater than the current stacks max limit)
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be EMPTY, if nothing can be extracted
     **/
    ItemStack extractItem(int amount, boolean simulate);

    /**
     * Check if the itemstack can be added to the slot, even partially
     *
     * @param stack     ItemStack to check
     * @return true if the itemstack can be fully or partially added to the slot. False otherwise
     */
    boolean canInsertItem(ItemStack stack);

    /**
     * Check if the given amount of items can be extracted from the slot
     *
     * @param amount    amount to check
     * @return true if the given amount of items can be extracted from the slot. False otherwise
     */
    boolean canExtractItem(int amount);
}
