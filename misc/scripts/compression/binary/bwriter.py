# The MIT License (MIT)
#
# Copyright (c) 2015 Ignacio Roldán Etcheverry
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.


class BinaryWriter:
    """
    A sequential byte buffer writer.
    """

    def __init__(self, byteorder='big'):
        """
        Keyword arguments:
        byteorder -- endianness used to read from the array. (default 'big')
        """
        self.data = bytearray()
        self.byteorder = byteorder

    def array(self):
        """
        Return the byte array of this buffer.
        """
        return self.data

    def size(self):
        """
        Returns the size of the array.
        """
        return len(self.data)

    def write_bool(self, value, length=1):
        """
        Appends the specified 8-bit bool.

        Keyword arguments:
        value -- the bool to write
        length -- the number of bytes to write
        """
        self.data.extend(value.to_bytes(length, self.byteorder))

    def write_byte(self, value):
        """
        Appends the specified 8-bit byte.

        Keyword arguments:
        value -- the byte to write
        """
        self.data.extend(value.to_bytes(1, self.byteorder))

    def write_short(self, value):
        """
        Appends the specified 16-bit integer.

        Keyword arguments:
        value -- the int to write
        """
        self.data.extend(value.to_bytes(2, self.byteorder))

    def write_int(self, value):
        """
        Appends the specified 32-bit integer.

        Keyword arguments:
        value -- the int to write
        """
        self.data.extend(value.to_bytes(4, self.byteorder))

    def write_string(self, value, encoding='utf-8'):
        """
        Appends the specified string represented by the specified encoding.

        Keyword arguments:
        value -- the string to write.
        encoding -- the encoding used to write de string (default 'utf-8')

        Returns:
        The number of bytes written
        """
        data = bytes(value, encoding)
        self.data.extend(data)

        return len(data)

    def write_string_fixed(self, value, length, encoding='utf-8'):
        """
        Appends the specified string represented by the specified encoding.
        The string is limited by the length specified. If the string is
        shorter than the limit, the remain space is filled with NUL zeros.

        Keyword arguments:
        value -- the string to write.
        length -- the length of bytes to write
        encoding -- the encoding used to write de string (default 'utf-8')
        """
        data = bytes(value, encoding)
        self.data.extend(data[0:length])

        if len(data) < length:
            self.write_zeros(length - len(data))

    def write_bytes(self, value):
        """
        Appends the specified byte array.

        Keyword arguments:
        value -- the byte array to write
        """
        self.data.extend(value)

    def write_zeros(self, number):
        """
        Fill the buffer with NUL zeros from the current index
        and increases index by the specified number.
        """
        self.data.extend(bytes(number))
