/*
 * Copyright (c) 2017-2018 Aion foundation.
 *
 *     This file is part of the aion network project.
 *
 *     The aion network project is free software: you can redistribute it
 *     and/or modify it under the terms of the GNU General Public License
 *     as published by the Free Software Foundation, either version 3 of
 *     the License, or any later version.
 *
 *     The aion network project is distributed in the hope that it will
 *     be useful, but WITHOUT ANY WARRANTY; without even the implied
 *     warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *     See the GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with the aion network project source files.
 *     If not, see <https://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Aion foundation.
 */
package org.aion.p2p.impl1.tasks;

import static org.aion.p2p.impl1.P2pMgr.p2pLOG;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.aion.p2p.Handler;

public class TaskReceive implements Runnable {

    private final AtomicBoolean start;
    private final BlockingQueue<MsgIn> receiveMsgQue;
    private final Map<Integer, List<Handler>> handlers;

    public TaskReceive(final AtomicBoolean _start,
        final BlockingQueue<MsgIn> _receiveMsgQue,
        final Map<Integer, List<Handler>> _handlers) {
        this.start = _start;
        this.receiveMsgQue = _receiveMsgQue;
        this.handlers = _handlers;
    }

    @Override
    public void run() {
        while (this.start.get()) {
            try {
                MsgIn mi = this.receiveMsgQue.take();

                List<Handler> hs = this.handlers.get(mi.getRoute());
                if (hs == null) {
                    continue;
                }
                for (Handler hlr : hs) {
                    if (hlr == null) {
                        continue;
                    }

                    try {
                        hlr.receive(mi.getNodeId(), mi.getDisplayId(), mi.getMsg());
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (p2pLOG.isDebugEnabled()) {
                            p2pLOG.debug("TaskReceive exception {}", e.getMessage());
                        }
                    }
                }
            } catch (InterruptedException e) {
                p2pLOG.error("TaskReceive interrupted {}", e.getMessage());
                return;
            } catch (Exception e) {
                e.printStackTrace();
                if (p2pLOG.isDebugEnabled()) {
                    p2pLOG.debug("TaskReceive exception {}", e.getMessage());
                }
            }
        }
    }
}
