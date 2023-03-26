package com.hoddmimes.distributor.api;

import java.util.LinkedList;
import java.util.List;

class RcvSegmentBatch {
	List<RcvSegment> mList;

	RcvSegmentBatch(RcvSegment pFirstSegmentInBatch) {
		mList = new LinkedList<RcvSegment>();
		mList.add(pFirstSegmentInBatch);
	}

	void addSegment(RcvSegment pSegment) {
		mList.add(pSegment);
	}

	RcvUpdate[] getUpdates( long pDistributorConnectionId ) {
		NetMsgUpdate tMsg = new NetMsgUpdate(mList.get(0));
		tMsg.decode();
		
		if (mList.size() == 1) {
			return tMsg.getUpdates(pDistributorConnectionId);
		} else {
			tMsg.readLargeDataHeader();
			
			RcvUpdate[] tUpdates = new RcvUpdate[1];
			
			String tSubjectName = tMsg.getLargeSubjectName();
			int tSize = tMsg.getLargeDataSize();
			
			byte[] tBuffer = new byte[tSize];
			int tOffset = 0;

			tOffset = tMsg.getLargeData(tBuffer, tOffset);
			for (int i = 1; i < mList.size(); i++) {
				tMsg = new NetMsgUpdate(mList.get(i));
				tOffset = tMsg.getLargeData(tBuffer, tOffset);
			}

			tUpdates[0] = new RcvUpdate(pDistributorConnectionId, tSubjectName, tBuffer, tMsg.getHeaderAppId(), tMsg.isMsgFromBdxGwy());
			return tUpdates;
		}
	}
}
