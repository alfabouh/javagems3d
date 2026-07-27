/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.system.service.synchronizing;

import org.lwjgl.opengl.GL46;

@Deprecated
public class GPUSyncer {
    private final long sync;

    private GPUSyncer() {
        this.sync = GL46.glFenceSync(GL46.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
    }

    public long getSync() {
        return this.sync;
    }

    public static SyncObj create(long timeout) {
        GPUSyncer gpuSyncer = new GPUSyncer();
        return new SyncObj(timeout, gpuSyncer.getSync());
    }

    public static final class SyncObj implements AutoCloseable {
        private final long sync;
        private final long timeout;

        public SyncObj(long timeout, long sync) {
            this.sync = sync;
            this.timeout = timeout;
        }

        @Override
        public void close() {
            int waitReturn = GL46.GL_UNSIGNALED;
            while (waitReturn != GL46.GL_ALREADY_SIGNALED && waitReturn != GL46.GL_CONDITION_SATISFIED) {
                waitReturn = GL46.glClientWaitSync(this.sync, GL46.GL_SYNC_FLUSH_COMMANDS_BIT, this.timeout);
            }
            GL46.glDeleteSync(this.sync);
        }
    }
}
