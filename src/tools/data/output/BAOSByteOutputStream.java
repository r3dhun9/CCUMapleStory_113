/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package tools.data.output;

import java.io.ByteArrayOutputStream;

/**
 * 利用一個 byte array 輸出串流的bytes資料.
 *
 * @author Frz
 * @version 1.0
 * @since Revision 352
 */
public class BAOSByteOutputStream implements ByteOutputStream {

    private ByteArrayOutputStream baos;

    /**
     * 類別建構子 - Wraps the stream around a Java BAOS.
     *
     * @param baos <code>The ByteArrayOutputStream</code> to wrap this around.
     */
    public BAOSByteOutputStream(final ByteArrayOutputStream baos) {
        super();
        this.baos = baos;
    }

    /**
     * 寫入一個Byte到串流中
     *
     * @param b 要寫入的byte.
     * @see tools.data.output.ByteOutputStream#writeByte(byte)
     */
    @Override
    public void writeByte(final byte b) {
        baos.write(b);
    }
}
