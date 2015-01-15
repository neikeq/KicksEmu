import os
import os.path
import sys
from compression import Compressor
from zlib import error


def main(argv):
    if len(argv) > 1:
        analyze_all(argv[1], argv[0] == '-c')
    else:
        analyze_all(argv[0])


def analyze_all(path, compress=False):
    if not path.endswith('/'):
        path += '/'

    if not os.path.isdir(path):
        print('Warning: The path given is not a valid directory.')

    c = Compressor()

    for root, directory, files in os.walk(path):
        for filename in files:
            if filename.endswith(('.koa', '.koc', '.kom')):
                # create directory to save the result
                dirs = root.replace(path, '', 1)
                if not os.path.exists(dirs):
                    os.makedirs(dirs)

                # get the full path to the file
                file = os.path.join(root, filename)

                try:
                    # change the extension of the new file
                    ext = '.nif' if extension(file) != '.koc' else ''
                    out = replace_extension(file, ext).replace(path, '', 1)

                    # compress/decompress the file and save the result
                    result = c.compress_file(file) if compress \
                        else c.decompress_file(file)
                    f = open(out, 'wb')
                    f.write(result)
                    f.close()

                    print('Decompressed:', out)
                except error as e:
                    print('zlib.error for file:', file)
                    print('Message:', e)
                except IOError as ioe:
                    print('IOError:', ioe)


def replace_extension(name, ext):
    return replace_last(name, extension(name), ext, 1)


def extension(name):
    return os.path.splitext(name)[1]


def replace_last(s, old, new, count):
    li = s.rsplit(old, count)
    return new.join(li)


def print_help():
    print('Usage:')
    print('python3 auto.py <input>')
    print(' -c specifies that the file must be compressed instead of decompressed')

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print_help()
        sys.exit(2)

    main(sys.argv[1:])
