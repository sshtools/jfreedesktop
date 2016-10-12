/**
 * SSHTOOLS Limited licenses this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.freedesktop.mime;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.RandomAccessContent;
import org.apache.commons.vfs2.util.RandomAccessMode;
import org.freedesktop.FreedesktopEntity;
import org.freedesktop.mime.MagicEntry.Pattern;

@SuppressWarnings("serial")
public class MagicEntry extends ArrayList<Pattern> implements
		FreedesktopEntity, Comparable<MagicEntry> {

	public static class Pattern implements Comparable<Pattern> {
		private int indent = 0;
		private long offset;
		private byte[] value;
		private byte[] mask;
		private int wordSize = 1;
		private int rangeLength = 1;
		private int valueLength;

		public int getIndent() {
			return indent;
		}

		public void setIndent(int indent) {
			this.indent = indent;
		}

		public long getOffset() {
			return offset;
		}

		public void setOffset(long offset) {
			this.offset = offset;
		}

		public byte[] getValue() {
			return value;
		}

		public void setValue(byte[] value) {
			this.value = value;
		}

		public byte[] getMask() {
			return mask;
		}

		public void setMask(byte[] mask) {
			this.mask = mask;
		}

		public int getWordSize() {
			return wordSize;
		}

		public void setWordSize(int wordSize) {
			this.wordSize = wordSize;
		}

		public int getRangeLength() {
			return rangeLength;
		}

		public void setRangeLength(int rangeLength) {
			this.rangeLength = rangeLength;
		}

		public int compareTo(Pattern o) {
			return Integer.valueOf(indent).compareTo(o.indent);
		}

		@Override
		public String toString() {
			return "Pattern [valueLength=" + valueLength + ", value=" + debugValue(getValue()) + ", mask="
					+ debugValue(getMask()) + ", indent=" + indent + ", offset="
					+ offset + ", wordSize=" + wordSize + ", rangeLength="
					+ rangeLength + "]";
		}
		
		public int getValueLength() {
			return valueLength;
		}

		public void setValueLength(int valueLength) {
			this.valueLength = valueLength;
			value = new byte[valueLength];
			mask = new byte[valueLength];
			for (int in = 0; in < mask.length; in++) {
				mask[in] = (byte) 0xff;
			}
		}
	}

	static String debugValue(byte[] b) {
		StringBuilder bui = new StringBuilder("[");
		for (byte a : b) {
			if (bui.length() > 1) {
				bui.append(",");
			}
			bui.append(String.format("%02x", a));
		}
		bui.append("]");
		return bui.toString();
	}

	private String mimeType;
	private int priority;

	public MagicEntry(String mimeType, int priority) {
		this.mimeType = mimeType;
		this.priority = priority;
	}

	public MagicEntry() {
	}

	public void setMIMEType(String mimeType) {
		this.mimeType = mimeType;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getInternalName() {
		return mimeType;
	}

	public boolean match(FileObject file) throws IOException {
		RandomAccessContent s = file.getContent().getRandomAccessContent(
				RandomAccessMode.READ);
		try {
			for (Pattern p : this) {

				byte[] val = p.getValue();
				byte[] mask = p.getMask();

				// Read in portion of file
				ByteBuffer buf = ByteBuffer.allocate(p.getRangeLength()
						+ val.length);
				s.seek(p.getOffset());
				byte[] bufArr = buf.array();
				s.readFully(bufArr);

				int valIdx = 0;
				for (int i = 0; i < bufArr.length; i++) {
					if ((bufArr[i] & mask[valIdx]) == (val[valIdx] & mask[valIdx])) {
						valIdx++;
						if (valIdx == val.length) {
							return true;
						}
					} else {
						valIdx = 0;
					}
				}
			}
		} finally {
			s.close();
		}
		return false;
	}

	@Override
	public String toString() {
		return "MagicEntry [mimeType=" + mimeType + ", priority=" + priority
				+ ", toString()=" + super.toString() + "]";
	}

	public int compareTo(MagicEntry o) {
		int i = mimeType.compareTo(o.mimeType);
		return i == 0 ? Integer.valueOf(priority).compareTo(o.priority) : i;
	}
}