package org.aion.zero.impl.sync.msg;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.aion.p2p.Ctrl;
import org.aion.p2p.Msg;
import org.aion.p2p.Ver;
import org.aion.rlp.RLP;
import org.aion.rlp.RLPElement;
import org.aion.rlp.RLPList;
import org.aion.zero.impl.sync.Act;
import org.aion.zero.types.AionTxReceipt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** Response for transaction receipts request */
public class ResTxReceipts extends Msg {
    private final List<AionTxReceipt> txReceipts;

    /**
     * Constructor
     *
     * @param txReceipts list of transaction receipts
     */
    public ResTxReceipts(List<AionTxReceipt> txReceipts) {
        super(Ver.V0, Ctrl.SYNC, Act.RES_TX_RECEIPT_HEADERS);
        this.txReceipts = new LinkedList<>(txReceipts);
    }

    /**
     * Constructor
     *
     * @param msg RLP-encoded representation of a ResTxReceipts (or equivalently, list of transaction
     *            receipts).  Must not be null.
     */
    public ResTxReceipts(byte[] msg) {
        this(decode(msg));
    }

    /**
     * Decode byte array into list of tx receipts (inverse operation of {@link #encode()}
     *
     * @param msgBytes ReqTxReceipts message encoded by {@link #encode()}
     * @return list of transaction hashes of a ReqTxReceipts
     */
    private static List<AionTxReceipt> decode(byte[] msgBytes) {
        Preconditions.checkNotNull(msgBytes, "Cannot decode null message bytes to ResTxReceipts");

        RLPList list = (RLPList) RLP.decode2(msgBytes).get(0);
        List<AionTxReceipt> receipts = new LinkedList<>();

        for (RLPElement elem : list) {
            byte[] elemData = elem.getRLPData();
            receipts.add(new AionTxReceipt(elemData));
        }
        return receipts;
    }

    /** @return the list of transaction receipts */
    public List<AionTxReceipt> getTxReceipts() {
        return Collections.unmodifiableList(txReceipts);
    }

    @Override
    public byte[] encode() {
        List<byte[]> receipts = new ArrayList<>();
        for (AionTxReceipt txr : this.txReceipts) {
            receipts.add(txr.getEncoded());
        }
        byte[][] bytesArray = receipts.toArray(new byte[receipts.size()][]);
        return RLP.encodeList(bytesArray);
    }
}
