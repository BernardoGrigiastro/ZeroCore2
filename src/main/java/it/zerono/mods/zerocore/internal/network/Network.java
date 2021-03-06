/*
 *
 * Network.java
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

package it.zerono.mods.zerocore.internal.network;

import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.lib.network.ModSyncableTileMessage;
import it.zerono.mods.zerocore.lib.network.NetworkHandler;

public final class Network {

    public static final NetworkHandler HANDLER;

    public static void initialize() {

        HANDLER.registerMessage(TileCommandMessage.class, TileCommandMessage::new);
        HANDLER.registerMessage(ModSyncableTileMessage.class, ModSyncableTileMessage::new);
        HANDLER.registerMessage(ErrorReportMessage.class, ErrorReportMessage::new);
    }

    static {
        HANDLER = new NetworkHandler(ZeroCore.newID("network"), "1");
    }
}
