/*
 *
 * Rectangle.java
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

package it.zerono.mods.zerocore.lib.data.geometry;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Rectangle {

    public static final Rectangle ZERO = new Rectangle();

    public final Point Origin;
    public final int Width;
    public final int Height;

    public Rectangle(final int x, final int y, final int width, final int height) {

        this.Origin = new Point(x, y);
        this.Width = Math.abs(width);
        this.Height = Math.abs(height);
    }

    public Rectangle(final Point origin, final int width, final int height) {

        this.Origin = new Point(origin);
        this.Width = Math.abs(width);
        this.Height = Math.abs(height);
    }

    public Rectangle(final Rectangle other) {
        this(other.Origin, other.Width, other.Height);
    }

    public static Rectangle syncDataFrom(CompoundNBT data) {

        if (data.contains("rx") && data.contains("ry") && data.contains("rw") && data.contains("rh")) {
            return new Rectangle(data.getInt("rx"), data.getInt("ry"),
                    data.getInt("rw"), data.getInt("rh"));
        }

        return ZERO;
    }

    public CompoundNBT syncDataTo(CompoundNBT data) {

        data.putInt("rx", this.Origin.X);
        data.putInt("ry", this.Origin.Y);
        data.putInt("rw", this.Width);
        data.putInt("rh", this.Height);
        return data;
    }

    public int getX1() {
        return this.Origin.X;
    }

    public int getY1() {
        return this.Origin.Y;
    }

    public int getX2() {
        return this.Origin.X + this.Width - 1;
    }

    public int getY2() {
        return this.Origin.Y + this.Height - 1;
    }

    public Direction.Plane getLayout() {

        if (null == this._layout) {
            this._layout = this.Width >= this.Height ? Direction.Plane.HORIZONTAL : Direction.Plane.VERTICAL;
        }

        return this._layout;
    }

    public Rectangle offset(final int offsetX, final int offsetY) {
        return new Rectangle(this.Origin.X + offsetX, this.Origin.Y + offsetY, this.Width, this.Height);
    }

    public Rectangle expand(final int deltaX, final int deltaY) {

        if (0 == deltaX && 0 == deltaY) {
            return this;
        }

        int x = this.Origin.X;
        int y = this.Origin.Y;
        int width = this.Width;
        int height = this.Height;

        if (deltaX > 0) {

            width += deltaX;

        } else {

            x += deltaX;
            width -= deltaX;
        }

        if (deltaY > 0) {

            height += deltaY;

        } else {

            y += deltaY;
            height -= deltaY;
        }

        return new Rectangle(x, y, width, height);
    }

    public Rectangle inset(final int horizontal, final int vertical) {

        return new Rectangle(this.Origin.X + horizontal, this.Origin.Y + vertical,
                Math.max(0, this.Width - horizontal * 2), Math.max(0, this.Height - vertical * 2));
    }

    public Rectangle wrap(final int x, final int y) {

        Rectangle r = this;

        if (x < r.Origin.X) {
            r = r.expand(x - r.Origin.X, 0);
        } else if (x >= r.Origin.X + r.Width) {
            r = r.expand(x - r.Origin.X - r.Width + 1, 0);
        }

        if (y < r.Origin.Y) {
            r = r.expand(0, y - r.Origin.Y);
        } else if (y >= r.Origin.Y + r.Height) {
            r = r.expand(0, y - r.Origin.Y - r.Height + 1);
        }

        return r;
    }

    public Rectangle wrap(final Rectangle other) {
        return this
                .wrap(other.getX1(), other.getY1())
                .wrap(other.getX2(), other.getY2());
    }

    public Rectangle fit(final Rectangle other) {

        int x = this.Origin.X;
        int y = this.Origin.Y;
        int w = this.Width;
        int h = this.Height;

        if (w > other.Width) {

            w = other.Width;
            x = other.Origin.X;

        } else if (this.getX2() > other.getX2()) {

            x -= this.getX2() - other.getX2();

        } else if (this.getX1() < other.getX1()) {

            x = other.getX1();
        }

        if (h > other.Height) {

            h = other.Height;
            y = other.Origin.Y;

        } else if (this.getY2() > other.getY2()) {

            y -= this.getY2() - other.getY2();

        } else if (this.getY1() < other.getY1()) {

            y = other.getY1();
        }

        return new Rectangle(x, y, w, h);
    }

    public boolean contains(final int x, final int y) {
        return this.getX1() <= x && x <= this.getX2() && this.getY1() <= y && y <= this.getY2();
    }

    public boolean contains(final Point p) {
        return p.collinear(Direction.Axis.X, this.getX1(), this.getX2()) &&
                p.collinear(Direction.Axis.Y, this.getY1(), this.getY2());
    }

    public boolean intersects(final Rectangle other) {
        return other.Origin.X + other.Width > this.Origin.X && other.Origin.X < this.Origin.X + this.Width &&
                other.Origin.Y + other.Height > this.Origin.Y && other.Origin.Y < this.Origin.Y + this.Height;
    }

    //region Object

    @Override
    public boolean equals(Object other) {

        if (other instanceof Rectangle) {

            final Rectangle r = (Rectangle)other;

            return this.Origin.equals(r.Origin) && this.Width == r.Width && this.Height == r.Height;
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("Rectangle (%d, %d) [%d x %d]", this.Origin.X, this.Origin.Y, this.Width, this.Height);
    }

    //endregion
    //region internals

    private Rectangle() {

        this.Origin = Point.ZERO;
        this.Width = this.Height = 0;
    }

    private Direction.Plane _layout;

    //endregion
}
