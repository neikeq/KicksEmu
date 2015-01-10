import zlib


class Compressor:
    header = [
        0x17, 0xA7, 0x01, 0x00, 0x58, 0x14, 0x01, 0x00, 0x39, 0x15, 0x00,
        0x00, 0x40, 0x5C, 0x3A, 0x32, 0x47, 0x21, 0x3F, 0x33, 0x39, 0x51,
        0x40, 0x56, 0x40, 0x4A, 0x40, 0x07, 0x38, 0x30, 0x49, 0x0C, 0x3A,
        0x4E, 0x42, 0x45, 0x40, 0x56, 0x40, 0x4A, 0x40, 0x07, 0x38, 0x30,
        0x49, 0x0C, 0xFF, 0xFF
    ]

    def decompress(self, data):
        return zlib.decompress(data[len(self.header):len(data)])

    def compress(self, data):
        result = zlib.compress(data)
        return self.add_header(result)

    def decompress_file(self, path):
        data = open(path, 'rb').read()
        return self.decompress(data)

    def compress_file(self, path):
        data = open(path, 'rb').read()
        return self.compress(data)

    def add_header(self, data):
        result = self.header
        result[len(self.header):len(data)+len(self.header)] = data
        return bytes(result)
