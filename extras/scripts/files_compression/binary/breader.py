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


class BinaryReader:
    """
    A sequential byte buffer reader.
    """

    def __init__(self, data, byteorder='big'):
        """
        Keyword arguments:
        data -- the byte array to read from
        byteorder -- endianness used to read from the array. (default 'big')
        """
        self.data = data
        self.order = byteorder
        self.index = 0

    def set_index(self, index):
        """
        Set the index of this buffer.
        """
        self.index = index

    def readable_bytes(self):
        """
        Returns the number of readable bytes.
        The result equals to (len(self.data) - self.index).
        """
        return len(self.data) - self.index

    def read_bool(self):
        """
        Returns True if the byte at the current index is not equal to zero,
        otherwise returns False. Increases index by 1.
        """
        value = self.data[self.index] != 0
        self.index += 1
        return value

    def read_byte(self):
        """
        Returns the byte at the current index and increases index by 1.
        """
        value = self.data[self.index]
        self.index += 1
        return value

    def read_short(self):
        """
        Returns the 16-bit int at the current index and increases index by 2.
        """
        data = self.data[self.index:self.index + 2]
        self.index += 2
        return int.from_bytes(data, byteorder=self.order)

    def read_int(self):
        """
        Returns the 32-bit int at the current index and increases index by 4.
        """
        data = self.data[self.index:self.index + 4]
        self.index += 4
        return int.from_bytes(data, byteorder=self.order)

    def read_string(self, length, encoding='utf-8'):
        """
        Returns the decoded string at the current index
        and increases index by length.

        Keyword arguments:
        length -- the number of bytes to decode
        encoding -- the encoding used to decode the bytes (default 'utf-8')
        """
        value = self.data[self.index:self.index + length].decode(encoding)
        self.index += length
        return value

    def ignore_bytes(self, number):
        """
        Increases the index by the specified number of bytes to ignore.
        """
        self.index += number
