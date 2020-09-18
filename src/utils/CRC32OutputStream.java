package utils;

import java.io.OutputStream;
import java.util.zip.CRC32;

/**
 * @author Jeremy Cloud
 * @version 1.0.0
 */
public class CRC32OutputStream extends OutputStream
{
	private CRC32 crc;

	public CRC32OutputStream() {
		crc = new CRC32();
	}

	public void write(int new_byte) {
		crc.update(new_byte);
	}

	public long getValue() {
		return crc.getValue();
	}
}
