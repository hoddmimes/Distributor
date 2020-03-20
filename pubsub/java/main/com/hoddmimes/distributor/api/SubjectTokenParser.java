package com.hoddmimes.distributor.api;

class SubjectTokenParser {
	private static final char TOKEN_DELIMITER = '/';
	private int mPosition, mStackPointer;
	private String[] mStrArr;
	private int[] mPostionStack;

	SubjectTokenParser(String pString) {
		mPosition = 0;
		mStackPointer = 0;
		mPostionStack = new int[12];
		mStrArr = useIndexOf(pString, 0, 0);
	}

	int size() {
		return mStackPointer;
	}

	void savePosition() {
		mPostionStack[mStackPointer++] = mPosition;
	}

	void restorePosition() {
		mPosition = mPostionStack[--mStackPointer];
	}

	boolean lastElement() {
		if ((mPosition + 1) == mStrArr.length) {
			return true;
		} else {
			return false;
		}
	}

	String getNextElement() {
		if (mPosition >= mStrArr.length) {
			return null;
		}
		return mStrArr[mPosition++];
	}

	boolean hasMore() {
		return mPosition < mStrArr.length;
	}

	void setNewToken(String pString) {
		mPosition = 0;
		mStrArr = useIndexOf(pString, 0, 0);
	}

	int getPosition() {
		return mPosition;
	}

	void setPosition(int pPosition) {
		mPosition = pPosition;
	}

	String[] useIndexOf(String pIn, int pCnt, int pPos) {
		// Recursive version...

		String[] tRet;

		int tNextpos = pIn.indexOf(TOKEN_DELIMITER, pPos);

		if (tNextpos != -1) {
			tRet = useIndexOf(pIn, pCnt + 1, tNextpos + 1);
			if (pCnt != 0) {
				tRet[pCnt - 1] = pIn.substring(pPos, tNextpos);
			}
		} else {
			tRet = new String[pCnt];
			tRet[pCnt - 1] = pIn.substring(pPos, pIn.length());
		}

		return tRet;
	}

}