package nova.core.network;

import nova.core.retention.Data;
import nova.core.retention.Storable;
import nova.core.util.exception.NovaException;

import java.util.stream.IntStream;

/**
 * A packet of data that is writable or readable.
 *
 * @author Calclavia
 */
public interface Packet {

	/**
	 * Writes an arbitrary object, automatically finding the relevant class.
	 *
	 * @param data Object to write
	 * @return This packet
	 */
	default Packet write(Object data) {
		if (data instanceof Boolean) {
			writeBoolean((boolean) data);
		} else if (data instanceof Byte) {
			writeByte((byte) data);
		} else if (data instanceof Short) {
			writeShort((short) data);
		} else if (data instanceof Integer) {
			writeInt((int) data);
		} else if (data instanceof Long) {
			writeLong((long) data);
		} else if (data instanceof Character) {
			writeChar((Character) data);
		} else if (data instanceof Float) {
			writeFloat((float) data);
		} else if (data instanceof Double) {
			writeDouble((double) data);
		} else if (data instanceof String) {
			writeString((String) data);
		} else if (data instanceof Enum) {
			writeEnum((Enum) data);
		} else if (data instanceof Data) {
			writeData((Data) data);
		} else if (data instanceof Storable) {
			writeStorable((Storable) data);
		} else {
			throw new IllegalArgumentException("Packet attempt to write an invalid object: " + data);
		}

		return this;
	}

	default Packet $less$less$less(Object data) {
		return write(data);
	}

	/**
	 * Sets the specified boolean at the current {@code writerIndex}
	 * and increases the {@code writerIndex} by {@code 1} in this buffer.
	 *
	 * @param value Data to write
	 * @return This packet
	 * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 1}
	 */
	Packet writeBoolean(boolean value);

	/**
	 * Sets the specified byte at the current {@code writerIndex}
	 * and increases the {@code writerIndex} by {@code 1} in this buffer.
	 * The 24 high-order bits of the specified value are ignored.
	 *
	 * @param value Data to write
	 * @return This packet
	 * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 1}
	 */
	Packet writeByte(int value);

	/**
	 * Sets the specified 16-bit short integer at the current
	 * {@code writerIndex} and increases the {@code writerIndex} by {@code 2}
	 * in this buffer.  The 16 high-order bits of the specified value are ignored.
	 *
	 * @param value Data to write
	 * @return This packet
	 * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 2}
	 */
	Packet writeShort(int value);

	/**
	 * Sets the specified 32-bit integer at the current {@code writerIndex}
	 * and increases the {@code writerIndex} by {@code 4} in this buffer.
	 *
	 * @param value Data to write
	 * @return This packet
	 * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 4}
	 */
	Packet writeInt(int value);

	/**
	 * Sets the specified 64-bit long integer at the current
	 * {@code writerIndex} and increases the {@code writerIndex} by {@code 8}
	 * in this buffer.
	 *
	 * @param value Data to write
	 * @return This packet
	 * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 8}
	 */
	Packet writeLong(long value);

	/**
	 * Sets the specified 2-byte UTF-16 character at the current
	 * {@code writerIndex} and increases the {@code writerIndex} by {@code 2}
	 * in this buffer.  The 16 high-order bits of the specified value are ignored.
	 *
	 * @param value Data to write
	 * @return This packet
	 * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 2}
	 */
	Packet writeChar(int value);

	/**
	 * Sets the specified 32-bit floating point number at the current
	 * {@code writerIndex} and increases the {@code writerIndex} by {@code 4}
	 * in this buffer.
	 *
	 * @param value Data to write
	 * @return This packet
	 * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 4}
	 */
	Packet writeFloat(float value);

	/**
	 * Sets the specified 64-bit floating point number at the current
	 * {@code writerIndex} and increases the {@code writerIndex} by {@code 8}
	 * in this buffer.
	 *
	 * @param value Data to write
	 * @return This packet
	 * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 8}
	 */
	Packet writeDouble(double value);

	Packet writeString(String value);

	default Packet writeEnum(Enum data) {
		writeString(data.getClass().getName());
		writeString(data.name());
		return this;
	}

	default Packet writeData(Data data) {
		//Write the data size
		writeInt(data.size());
		//Write the data class
		writeString(data.className);

		data.forEach((k, v) -> {
				int typeID = IntStream.range(0, Data.dataTypes.length)
					.filter(i -> Data.dataTypes[i].isAssignableFrom(v.getClass()))
					.findFirst()
					.getAsInt();

				//Write key
				writeString(k);
				//Write data type
				writeShort(typeID);
				//Write value
				write(v);
			}
		);

		return this;
	}

	default Packet writeStorable(Storable storable) {
		writeData(Data.serialize(storable));
		return this;
	}

	/**
	 * Gets a boolean at the current {@code readerIndex} and increases
	 * the {@code readerIndex} by {@code 1} in this buffer.
	 *
	 * @return Data read from this packet
	 * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 1}
	 */
	boolean readBoolean();

	/**
	 * Gets a byte at the current {@code readerIndex} and increases
	 * the {@code readerIndex} by {@code 1} in this buffer.
	 *
	 * @return Data read from this packet
	 * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 1}
	 */
	byte readByte();

	/**
	 * Gets an unsigned byte at the current {@code readerIndex} and increases
	 * the {@code readerIndex} by {@code 1} in this buffer.
	 *
	 * @return Data read from this packet
	 * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 1}
	 */
	short readUnsignedByte();

	/**
	 * Gets a 16-bit short integer at the current {@code readerIndex}
	 * and increases the {@code readerIndex} by {@code 2} in this buffer.
	 *
	 * @return Data read from this packet
	 * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 2}
	 */
	short readShort();

	/**
	 * Gets a 32-bit integer at the current {@code readerIndex}
	 * and increases the {@code readerIndex} by {@code 4} in this buffer.
	 *
	 * @return Data read from this packet
	 * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 4}
	 */
	int readInt();

	/**
	 * Gets an unsigned 32-bit integer at the current {@code readerIndex}
	 * and increases the {@code readerIndex} by {@code 4} in this buffer.
	 *
	 * @return Data read from this packet
	 * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 4}
	 */
	long readUnsignedInt();

	/**
	 * Gets a 64-bit integer at the current {@code readerIndex}
	 * and increases the {@code readerIndex} by {@code 8} in this buffer.
	 *
	 * @return Data read from this packet
	 * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 8}
	 */
	long readLong();

	/**
	 * Gets a 2-byte UTF-16 character at the current {@code readerIndex}
	 * and increases the {@code readerIndex} by {@code 2} in this buffer.
	 *
	 * @return Data read from this packet
	 * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 2}
	 */
	char readChar();

	/**
	 * Gets a 32-bit floating point number at the current {@code readerIndex}
	 * and increases the {@code readerIndex} by {@code 4} in this buffer.
	 *
	 * @return Data read from this packet
	 * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 4}
	 */
	float readFloat();

	/**
	 * Gets a 64-bit floating point number at the current {@code readerIndex}
	 * and increases the {@code readerIndex} by {@code 8} in this buffer.
	 *
	 * @return Data read from this packet
	 * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 8}
	 */
	double readDouble();

	String readString();

	default Enum readEnum() {
		try {
			Class<? extends Enum> className = (Class) Class.forName(readString());
			return readEnum(className);
		} catch (Exception e) {
			throw new NovaException("Failed to read enum.", e);
		}
	}

	default Enum readEnum(Class<? extends Enum> type) {
		return Enum.valueOf(type, readString());
	}

	/**
	 * Reads a {@link Data} type.
	 */
	default Data readData() {
		Data readData = new Data();
		int size = readInt();
		readData.className = readString();
		IntStream.range(0, size)
			.forEach(i -> {
				String key = readString();
				short type = readShort();
				Object value = read(Data.dataTypes[type]);
				readData.put(key, value);
			});
		return readData;
	}

	default <T extends Storable> T readStorable() {
		Data data = readData();
		return Data.unserialize(data);
	}

	default <T> T read(Class<T> clazz) {
		if (clazz == Boolean.class && clazz == Boolean.TYPE) {
			return (T) Boolean.valueOf(readBoolean());
		} else if (clazz == Byte.class && clazz == Byte.TYPE) {
			return (T) Byte.valueOf(readByte());
		} else if (clazz == Short.class && clazz == Short.TYPE) {
			return (T) Short.valueOf(readShort());
		} else if (clazz == Integer.class && clazz == Integer.TYPE) {
			return (T) Integer.valueOf(readInt());
		} else if (clazz == Long.class && clazz == Long.TYPE) {
			return (T) Long.valueOf(readLong());
		} else if (clazz == Character.class && clazz == Character.TYPE) {
			return (T) Character.valueOf(readChar());
		} else if (clazz == Float.class && clazz == Float.TYPE) {
			return (T) Float.valueOf(readFloat());
		} else if (clazz == Double.class && clazz == Double.TYPE) {
			return (T) Double.valueOf(readDouble());
		} else if (clazz == String.class) {
			return (T) readString();
		}
		//Special data types that all convert into Data.
		else if (clazz == Enum.class) {
			return (T) readEnum((Class) clazz);
		} else if (clazz == Data.class) {
			return (T) readData();
		}

		throw new NovaException("Attempt to read an invalid type");
	}
}
