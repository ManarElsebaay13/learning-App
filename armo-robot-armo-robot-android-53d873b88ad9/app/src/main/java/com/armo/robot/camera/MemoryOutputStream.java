/* Copyright 2013 Foxdog Studios Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.armo.robot.camera;

import java.io.IOException;
import java.io.OutputStream;

final class MemoryOutputStream extends OutputStream {
    private final byte[] mBuffer;
    private int mLength = 0;

    MemoryOutputStream(final int size) {
        this(new byte[size]);
    } // constructor(int)

    MemoryOutputStream(final byte[] buffer) {
        super();
        mBuffer = buffer;
    }

    @Override
    public void write(final byte[] buffer, final int offset, final int count)
            throws IOException {
        checkSpace(count);
        System.arraycopy(buffer, offset, mBuffer, mLength, count);
        mLength += count;
    }

    @Override
    public void write(final byte[] buffer) throws IOException {
        checkSpace(buffer.length);
        System.arraycopy(buffer, 0, mBuffer, mLength, buffer.length);
        mLength += buffer.length;
    }

    @Override
    public void write(final int oneByte) throws IOException {
        checkSpace(1);
        mBuffer[mLength++] = (byte) oneByte;
    }

    private void checkSpace(final int length) throws IOException {
        if (mLength + length >= mBuffer.length) {
            throw new IOException("insufficient space in buffer");
        }
    }

    void seek(final int index) {
        mLength = index;
    } // seek(int)


    byte[] getBuffer() {
        return mBuffer;
    }

    int getLength() {
        return mLength;
    }


}


