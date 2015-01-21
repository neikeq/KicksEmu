import zlib
from binary.bwriter import BinaryWriter


class Compressor:

    header_version = 108311

    header = [
        0x40, 0x5C, 0x3A, 0x32, 0x47, 0x21, 0x3F, 0x33, 0x39, 0x51, 0x40, 0x56,
        0x40, 0x4A, 0x40, 0x07, 0x38, 0x30, 0x49, 0x0C, 0x3A, 0x4E, 0x42, 0x45,
        0x40, 0x56, 0x40, 0x4A, 0x40, 0x07, 0x38, 0x30, 0x49, 0x0C, 0xFF, 0xFF
    ]

    def decompress(self, data):
        return zlib.decompress(data[len(self.header):len(data)])

    def compress(self, data):
        result = zlib.compress(data)
        return self.add_header(result, len(data))

    def decompress_file(self, path):
        data = open(path, 'rb').read()
        return self.decompress(data)

    def compress_file(self, path):
        data = open(path, 'rb').read()
        return self.compress(data)

    def add_header(self, data, orig_len):
        result = BinaryWriter('little')
        result.write_int(self.header_version)
        result.write_int(orig_len)
        result.write_int(len(data))
        result.write_byte(self.header)
        result.write_byte(data)
        return result.array()
